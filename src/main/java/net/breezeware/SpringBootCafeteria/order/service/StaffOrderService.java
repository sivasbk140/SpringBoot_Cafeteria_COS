package net.breezeware.SpringBootCafeteria.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.breezeware.SpringBootCafeteria.exception.AppCustomException;
import net.breezeware.SpringBootCafeteria.order.dto.OrderDetailDto;
import net.breezeware.SpringBootCafeteria.order.dto.OrderSummaryDetailDto;
import net.breezeware.SpringBootCafeteria.order.entity.Order;
import net.breezeware.SpringBootCafeteria.order.entity.OrderDeliveryMap;
import net.breezeware.SpringBootCafeteria.order.enumeration.OrderStatus;
import net.breezeware.SpringBootCafeteria.order.repo.OrderDeliveryMapRepository;
import net.breezeware.SpringBootCafeteria.order.repo.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class responsible for managing order operations available to
 * cafeteria staff in the cafeteria system.
 *
 * <p>This service allows staff to view all orders, filter by status or user,
 * update order statuses, assign delivery staff, and cancel orders.
 * Staff cannot cancel orders via the status update endpoint;
 * a dedicated cancel method is provided instead.</p>
 *
 * @author Siva
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StaffOrderService {

    private final OrderRepository orderRepository;
    private final OrderDeliveryMapRepository orderDeliveryMapRepository;

    // ═══════════════════════════════════════════════════════
    // View all orders
    // ═══════════════════════════════════════════════════════

    /**
     * Retrieves all orders in the system.
     *
     * @return list of OrderSummaryDetailDto for all orders
     *
     * @apiNote Accessible by staff users only.
     */
    public List<OrderSummaryDetailDto> getAllOrders() {
        log.info("Staff fetching all orders");
        return orderRepository.findAll().stream()
                .map(this::mapToSummary)
                .collect(Collectors.toList());
    }

    // ═══════════════════════════════════════════════════════
    // Filter orders by status
    // ═══════════════════════════════════════════════════════

    /**
     * Retrieves all orders filtered by a specific status.
     *
     * @param status the OrderStatus to filter by
     * @return list of OrderSummaryDetailDto matching the given status
     */
    @Transactional(readOnly = true)
    public List<OrderSummaryDetailDto> getOrdersByStatus(OrderStatus status) {
        log.info("Staff fetching orders by status: {}", status);
        return orderRepository.findByStatus(status).stream()
                .map(this::mapToSummary)
                .collect(Collectors.toList());
    }

    // ═══════════════════════════════════════════════════════
    // View single order detail
    // ═══════════════════════════════════════════════════════

    /**
     * Retrieves full details of a single order by its ID.
     *
     * @param orderId the unique identifier of the order
     * @return OrderDetailDto containing full order details
     *
     * @throws AppCustomException if the order is not found
     */
    @Transactional(readOnly = true)
    public OrderDetailDto getOrderById(Long orderId) {
        log.info("Staff fetching order by id: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppCustomException("Order not found with id: " + orderId, HttpStatus.NOT_FOUND));
        return mapToDetail(order);
    }

    /**
     * Retrieves all orders placed by a specific user.
     *
     * @param userId the ID of the user whose orders to retrieve
     * @return list of OrderSummaryDetailDto for the given user
     */
    @Transactional(readOnly = true)
    public List<OrderSummaryDetailDto> getOrdersByUserId(Long userId) {
        log.info("Staff fetching orders for user: {}", userId);
        return orderRepository.findByUserId(userId).stream()
                .map(this::mapToSummary)
                .collect(Collectors.toList());
    }

    // ═══════════════════════════════════════════════════════
    // Update order status (staff cannot cancel orders)
    // ═══════════════════════════════════════════════════════

    /**
     * Updates the status of an existing order (cancellation is not allowed via this method).
     *
     * @param orderId   the ID of the order to update
     * @param newStatus the new OrderStatus to transition to
     * @return OrderDetailDto reflecting the updated order state
     *
     * @throws AppCustomException if ORDER_CANCELLED is passed, the order is not found,
     *                            or the status transition is invalid
     *
     * @apiNote Staff must use the dedicated cancel endpoint to cancel orders.
     */
    public OrderDetailDto updateOrderStatus(Long orderId, OrderStatus newStatus) {
        log.info("Staff updating order {} status to {}", orderId, newStatus);

        if (newStatus == OrderStatus.ORDER_CANCELLED) {
            throw new AppCustomException("Use the cancel endpoint to cancel an order.", HttpStatus.BAD_REQUEST);
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppCustomException("Order not found with id: " + orderId, HttpStatus.NOT_FOUND));

        if (!order.getStatus().canTransitionTo(newStatus)) {
            throw new AppCustomException("Invalid status transition: " + order.getStatus() + " -> " + newStatus, HttpStatus.BAD_REQUEST);
        }

        order.setStatus(newStatus);
        Order updated = orderRepository.save(order);
        return mapToDetail(updated);
    }

    /**
     * Force-cancels an order unless it is already delivered or cancelled.
     *
     * @param orderId the ID of the order to cancel
     * @return OrderDetailDto with status set to ORDER_CANCELLED
     *
     * @throws AppCustomException if the order is not found or is already delivered or cancelled
     *
     * @apiNote This operation is irreversible.
     */
    public OrderDetailDto cancelOrder(Long orderId) {
        log.info("Staff force cancelling order: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppCustomException("Order not found with id: " + orderId, HttpStatus.NOT_FOUND));

        if (!order.getStatus().isForceCancellable()) {
            throw new AppCustomException("Order is already delivered or cancelled, cannot cancel.", HttpStatus.BAD_REQUEST);
        }
        order.setStatus(OrderStatus.ORDER_CANCELLED);
        Order updated = orderRepository.save(order);
        return mapToDetail(updated);
    }

    /**
     * Assigns a delivery staff member to an order that is in ORDER_PREPARING status.
     *
     * @param orderId the ID of the order to assign delivery staff to
     * @param staffId the ID of the delivery staff user to assign
     * @return OrderDetailDto with updated delivery staff and status
     *
     * @throws AppCustomException if the order is not found, is not in ORDER_PREPARING status,
     *                            or no delivery details record exists for the order
     *
     * @implSpec Order status is automatically transitioned to ASSIGNED_DELIVERY_STAFF after assignment.
     */
    public OrderDetailDto assignDeliveryStaff(Long orderId, Long staffId) {
        log.info("Admin assigning delivery staff {} to order {}", staffId, orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppCustomException("Order not found with id: " + orderId, HttpStatus.NOT_FOUND));

        if (order.getStatus() != OrderStatus.ORDER_PREPARING) {
            throw new AppCustomException("Order must be in ORDER_PREPARING status to assign delivery staff", HttpStatus.BAD_REQUEST);
        }

        OrderDeliveryMap deliveryMap = orderDeliveryMapRepository.findByOrderId(orderId)
                .orElseThrow(() -> new AppCustomException("No delivery details found for order: " + orderId, HttpStatus.NOT_FOUND));
        deliveryMap.setDeliveryStaffId(staffId);
        orderDeliveryMapRepository.save(deliveryMap);

        order.setStatus(OrderStatus.ASSIGNED_DELIVERY_STAFF);
        Order updated = orderRepository.save(order);
        return mapToDetail(updated);
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
