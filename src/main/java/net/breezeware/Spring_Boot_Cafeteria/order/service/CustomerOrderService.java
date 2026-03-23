package net.breezeware.Spring_Boot_Cafeteria.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.breezeware.Spring_Boot_Cafeteria.food.entity.FoodItem;
import net.breezeware.Spring_Boot_Cafeteria.food.repo.FoodItemRepository;
import net.breezeware.Spring_Boot_Cafeteria.order.dto.CartItemDto;
import net.breezeware.Spring_Boot_Cafeteria.order.dto.OrderDetailDto;
import net.breezeware.Spring_Boot_Cafeteria.order.dto.OrderRequestDto;
import net.breezeware.Spring_Boot_Cafeteria.order.dto.OrderSummaryDetailDto;
import net.breezeware.Spring_Boot_Cafeteria.order.entity.Order;
import net.breezeware.Spring_Boot_Cafeteria.order.entity.OrderItem;
import net.breezeware.Spring_Boot_Cafeteria.order.enumeration.OrderStatus;
import net.breezeware.Spring_Boot_Cafeteria.order.repo.OrderItemRepository;
import net.breezeware.Spring_Boot_Cafeteria.order.repo.OrderRepository;
import net.breezeware.Spring_Boot_Cafeteria.user.entity.DeliveryDetail;
import net.breezeware.Spring_Boot_Cafeteria.user.entity.User;
import net.breezeware.Spring_Boot_Cafeteria.user.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomerOrderService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final FoodItemRepository foodItemRepository;

    // In-memory cart: userId → list of cart items
    private final Map<Long, List<CartItemDto>> cartStore = new ConcurrentHashMap<>();

    // ═══════════════════════════════════════════════════════
    // CART: Add food item to cart
    // ═══════════════════════════════════════════════════════

    public List<CartItemDto> addToCart(Long userId, Long foodItemId, int quantity) {
        log.info("Adding foodItemId: {} to cart for userId: {}", foodItemId, userId);

        FoodItem foodItem = foodItemRepository.findById(foodItemId)
                .orElseThrow(() -> new RuntimeException("Food item not found with id: " + foodItemId));

        if (!foodItem.hasStock(quantity)) {
            throw new RuntimeException("Insufficient stock for item: " + foodItem.getName());
        }

        List<CartItemDto> cart = cartStore.computeIfAbsent(userId, k -> new ArrayList<>());

        // If item already in cart, increase quantity
        for (CartItemDto existing : cart) {
            if (existing.getFoodItemId().equals(foodItemId)) {
                existing.setQuantity(existing.getQuantity() + quantity);
                existing.setTotalPrice(foodItem.getPrice() * existing.getQuantity());
                return cart;
            }
        }

        // New item — add to cart
        cart.add(new CartItemDto(
                foodItem.getId(),
                foodItem.getName(),
                foodItem.getPrice() * quantity,
                (long) quantity
        ));

        return cart;
    }

    // ═══════════════════════════════════════════════════════
    // CART: View cart
    // ═══════════════════════════════════════════════════════

    @Transactional(readOnly = true)
    public List<CartItemDto> viewCart(Long userId) {
        log.info("Viewing cart for userId: {}", userId);
        return cartStore.getOrDefault(userId, new ArrayList<>());
    }

    // ═══════════════════════════════════════════════════════
    // CART: Remove item from cart
    // ═══════════════════════════════════════════════════════

    public List<CartItemDto> removeFromCart(Long userId, Long foodItemId) {
        log.info("Removing foodItemId: {} from cart for userId: {}", foodItemId, userId);
        List<CartItemDto> cart = cartStore.getOrDefault(userId, new ArrayList<>());
        cart.removeIf(item -> item.getFoodItemId().equals(foodItemId));
        return cart;
    }

    // ═══════════════════════════════════════════════════════
    // CART: Checkout — convert cart to order
    // ═══════════════════════════════════════════════════════

    public OrderDetailDto checkout(Long userId) {
        log.info("Checkout for userId: {}", userId);

        List<CartItemDto> cart = cartStore.getOrDefault(userId, new ArrayList<>());
        if (cart.isEmpty()) {
            throw new RuntimeException("Cart is empty for userId: " + userId);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Order order = new Order(user, OrderStatus.PLACED_ORDER);

        for (CartItemDto cartItem : cart) {
            FoodItem foodItem = foodItemRepository.findById(cartItem.getFoodItemId())
                    .orElseThrow(() -> new RuntimeException("Food item not found: " + cartItem.getFoodItemId()));

            int qty = cartItem.getQuantity().intValue();
            if (!foodItem.hasStock(qty)) {
                throw new RuntimeException("Insufficient stock for item: " + foodItem.getName());
            }

            foodItem.reduceStock(qty);
            foodItemRepository.save(foodItem);

            OrderItem orderItem = new OrderItem(order, foodItem, foodItem.getPrice(), qty);
            order.addItem(orderItem);
        }

        Order saved = orderRepository.save(order);

        // Clear cart after successful checkout
        cartStore.remove(userId);

        return mapToDetail(saved);
    }

    // ═══════════════════════════════════════════════════════
    // Place a new order
    // ═══════════════════════════════════════════════════════

public OrderDetailDto placeOrder(OrderRequestDto request) {
        log.info("Customer placing order for userId: {}", request.getUser_id());

        User user = userRepository.findById(request.getUser_id())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUser_id()));

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Order must have at least one item");
        }

        Order order = new Order(user, OrderStatus.PLACED_ORDER);

        for (var itemReq : request.getItems()) {
            FoodItem foodItem = foodItemRepository.findById(itemReq.getId())
                    .orElseThrow(() -> new RuntimeException("Food item not found with id: " + itemReq.getId()));

            int qty = itemReq.getQuantity().intValue();
            if (!foodItem.hasStock(qty)) {
                throw new RuntimeException("Insufficient stock for item: " + foodItem.getName());
            }

            foodItem.reduceStock(qty);
            foodItemRepository.save(foodItem);

            OrderItem orderItem = new OrderItem(order, foodItem, itemReq.getPrice(), qty);
            order.addItem(orderItem);
        }

        Order saved = orderRepository.save(order);
        return mapToDetail(saved);
    }

    // ═══════════════════════════════════════════════════════
    // View all orders for a customer
    // ═══════════════════════════════════════════════════════

    @Transactional(readOnly = true)
    public List<OrderSummaryDetailDto> getMyOrders(Long userId) {
        log.info("Customer fetching orders for userId: {}", userId);
        return orderRepository.findByUserId(userId).stream()
                .map(this::mapToSummary)
                .collect(Collectors.toList());
    }

    // ═══════════════════════════════════════════════════════
    // View order detail (customer can only see their own)
    // ═══════════════════════════════════════════════════════

    @Transactional(readOnly = true)
    public OrderDetailDto getOrderDetail(Long orderId, Long userId) {
        log.info("Customer fetching order detail for orderId: {}, userId: {}", orderId, userId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied: Order does not belong to this user");
        }

        return mapToDetail(order);
    }

    // ═══════════════════════════════════════════════════════
    // Cancel order (only PLACED_ORDER status can be cancelled)
    // ═══════════════════════════════════════════════════════

    public OrderDetailDto cancelOrder(Long orderId, Long userId) {
        log.info("Customer cancelling order: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied: Order does not belong to this user");
        }

        if (!order.canBeCancelled()) {
            throw new RuntimeException("Order cannot be cancelled in status: " + order.getStatus());
        }

        // Restore stock for each cancelled item
        for (OrderItem item : order.getItems()) {
            item.getFoodItem().restoreStock(item.getQuantity());
            foodItemRepository.save(item.getFoodItem());
        }

        order.setStatus(OrderStatus.ORDER_CANCELLED);
        Order updated = orderRepository.save(order);
        return mapToDetail(updated);
    }

    // ═══════════════════════════════════════════════════════
    // Helper Methods
    // ═══════════════════════════════════════════════════════

    private OrderSummaryDetailDto mapToSummary(Order order) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return new OrderSummaryDetailDto(
                order.getId().intValue(),
                order.getUser().getId().intValue(),
                order.getStatus(),
                order.getTotalPrice(),
                order.getCreatedOn() != null ? sdf.format(order.getCreatedOn()) : null
        );
    }

    private OrderDetailDto mapToDetail(Order order) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        List<OrderDetailDto.OrderItemDetailDTO> itemDetails = order.getItems().stream()
                .map(item -> new OrderDetailDto.OrderItemDetailDTO(
                        item.getFoodItem().getName(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getTotalPrice()
                ))
                .collect(Collectors.toList());

        String deliveryEmail = null;
        String deliveryPhone = null;
        String deliveryLocation = null;
        List<DeliveryDetail> deliveryDetails = order.getUser().getDeliveryDetails();
        if (deliveryDetails != null && !deliveryDetails.isEmpty()) {
            DeliveryDetail dd = deliveryDetails.get(0);
            deliveryEmail = dd.getEmail();
            deliveryPhone = dd.getPhoneNumber();
            deliveryLocation = dd.getLocation();
        }

        return new OrderDetailDto(
                order.getId().intValue(),
                order.getUser().getId().intValue(),
                order.getUser().getName(),
                order.getStatus(),
                itemDetails,
                order.getTotalPrice(),
                deliveryEmail,
                deliveryPhone,
                deliveryLocation,
                order.getCreatedOn() != null ? sdf.format(order.getCreatedOn()) : null
        );
    }
}