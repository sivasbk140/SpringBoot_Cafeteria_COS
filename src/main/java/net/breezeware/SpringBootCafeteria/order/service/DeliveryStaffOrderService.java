package net.breezeware.SpringBootCafeteria.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.breezeware.SpringBootCafeteria.exception.AppException;
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
                .orElseThrow(() -> new AppException("No delivery details found for order: " + orderId, HttpStatus.NOT_FOUND));

        if (!staffId.equals(deliveryMap.getDeliveryStaffId())) {
            throw new AppException("Order " + orderId + " is not assigned to staff " + staffId, HttpStatus.FORBIDDEN);
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException("Order not found with id: " + orderId, HttpStatus.NOT_FOUND));

        if (order.getStatus() != OrderStatus.ASSIGNED_DELIVERY_STAFF) {
            throw new AppException("Order must be in ASSIGNED_DELIVERY_STAFF status to mark as delivered", HttpStatus.BAD_REQUEST);
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
