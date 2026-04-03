package net.breezeware.SpringBootCafeteria.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.breezeware.SpringBootCafeteria.exception.DuplicateResourceException;
import net.breezeware.SpringBootCafeteria.exception.InsufficientStockException;
import net.breezeware.SpringBootCafeteria.exception.InvalidStatusException;
import net.breezeware.SpringBootCafeteria.exception.ResourceNotFoundException;
import net.breezeware.SpringBootCafeteria.exception.UnauthorizedAccessException;
import net.breezeware.SpringBootCafeteria.food.entity.FoodItem;
import net.breezeware.SpringBootCafeteria.food.repo.FoodItemRepository;
import net.breezeware.SpringBootCafeteria.order.dto.CartItemDto;
import net.breezeware.SpringBootCafeteria.order.dto.OrderDeliveryRequest;
import net.breezeware.SpringBootCafeteria.order.dto.OrderDetailDto;
import net.breezeware.SpringBootCafeteria.order.dto.OrderRequestDto;
import net.breezeware.SpringBootCafeteria.order.dto.OrderSummaryDetailDto;
import net.breezeware.SpringBootCafeteria.order.entity.Order;
import net.breezeware.SpringBootCafeteria.order.entity.OrderDeliveryMap;
import net.breezeware.SpringBootCafeteria.order.entity.OrderItem;
import net.breezeware.SpringBootCafeteria.order.enumeration.OrderStatus;
import net.breezeware.SpringBootCafeteria.order.repo.OrderDeliveryMapRepository;
import net.breezeware.SpringBootCafeteria.order.repo.OrderItemRepository;
import net.breezeware.SpringBootCafeteria.order.repo.OrderRepository;
import net.breezeware.SpringBootCafeteria.user.entity.User;
import net.breezeware.SpringBootCafeteria.user.repo.UserRepository;
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
    private final OrderDeliveryMapRepository orderDeliveryMapRepository;

    // In-memory cart: userId → list of cart items
    private final Map<Long, List<CartItemDto>> cartStore = new ConcurrentHashMap<>();

    // ═══════════════════════════════════════════════════════
    // CART: Add food item to cart
    // ═══════════════════════════════════════════════════════

    public List<CartItemDto> addToCart(Long userId, String foodItemName, int quantity) {
        log.info("Adding foodItem: {} to cart for userId: {}", foodItemName, userId);

        FoodItem foodItem = foodItemRepository.findByNameIgnoreCase(foodItemName)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found with name: " + foodItemName));

        if (!foodItem.hasStock(quantity)) {
            throw new InsufficientStockException("Insufficient stock for item: " + foodItem.getName());
        }

        List<CartItemDto> cart = cartStore.computeIfAbsent(userId, k -> new ArrayList<>());

        // If item already in cart, increase quantity
        for (CartItemDto existing : cart) {
            if (existing.getFoodItemName().equalsIgnoreCase(foodItemName)) {
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

    public List<CartItemDto> removeFromCart(Long userId, String foodItemName, int quantity) {
        log.info("Removing {} unit(s) of foodItem: {} from cart for userId: {}", quantity, foodItemName, userId);
        List<CartItemDto> cart = cartStore.getOrDefault(userId, new ArrayList<>());

        CartItemDto item = cart.stream()
                .filter(c -> c.getFoodItemName().equalsIgnoreCase(foodItemName))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found in cart: " + foodItemName));

        long newQuantity = item.getQuantity() - quantity;
        if (newQuantity <= 0) {
            cart.remove(item);
        } else {
            double unitPrice = item.getTotalPrice() / item.getQuantity();
            item.setQuantity(newQuantity);
            item.setTotalPrice(unitPrice * newQuantity);
        }

        return cart;
    }

    // ═══════════════════════════════════════════════════════
    // CART: Checkout — convert cart to order
    // ═══════════════════════════════════════════════════════

    public OrderDetailDto checkout(Long userId, OrderDeliveryRequest deliveryRequest) {
        log.info("Checkout for userId: {}", userId);

        List<CartItemDto> cart = cartStore.getOrDefault(userId, new ArrayList<>());
        if (cart.isEmpty()) {
            throw new ResourceNotFoundException("Cart is empty for userId: " + userId);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Order order = new Order(user, OrderStatus.PLACED_ORDER);

        for (CartItemDto cartItem : cart) {
            FoodItem foodItem = foodItemRepository.findById(cartItem.getFoodItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Food item not found: " + cartItem.getFoodItemId()));

            int qty = cartItem.getQuantity().intValue();
            if (!foodItem.hasStock(qty)) {
                throw new InsufficientStockException("Insufficient stock for item: " + foodItem.getName());
            }

            foodItem.reduceStock(qty);
            foodItemRepository.save(foodItem);

            OrderItem orderItem = new OrderItem(order, foodItem, foodItem.getPrice(), qty);
            order.addItem(orderItem);
        }

        Order saved = orderRepository.save(order);

        OrderDeliveryMap delivery = new OrderDeliveryMap(saved, deliveryRequest.getName(), deliveryRequest.getPhone(), deliveryRequest.getAddress());
        orderDeliveryMapRepository.save(delivery);

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
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUser_id()));

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new InvalidStatusException("Order must have at least one item");
        }

        Order order = new Order(user, OrderStatus.PLACED_ORDER);

        for (var itemReq : request.getItems()) {
            FoodItem foodItem = foodItemRepository.findById(itemReq.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Food item not found with id: " + itemReq.getId()));

            int qty = itemReq.getQuantity().intValue();
            if (!foodItem.hasStock(qty)) {
                throw new InsufficientStockException("Insufficient stock for item: " + foodItem.getName());
            }

            foodItem.reduceStock(qty);
            foodItemRepository.save(foodItem);

            OrderItem orderItem = new OrderItem(order, foodItem, itemReq.getPrice(), qty);
            order.addItem(orderItem);
        }

        Order saved = orderRepository.save(order);

        OrderDeliveryMap delivery = new OrderDeliveryMap(saved, request.getDeliveryName(), request.getDeliveryPhone(), request.getDeliveryAddress());
        orderDeliveryMapRepository.save(delivery);

        return mapToDetail(saved);
    }

    // ═══════════════════════════════════════════════════════
    // View all orders for a customer
    // ═══════════════════════════════════════════════════════

    @Transactional(readOnly = true)
    public List<OrderSummaryDetailDto> getMyOrders(Long userId) {
        log.info("Customer fetching orders for userId: {}", userId);
        List<OrderSummaryDetailDto> myOrders = orderRepository.findByUserId(userId).stream()
                .map(this::mapToSummary)
                .collect(Collectors.toList());
    if(myOrders.isEmpty())
    {
        throw new ResourceNotFoundException("No orders found for user with id: " + userId);
    }
        return myOrders;
    }

    // ═══════════════════════════════════════════════════════
    // View order detail (customer can only see their own)
    // ═══════════════════════════════════════════════════════

    @Transactional(readOnly = true)
    public OrderDetailDto getOrderDetail(Long orderId, Long userId) {
        log.info("Customer fetching order detail for orderId: {}, userId: {}", orderId, userId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!order.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("Access denied: Order does not belong to this user");
        }

        return mapToDetail(order);
    }

    // ═══════════════════════════════════════════════════════
    // Cancel order (only PLACED_ORDER status can be cancelled)
    // ═══════════════════════════════════════════════════════

    public OrderDetailDto cancelOrder(Long orderId, Long userId) {
        log.info("Customer cancelling order: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!order.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("Access denied: Order does not belong to this user");
        }

        if (!order.canBeCancelled()) {
            throw new InvalidStatusException("Order cannot be cancelled in status: " + order.getStatus());
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
    // Delivery Details
    // ═══════════════════════════════════════════════════════

    public OrderDeliveryMap addDeliveryDetails(Long orderId, Long userId, OrderDeliveryRequest request) {
        log.info("Adding delivery details for orderId: {}, userId: {}", orderId, userId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!order.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("Access denied: Order does not belong to this user");
        }

        if (orderDeliveryMapRepository.findByOrderId(orderId).isPresent()) {
            throw new DuplicateResourceException("Delivery details already exist for this order");
        }

        OrderDeliveryMap delivery = new OrderDeliveryMap(order, request.getName(), request.getPhone(), request.getAddress());
        return orderDeliveryMapRepository.save(delivery);
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

        String deliveryName = null;
        String deliveryPhone = null;
        String deliveryAddress = null;
        OrderDeliveryMap deliveryMap = orderDeliveryMapRepository.findByOrderId(order.getId()).orElse(null);
        if (deliveryMap != null) {
            deliveryName = deliveryMap.getName();
            deliveryPhone = deliveryMap.getPhone();
            deliveryAddress = deliveryMap.getAddress();
        }

        return new OrderDetailDto(
                order.getId().longValue(),
                order.getUser().getId().intValue(),
                order.getUser().getName(),
                order.getStatus(),
                itemDetails,
                order.getTotalPrice(),
                deliveryName,
                deliveryPhone,
                deliveryAddress,
                order.getCreatedOn() != null ? sdf.format(order.getCreatedOn()) : null
        );
    }
}