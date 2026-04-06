package net.breezeware.SpringBootCafeteria.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.breezeware.SpringBootCafeteria.exception.AppCustomException;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service class responsible for managing customer-facing order operations
 * in the cafeteria system.
 *
 * <p>This service handles the full customer order lifecycle including cart
 * management (add, view, remove), order placement, checkout with delivery
 * details, order tracking, and cancellation.</p>
 *
 * @author Siva
 * @version 1.0
 * @since 1.0
 */
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

    /**
     * Adds a food item to the customer's in-memory cart.
     *
     * @param userId       the ID of the customer
     * @param foodItemName the name of the food item to add
     * @param quantity     the quantity to add
     * @return updated list of CartItemDto for the user's cart
     *
     * @throws AppCustomException if the food item is not found or stock is insufficient
     *
     * @implNote If the item already exists in the cart, quantity is incremented instead of adding a duplicate.
     */
    public List<CartItemDto> addToCart(Long userId, String foodItemName, int quantity) {
        log.info("Adding foodItem: {} to cart for userId: {}", foodItemName, userId);

        FoodItem foodItem = foodItemRepository.findByNameIgnoreCase(foodItemName)
                .orElseThrow(() -> new AppCustomException("Food item not found with name: " + foodItemName, HttpStatus.NOT_FOUND));

        if (!foodItem.hasStock(quantity)) {
            throw new AppCustomException("Insufficient stock for item: " + foodItem.getName(), HttpStatus.BAD_REQUEST);
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

    /**
     * Returns the current contents of a customer's in-memory cart.
     *
     * @param userId the ID of the customer
     * @return list of CartItemDto currently in the cart; empty list if cart is empty
     */
    @Transactional(readOnly = true)
    public List<CartItemDto> viewCart(Long userId) {
        log.info("Viewing cart for userId: {}", userId);
        return cartStore.getOrDefault(userId, new ArrayList<>());
    }

    // ═══════════════════════════════════════════════════════
    // CART: Remove item from cart
    // ═══════════════════════════════════════════════════════

    /**
     * Removes a specified quantity of a food item from the customer's cart.
     *
     * @param userId       the ID of the customer
     * @param foodItemName the name of the food item to remove
     * @param quantity     the quantity to remove
     * @return updated list of CartItemDto after removal
     *
     * @throws AppCustomException if the food item is not found in the cart
     *
     * @implNote If the resulting quantity is zero or less, the item is fully removed from the cart.
     */
    public List<CartItemDto> removeFromCart(Long userId, String foodItemName, int quantity) {
        log.info("Removing {} unit(s) of foodItem: {} from cart for userId: {}", quantity, foodItemName, userId);
        List<CartItemDto> cart = cartStore.getOrDefault(userId, new ArrayList<>());

        CartItemDto item = cart.stream()
                .filter(c -> c.getFoodItemName().equalsIgnoreCase(foodItemName))
                .findFirst()
                .orElseThrow(() -> new AppCustomException("Food item not found in cart: " + foodItemName, HttpStatus.NOT_FOUND));

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

    /**
     * Converts the customer's cart into a confirmed order with delivery details.
     *
     * @param userId          the ID of the customer checking out
     * @param deliveryRequest the delivery details (name, phone, address)
     * @return OrderDetailDto for the newly created order
     *
     * @throws AppCustomException if the cart is empty, user is not found,
     *                            any cart item is not found, or stock is insufficient
     *
     * @implSpec Stock is reduced for each item during checkout. Cart is cleared on success.
     */
    public OrderDetailDto checkout(Long userId, OrderDeliveryRequest deliveryRequest) {
        log.info("Checkout for userId: {}", userId);

        List<CartItemDto> cart = cartStore.getOrDefault(userId, new ArrayList<>());
        if (cart.isEmpty()) {
            throw new AppCustomException("Cart is empty for userId: " + userId, HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppCustomException("User not found with id: " + userId, HttpStatus.NOT_FOUND));

        Order order = new Order(user, OrderStatus.PLACED_ORDER);

        for (CartItemDto cartItem : cart) {
            FoodItem foodItem = foodItemRepository.findById(cartItem.getFoodItemId())
                    .orElseThrow(() -> new AppCustomException("Food item not found: " + cartItem.getFoodItemId(), HttpStatus.NOT_FOUND));

            int qty = cartItem.getQuantity().intValue();
            if (!foodItem.hasStock(qty)) {
                throw new AppCustomException("Insufficient stock for item: " + foodItem.getName(), HttpStatus.BAD_REQUEST);
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

    /**
     * Places a direct order without using the cart flow.
     *
     * @param request the order request containing user ID, items, and delivery details
     * @return OrderDetailDto for the newly placed order
     *
     * @throws AppCustomException if the user is not found, items list is empty,
     *                            any food item is not found, or stock is insufficient
     *
     * @implSpec Stock is reduced per item at the time of order placement.
     */
    public OrderDetailDto placeOrder(OrderRequestDto request) {
        log.info("Customer placing order for userId: {}", request.getUser_id());

        User user = userRepository.findById(request.getUser_id())
                .orElseThrow(() -> new AppCustomException("User not found with id: " + request.getUser_id(), HttpStatus.NOT_FOUND));

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new AppCustomException("Order must have at least one item", HttpStatus.BAD_REQUEST);
        }

        Order order = new Order(user, OrderStatus.PLACED_ORDER);

        for (var itemReq : request.getItems()) {
            FoodItem foodItem = foodItemRepository.findById(itemReq.getId())
                    .orElseThrow(() -> new AppCustomException("Food item not found with id: " + itemReq.getId(), HttpStatus.NOT_FOUND));

            int qty = itemReq.getQuantity().intValue();
            if (!foodItem.hasStock(qty)) {
                throw new AppCustomException("Insufficient stock for item: " + foodItem.getName(), HttpStatus.BAD_REQUEST);
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

    /**
     * Retrieves all orders placed by the authenticated customer.
     *
     * @param userId the ID of the customer
     * @return list of OrderSummaryDetailDto for the customer's orders
     *
     * @throws AppCustomException if no orders are found for the user
     */
    @Transactional(readOnly = true)
    public List<OrderSummaryDetailDto> getMyOrders(Long userId) {
        log.info("Customer fetching orders for userId: {}", userId);
        List<OrderSummaryDetailDto> myOrders = orderRepository.findByUserId(userId).stream()
                .map(this::mapToSummary)
                .collect(Collectors.toList());

        if (myOrders.isEmpty()) {
            throw new AppCustomException("No orders found for user with id: " + userId, HttpStatus.NOT_FOUND);
        }
        return myOrders;
    }

    // ═══════════════════════════════════════════════════════
    // View order detail (customer can only see their own)
    // ═══════════════════════════════════════════════════════

    /**
     * Retrieves full details of a specific order belonging to the customer.
     *
     * @param orderId the ID of the order to retrieve
     * @param userId  the ID of the customer requesting the details
     * @return OrderDetailDto for the specified order
     *
     * @throws AppCustomException if the order is not found or does not belong to the user
     *
     * @apiNote Returns HTTP 403 if the order belongs to a different user.
     */
    @Transactional(readOnly = true)
    public OrderDetailDto getOrderDetail(Long orderId, Long userId) {
        log.info("Customer fetching order detail for orderId: {}, userId: {}", orderId, userId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppCustomException("Order not found with id: " + orderId, HttpStatus.NOT_FOUND));

        if (!order.getUser().getId().equals(userId)) {
            throw new AppCustomException("Access denied: Order does not belong to this user", HttpStatus.FORBIDDEN);
        }

        return mapToDetail(order);
    }

    // ═══════════════════════════════════════════════════════
    // Cancel order (only PLACED_ORDER status can be cancelled)
    // ═══════════════════════════════════════════════════════

    /**
     * Cancels a customer's order if it is still in a cancellable state.
     *
     * @param orderId the ID of the order to cancel
     * @param userId  the ID of the customer requesting cancellation
     * @return OrderDetailDto with status set to ORDER_CANCELLED
     *
     * @throws AppCustomException if the order is not found, does not belong to the user,
     *                            or cannot be cancelled in its current status
     *
     * @implSpec Stock is restored for each cancelled item upon successful cancellation.
     */
    public OrderDetailDto cancelOrder(Long orderId, Long userId) {
        log.info("Customer cancelling order: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppCustomException("Order not found with id: " + orderId, HttpStatus.NOT_FOUND));

        if (!order.getUser().getId().equals(userId)) {
            throw new AppCustomException("Access denied: Order does not belong to this user", HttpStatus.FORBIDDEN);
        }

        if (!order.canBeCancelled()) {
            throw new AppCustomException("Order cannot be cancelled in status: " + order.getStatus(), HttpStatus.BAD_REQUEST);
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

    /**
     * Adds delivery details to an existing order that does not yet have them.
     *
     * @param orderId the ID of the order
     * @param userId  the ID of the customer adding delivery details
     * @param request the delivery details (name, phone, address)
     * @return the saved OrderDeliveryMap entity
     *
     * @throws AppCustomException if the order is not found, does not belong to the user,
     *                            or delivery details already exist for the order
     *
     * @apiNote Returns HTTP 409 if delivery details are already present.
     */
    public OrderDeliveryMap addDeliveryDetails(Long orderId, Long userId, OrderDeliveryRequest request) {
        log.info("Adding delivery details for orderId: {}, userId: {}", orderId, userId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppCustomException("Order not found with id: " + orderId, HttpStatus.NOT_FOUND));

        if (!order.getUser().getId().equals(userId)) {
            throw new AppCustomException("Access denied: Order does not belong to this user", HttpStatus.FORBIDDEN);
        }

        if (orderDeliveryMapRepository.findByOrderId(orderId).isPresent()) {
            throw new AppCustomException("Delivery details already exist for this order", HttpStatus.CONFLICT);
        }

        OrderDeliveryMap delivery = new OrderDeliveryMap(order, request.getName(), request.getPhone(), request.getAddress());
        return orderDeliveryMapRepository.save(delivery);
    }

    // ═══════════════════════════════════════════════════════
    // Helper Methods
    // ═══════════════════════════════════════════════════════

    /**
     * Maps an Order entity to an OrderSummaryDetailDto.
     *
     * @param order the Order entity to map
     * @return OrderSummaryDetailDto with high-level order fields
     *
     * @implNote Internal helper for lightweight list responses.
     */
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

    /**
     * Maps an Order entity to a full OrderDetailDto including items and delivery info.
     *
     * @param order the Order entity to map
     * @return OrderDetailDto with complete order, item, and delivery details
     *
     * @implNote Delivery details are fetched from OrderDeliveryMap; null-safe if not present.
     */
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
