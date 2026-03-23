package net.breezeware.Spring_Boot_Cafeteria.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.breezeware.Spring_Boot_Cafeteria.order.dto.OrderDetailDto;
import net.breezeware.Spring_Boot_Cafeteria.order.dto.OrderSummaryDetailDto;
import net.breezeware.Spring_Boot_Cafeteria.order.entity.Order;
import net.breezeware.Spring_Boot_Cafeteria.order.enumeration.OrderStatus;
import net.breezeware.Spring_Boot_Cafeteria.order.repo.OrderRepository;
import net.breezeware.Spring_Boot_Cafeteria.user.entity.DeliveryDetail;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StaffOrderService {

    private final OrderRepository orderRepository;

    // ═══════════════════════════════════════════════════════
    // View all orders
    // ═══════════════════════════════════════════════════════

    @Transactional(readOnly = true)
    public List<OrderSummaryDetailDto> getAllOrders() {
        log.info("Staff fetching all orders");
        return orderRepository.findAll().stream()
                .map(this::mapToSummary)
                .collect(Collectors.toList());
    }

    // ═══════════════════════════════════════════════════════
    // Filter orders by status
    // ═══════════════════════════════════════════════════════

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

    @Transactional(readOnly = true)
    public OrderDetailDto getOrderById(Long orderId) {
        log.info("Staff fetching order by id: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        return mapToDetail(order);
    }

    // ═══════════════════════════════════════════════════════
    // Update order status (staff cannot cancel orders)
    // ═══════════════════════════════════════════════════════

    public OrderDetailDto updateOrderStatus(Long orderId, OrderStatus newStatus) {
        log.info("Staff updating order {} status to {}", orderId, newStatus);

        if (newStatus == OrderStatus.ORDER_CANCELLED) {
            throw new RuntimeException("Staff cannot cancel orders. Only customer or admin can cancel.");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        order.setStatus(newStatus);
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