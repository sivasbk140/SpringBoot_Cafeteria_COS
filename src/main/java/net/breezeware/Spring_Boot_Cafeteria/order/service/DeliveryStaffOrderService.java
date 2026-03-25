package net.breezeware.Spring_Boot_Cafeteria.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.breezeware.Spring_Boot_Cafeteria.order.dto.OrderDetailDto;
import net.breezeware.Spring_Boot_Cafeteria.order.dto.OrderSummaryDetailDto;
import net.breezeware.Spring_Boot_Cafeteria.order.entity.Order;
import net.breezeware.Spring_Boot_Cafeteria.order.entity.OrderDeliveryMap;
import net.breezeware.Spring_Boot_Cafeteria.order.enumeration.OrderStatus;
import net.breezeware.Spring_Boot_Cafeteria.order.repo.OrderDeliveryMapRepository;
import net.breezeware.Spring_Boot_Cafeteria.order.repo.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryStaffOrderService {

    private final OrderRepository orderRepository;
    private final OrderDeliveryMapRepository orderDeliveryMapRepository;

    @Transactional(readOnly = true)
    public List<OrderSummaryDetailDto> getAssignedOrders(Long staffId) {
        log.info("Delivery staff {} fetching assigned orders", staffId);
        return orderDeliveryMapRepository.findByDeliveryStaffId(staffId).stream()
                .map(OrderDeliveryMap::getOrder)
                .filter(order -> order.getStatus() == OrderStatus.ASSIGNED_DELIVERY_STAFF)
                .map(this::mapToSummary)
                .collect(Collectors.toList());
    }

    public OrderDetailDto markOrderDelivered(Long orderId, Long staffId) {
        log.info("Delivery staff {} marking order {} as delivered", staffId, orderId);

        OrderDeliveryMap deliveryMap = orderDeliveryMapRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("No delivery details found for order: " + orderId));

        if (!staffId.equals(deliveryMap.getDeliveryStaffId())) {
            throw new RuntimeException("Order " + orderId + " is not assigned to staff " + staffId);
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        if (order.getStatus() != OrderStatus.ASSIGNED_DELIVERY_STAFF) {
            throw new RuntimeException("Order must be in ASSIGNED_DELIVERY_STAFF status to mark as delivered");
        }

        order.setStatus(OrderStatus.ORDER_DELIVERED);
        Order updated = orderRepository.save(order);
        return mapToDetail(updated);
    }

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
                order.getId().intValue(),
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
