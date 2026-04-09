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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class responsible for managing order operations available to
 * delivery staff in the cafeteria system.
 *
 * This service allows delivery staff to view orders assigned to them
 * and mark those orders as delivered upon completion.
 *
 * @author Siva
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryStaffOrderService {

    private final OrderRepository orderRepository;
    private final OrderDeliveryMapRepository orderDeliveryMapRepository;

    /**
     * Retrieves all orders currently assigned to a specific delivery staff member.
     *
     * @param staffId the ID of the delivery staff member
     * @return list of OrderSummaryDetailDto for orders in ASSIGNED_DELIVERY_STAFF status
     *
     * @implNote Filters by delivery staff ID and order status to return only active assignments.
     */
    public List<OrderSummaryDetailDto> getAssignedOrders(Long staffId) {
        log.info("Delivery staff {} fetching assigned orders", staffId);
        return orderDeliveryMapRepository.findByDeliveryStaffId(staffId).stream()
                .map(OrderDeliveryMap::getOrder)
                .filter(order -> order.getStatus() == OrderStatus.ASSIGNED_DELIVERY_STAFF)
                .map(this::mapToSummary)
                .collect(Collectors.toList());
    }

    /**
     * Marks an assigned order as delivered by the delivery staff member.
     *
     * @param orderId the ID of the order to mark as delivered
     * @param staffId the ID of the delivery staff member confirming delivery
     * @return OrderDetailDto with status set to ORDER_DELIVERED
     *
     * @throws AppCustomException if no delivery record is found for the order,
     *                            the order is not assigned to this staff member,
     *                            the order is not found, or the order is not in
     *                            ASSIGNED_DELIVERY_STAFF status
     *
     * @apiNote Returns HTTP 403 if the staff member is not assigned to this order.
     */
    public OrderDetailDto markOrderDelivered(Long orderId, Long staffId) {
        log.info("Delivery staff {} marking order {} as delivered", staffId, orderId);

        OrderDeliveryMap deliveryMap = orderDeliveryMapRepository.findByOrderId(orderId)
                .orElseThrow(() -> new AppCustomException("No delivery details found for order: " + orderId, HttpStatus.NOT_FOUND));

        if (!staffId.equals(deliveryMap.getDeliveryStaffId())) {
            throw new AppCustomException("Order " + orderId + " is not assigned to staff " + staffId, HttpStatus.FORBIDDEN);
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppCustomException("Order not found with id: " + orderId, HttpStatus.NOT_FOUND));

        if (order.getStatus() != OrderStatus.ASSIGNED_DELIVERY_STAFF) {
            throw new AppCustomException("Order must be in ASSIGNED_DELIVERY_STAFF status to mark as delivered", HttpStatus.BAD_REQUEST);
        }

        order.setStatus(OrderStatus.ORDER_DELIVERED);
        Order updated = orderRepository.save(order);
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
