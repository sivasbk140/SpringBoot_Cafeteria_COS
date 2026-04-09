package net.breezeware.springbootcafeteria.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.breezeware.springbootcafeteria.exception.AppCustomException;
import net.breezeware.springbootcafeteria.food.entity.FoodItem;
import net.breezeware.springbootcafeteria.food.dao.FoodItemRepository;
import net.breezeware.springbootcafeteria.order.dto.CartItemDto;
import net.breezeware.springbootcafeteria.order.dto.OrderDeliveryRequest;
import net.breezeware.springbootcafeteria.order.dto.OrderDetailDto;
import net.breezeware.springbootcafeteria.order.dto.OrderSummaryDetailDto;
import net.breezeware.springbootcafeteria.order.entity.Order;
import net.breezeware.springbootcafeteria.order.entity.OrderDeliveryMap;
import net.breezeware.springbootcafeteria.order.entity.OrderItem;
import net.breezeware.springbootcafeteria.order.enumeration.OrderStatus;
import net.breezeware.springbootcafeteria.order.dao.OrderDeliveryMapRepository;
import net.breezeware.springbootcafeteria.order.dao.OrderItemRepository;
import net.breezeware.springbootcafeteria.order.dao.OrderRepository;
import net.breezeware.springbootcafeteria.user.entity.User;
import net.breezeware.springbootcafeteria.user.dao.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
        log.info("Adding foodItem: {} to cart for userId: {} in service layer", foodItemName, userId);

        FoodItem foodItem = foodItemRepository.findByNameIgnoreCase(foodItemName)
                .orElseThrow(() -> { log.error("Food item not found with name: {}", foodItemName);
                    return new AppCustomException("Food item not found with name: " + foodItemName, HttpStatus.NOT_FOUND); });

        if (foodItem.getQuantity() < quantity) {
            log.error("Insufficient stock for item: {}, available: {}, requested: {}", foodItem.getName(), foodItem.getQuantity(), quantity);
            throw new AppCustomException("Insufficient stock for item: " + foodItem.getName(), HttpStatus.BAD_REQUEST);
        }

        List<CartItemDto> cart = cartStore.computeIfAbsent(userId, k -> new ArrayList<>());

        // If item already in cart, increase quantity
        for (CartItemDto existing : cart) {
            if (existing.getFoodItemName().equalsIgnoreCase(foodItemName)) {
                existing.setQuantity(existing.getQuantity() + quantity);
                existing.setTotalPrice(foodItem.getPrice() * existing.getQuantity());
                log.info("Updated quantity for item: {} in cart for userId: {}", foodItemName, userId);
                return cart;
            }
        }

        // New item — add to cart
        cart.add(new CartItemDto(
                foodItem.getId(),
                foodItem.getName(),
                foodItem.getPrice() * quantity,
                quantity
        ));

        log.info("Added new item: {} to cart for userId: {}", foodItemName, userId);
        return cart;
    }


    /**
     * Returns the current contents of a customer's in-memory cart.
     *
     * @param userId the ID of the customer
     * @return list of CartItemDto currently in the cart; empty list if cart is empty
     */
    @Transactional(readOnly = true)
    public List<CartItemDto> viewCart(Long userId) {
        log.info("Viewing cart for userId: {} in service layer", userId);
        return cartStore.getOrDefault(userId, new ArrayList<>());
    }


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
        log.info("Removing {} unit(s) of foodItem: {} from cart for userId: {} in service layer", quantity, foodItemName, userId);
        List<CartItemDto> cart = cartStore.getOrDefault(userId, new ArrayList<>());

        CartItemDto item = cart.stream()
                .filter(c -> c.getFoodItemName().equalsIgnoreCase(foodItemName))
                .findFirst()
                .orElseThrow(() -> { log.error("Food item not found in cart: {} for userId: {}", foodItemName, userId);
                    return new AppCustomException("Food item not found in cart: " + foodItemName, HttpStatus.NOT_FOUND); });

        int newQuantity = item.getQuantity() - quantity;
        if (newQuantity <= 0) {
            cart.remove(item);
            log.info("Item: {} fully removed from cart for userId: {}", foodItemName, userId);
        } else {
            double unitPrice = item.getTotalPrice() / item.getQuantity();
            item.setQuantity(newQuantity);
            item.setTotalPrice(unitPrice * newQuantity);
            log.info("Item: {} quantity reduced to {} in cart for userId: {}", foodItemName, newQuantity, userId);
        }

        return cart;
    }


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
        log.info("Checkout for userId: {} in service layer", userId);

        List<CartItemDto> cart = cartStore.getOrDefault(userId, new ArrayList<>());
        if (cart.isEmpty()) {
            log.error("Cart is empty for userId: {}", userId);
            throw new AppCustomException("Cart is empty for userId: " + userId, HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> { log.error("User not found for id: {}", userId);
                    return new AppCustomException("User not found with id: " + userId, HttpStatus.NOT_FOUND); });

        Order order = new Order(user, OrderStatus.PLACED_ORDER);

        for (CartItemDto cartItem : cart) {
            FoodItem foodItem = foodItemRepository.findByIdWithLock(cartItem.getFoodItemId())
                    .orElseThrow(() -> { log.error("Food item not found for id: {}", cartItem.getFoodItemId());
                        return new AppCustomException("Food item not found: " + cartItem.getFoodItemId(), HttpStatus.NOT_FOUND); });

            int qty = cartItem.getQuantity();
            if (foodItem.getQuantity() < qty) {
                log.error("Insufficient stock for item: {}, available: {}, requested: {}", foodItem.getName(), foodItem.getQuantity(), qty);
                throw new AppCustomException("Insufficient stock for item: " + foodItem.getName(), HttpStatus.BAD_REQUEST);
            }

            foodItem.setQuantity(foodItem.getQuantity() - qty);
            foodItemRepository.save(foodItem);

            OrderItem orderItem = new OrderItem(order, foodItem, foodItem.getPrice(), qty);
            order.getItems().add(orderItem);
        }

        Order saved = orderRepository.save(order);

        OrderDeliveryMap delivery = new OrderDeliveryMap(saved, deliveryRequest.getName(), deliveryRequest.getPhone(), deliveryRequest.getAddress());
        orderDeliveryMapRepository.save(delivery);

        // Clear cart after successful checkout
        cartStore.remove(userId);

        log.info("Order placed successfully for userId: {}, orderId: {}", userId, saved.getId());
        return mapToDetail(saved);
    }


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
        log.info("Customer fetching orders for userId: {} in service layer", userId);
        List<OrderSummaryDetailDto> myOrders = orderRepository.findByUserId(userId).stream()
                .map(this::mapToSummary)
                .collect(Collectors.toList());

        if (myOrders.isEmpty()) {
            log.error("No orders found for user with id: {}", userId);
            throw new AppCustomException("No orders found for user with id: " + userId, HttpStatus.NOT_FOUND);
        }
        log.info("Returning {} orders for userId: {}", myOrders.size(), userId);
        return myOrders;
    }


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
        log.info("Customer fetching order detail for orderId: {}, userId: {} in service layer", orderId, userId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> { log.error("Order not found for id: {}", orderId);
                    return new AppCustomException("Order not found with id: " + orderId, HttpStatus.NOT_FOUND); });

        if (!order.getUser().getId().equals(userId)) {
            log.error("Access denied: Order {} does not belong to userId: {}", orderId, userId);
            throw new AppCustomException("Access denied: Order does not belong to this user", HttpStatus.FORBIDDEN);
        }

        log.info("Order detail found for orderId: {}", orderId);
        return mapToDetail(order);
    }


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
        log.info("Customer cancelling order: {} for userId: {} in service layer", orderId, userId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> { log.error("Order not found for id: {}", orderId);
                    return new AppCustomException("Order not found with id: " + orderId, HttpStatus.NOT_FOUND); });

        if (!order.getUser().getId().equals(userId)) {
            log.error("Access denied: Order {} does not belong to userId: {}", orderId, userId);
            throw new AppCustomException("Access denied: Order does not belong to this user", HttpStatus.FORBIDDEN);
        }

        if (!order.getStatus().isCancellable()) {
            log.error("Order {} cannot be cancelled in status: {}", orderId, order.getStatus());
            throw new AppCustomException("Order cannot be cancelled in status: " + order.getStatus(), HttpStatus.BAD_REQUEST);
        }

        // Restore stock for each cancelled item
        for (OrderItem item : order.getItems()) {
            item.getFoodItem().setQuantity(item.getFoodItem().getQuantity() + item.getQuantity());
            foodItemRepository.save(item.getFoodItem());
        }

        order.setStatus(OrderStatus.ORDER_CANCELLED);
        Order updated = orderRepository.save(order);
        log.info("Order {} cancelled successfully", orderId);
        return mapToDetail(updated);
    }


    /**
     * Maps an Order entity to an OrderSummaryDetailDto.
     *
     * @param order the Order entity to map
     * @return OrderSummaryDetailDto with high-level order fields
     *
     * @implNote Internal helper for lightweight list responses.
     */
    private OrderSummaryDetailDto mapToSummary(Order order) {
        log.debug("Mapping order to summary: {}", order.getId());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
        return new OrderSummaryDetailDto(
                order.getId(),
                order.getUser().getId(),
                order.getStatus(),
                order.getItems().stream().mapToDouble(i -> i.getPrice() * i.getQuantity()).sum(),
                order.getCreatedOn() != null ? dtf.format(order.getCreatedOn()) : null
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
        log.debug("Mapping order to detail: {}", order.getId());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

        List<OrderDetailDto.OrderItemDetailDTO> itemDetails = order.getItems().stream()
                .map(item -> new OrderDetailDto.OrderItemDetailDTO(
                        item.getFoodItem().getName(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getPrice() * item.getQuantity()
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
                order.getId(),
                order.getUser().getId(),
                order.getUser().getName(),
                order.getStatus(),
                itemDetails,
                order.getItems().stream().mapToDouble(i -> i.getPrice() * i.getQuantity()).sum(),
                deliveryName,
                deliveryPhone,
                deliveryAddress,
                order.getCreatedOn() != null ? dtf.format(order.getCreatedOn()) : null
        );
    }
}
