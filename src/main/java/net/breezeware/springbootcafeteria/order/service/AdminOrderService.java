package net.breezeware.springbootcafeteria.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.breezeware.springbootcafeteria.exception.AppCustomException;
import net.breezeware.springbootcafeteria.order.dto.OrderDetailDto;
import net.breezeware.springbootcafeteria.order.dto.OrderSummaryDetailDto;
import net.breezeware.springbootcafeteria.order.entity.Order;
import net.breezeware.springbootcafeteria.order.entity.OrderDeliveryMap;
import net.breezeware.springbootcafeteria.order.enumeration.OrderStatus;
import net.breezeware.springbootcafeteria.order.dao.OrderDeliveryMapRepository;
import net.breezeware.springbootcafeteria.order.dao.OrderRepository;
import net.breezeware.springbootcafeteria.user.entity.User;
import net.breezeware.springbootcafeteria.user.enumeration.Role;
import net.breezeware.springbootcafeteria.user.dao.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class responsible for managing all orders in the cafeteria system
 * from an administrative perspective.
 *
 * his service provides admin-level access to view, filter, update, and
 * cancel orders, as well as assign delivery staff to prepared orders.
 *
 * @author Siva
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminOrderService {

    private final OrderRepository orderRepository;
    private final OrderDeliveryMapRepository orderDeliveryMapRepository;
    private final UserRepository userRepository;

    /**
     * Retrieves all orders in the system.
     *
     * @return list of OrderSummaryDetailDto for all orders
     *
     * @throws AppCustomException if no orders exist in the system
     *
     * @apiNote Only admin users are allowed to view all orders.
     */
    public List<OrderSummaryDetailDto> getAllOrders() {
        log.info("Admin fetching all orders in service layer");
        List<OrderSummaryDetailDto> orders = orderRepository.findAll().stream()
                .map(this::mapToSummary)
                .collect(Collectors.toList());

        if (orders.isEmpty()) {
            log.error("No orders found in the system");
            throw new AppCustomException("No orders found in the system", HttpStatus.NOT_FOUND);
        }

        log.info("Returning {} orders", orders.size());
        return orders;
    }

    /**
     * Retrieves a single order by its ID.
     *
     * @param orderId the unique identifier of the order
     * @return OrderDetailDto containing full order details
     *
     * @throws AppCustomException if the order is not found
     *
     * @apiNote Returns HTTP 404 if the order does not exist.
     */
    @Transactional(readOnly = true)
    public OrderDetailDto getOrderById(Long orderId) {
        log.info("Admin fetching order by id: {} in service layer", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> { log.error("Order not found for id: {}", orderId);
                    return new AppCustomException("Order not found with id: " + orderId, HttpStatus.NOT_FOUND); });
        log.info("Order found with id: {}", orderId);
        return mapToDetail(order);
    }

    /**
     * Retrieves all orders filtered by a specific status.
     *
     * @param status the OrderStatus to filter by
     * @return list of OrderSummaryDetailDto matching the given status
     *
     * @throws AppCustomException if no orders are found for the given status
     */
    @Transactional(readOnly = true)
    public List<OrderSummaryDetailDto> getOrdersByStatus(OrderStatus status) {
        log.info("Admin fetching orders by status: {} in service layer", status);
        List<OrderSummaryDetailDto> ordersByStatus = orderRepository.findByStatus(status).stream()
                .map(this::mapToSummary)
                .collect(Collectors.toList());

        if (ordersByStatus.isEmpty()) {
            log.error("No orders found for the status: {}", status);
            throw new AppCustomException("No orders found for the status: " + status, HttpStatus.NOT_FOUND);
        }
        log.info("Returning {} orders for status: {}", ordersByStatus.size(), status);
        return ordersByStatus;
    }

    /**
     * Retrieves all orders placed by a specific user.
     *
     * @param userId the ID of the user whose orders to retrieve
     * @return list of OrderSummaryDetailDto for the given user
     *
     * @throws AppCustomException if no orders are found for the user
     */
    @Transactional(readOnly = true)
    public List<OrderSummaryDetailDto> getOrdersByUserId(Long userId) {
        log.info("Admin fetching orders for user: {} in service layer", userId);
        List<OrderSummaryDetailDto> ordersById = orderRepository.findByUserId(userId).stream()
                .map(this::mapToSummary)
                .collect(Collectors.toList());

        if (ordersById.isEmpty()) {
            log.error("No orders found for the user with id: {}", userId);
            throw new AppCustomException("No orders found for the user with id: " + userId, HttpStatus.NOT_FOUND);
        }
        log.info("Returning {} orders for user: {}", ordersById.size(), userId);
        return ordersById;
    }

    /**
     * Updates the status of an existing order.
     *
     * @param orderId   the ID of the order to update
     * @param newStatus the new OrderStatus to transition to
     * @return OrderDetailDto reflecting the updated order state
     *
     * @throws AppCustomException if the order is not found or the status transition is invalid
     *
     * @implNote Status transitions are validated via the OrderStatus state machine.
     */
    public OrderDetailDto updateOrderStatus(Long orderId, OrderStatus newStatus) {
        log.info("Admin updating order {} status to {} in service layer", orderId, newStatus);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> { log.error("Order not found for id: {}", orderId);
                    return new AppCustomException("Order not found with id: " + orderId, HttpStatus.NOT_FOUND); });

        if (!order.getStatus().canTransitionTo(newStatus)) {
            log.error("Invalid status transition: {} -> {} for order: {}", order.getStatus(), newStatus, orderId);
            throw new AppCustomException("Invalid status transition: " + order.getStatus() + " -> " + newStatus, HttpStatus.BAD_REQUEST);
        }
        order.setStatus(newStatus);
        Order updated = orderRepository.save(order);
        log.info("Order {} status updated to {} successfully", orderId, newStatus);
        return mapToDetail(updated);
    }

    /**
     * Assigns a delivery staff member to an order that is in ORDER_PREPARING status.
     *
     * @param orderId the ID of the order to assign delivery staff to
     * @param staffId the ID of the delivery staff user to assign
     * @return OrderDetailDto with updated delivery staff and status
     *
     * @throws AppCustomException if the order or staff is not found, the order is not in
     *                            ORDER_PREPARING status, or the user is not a DELIVERY_STAFF role
     *
     * @implSpec Order status is automatically transitioned to ASSIGNED_DELIVERY_STAFF after assignment.
     */
    public OrderDetailDto assignDeliveryStaff(Long orderId, Long staffId) {
        log.info("Admin assigning delivery staff {} to order {} in service layer", staffId, orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> { log.error("Order not found for id: {}", orderId);
                    return new AppCustomException("Order not found with id: " + orderId, HttpStatus.NOT_FOUND); });

        if (order.getStatus() != OrderStatus.ORDER_PREPARING) {
            log.error("Order {} is not in ORDER_PREPARING status, current status: {}", orderId, order.getStatus());
            throw new AppCustomException("Order must be in ORDER_PREPARING status to assign delivery staff", HttpStatus.BAD_REQUEST);
        }

        User deliveryStaff = userRepository.findById(staffId)
                .orElseThrow(() -> { log.error("Delivery staff not found for id: {}", staffId);
                    return new AppCustomException("Delivery staff not found with id: " + staffId, HttpStatus.NOT_FOUND); });

        if (deliveryStaff.getRole() != Role.DELIVERY_STAFF) {
            log.error("User {} is not a delivery staff, role: {}", staffId, deliveryStaff.getRole());
            throw new AppCustomException("User with id " + staffId + " is not a delivery staff", HttpStatus.BAD_REQUEST);
        }

        OrderDeliveryMap deliveryMap = orderDeliveryMapRepository.findByOrderId(orderId)
                .orElseThrow(() -> { log.error("No delivery details found for order: {}", orderId);
                    return new AppCustomException("No delivery details found for order: " + orderId, HttpStatus.NOT_FOUND); });
        deliveryMap.setDeliveryStaffId(staffId);
        orderDeliveryMapRepository.save(deliveryMap);

        order.setStatus(OrderStatus.ASSIGNED_DELIVERY_STAFF);
        Order updated = orderRepository.save(order);
        log.info("Delivery staff {} assigned to order {} successfully", staffId, orderId);
        return mapToDetail(updated);
    }

    /**
     * Force-cancels an order regardless of its current status (unless already delivered or cancelled).
     *
     * @param orderId the ID of the order to cancel
     * @return OrderDetailDto with status set to ORDER_CANCELLED
     *
     * @throws AppCustomException if the order is not found or is already delivered or cancelled
     *
     * @apiNote This operation is irreversible.
     */
    public OrderDetailDto cancelOrder(Long orderId) {
        log.info("Admin force cancelling order: {} in service layer", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> { log.error("Order not found for id: {}", orderId);
                    return new AppCustomException("Order not found with id: " + orderId, HttpStatus.NOT_FOUND); });

        if (!order.getStatus().isForceCancellable()) {
            log.error("Order {} cannot be cancelled, current status: {}", orderId, order.getStatus());
            throw new AppCustomException("Order is already delivered or cancelled, cannot cancel.", HttpStatus.BAD_REQUEST);
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
