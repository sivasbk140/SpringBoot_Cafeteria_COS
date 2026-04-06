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
import net.breezeware.SpringBootCafeteria.user.entity.User;
import net.breezeware.SpringBootCafeteria.user.enumeration.Role;
import net.breezeware.SpringBootCafeteria.user.repo.UserRepository;
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
public class AdminOrderService {

    private final OrderRepository orderRepository;
    private final OrderDeliveryMapRepository orderDeliveryMapRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<OrderSummaryDetailDto> getAllOrders() {
        log.info("Admin fetching all orders");
        List<OrderSummaryDetailDto> orders = orderRepository.findAll().stream()
                .map(this::mapToSummary)
                .collect(Collectors.toList());

        if (orders.isEmpty()) {
            throw new AppException("No orders found in the system", HttpStatus.NOT_FOUND);
        }

        return orders;
    }

    @Transactional(readOnly = true)
    public OrderDetailDto getOrderById(Long orderId) {
        log.info("Admin fetching order by id: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException("Order not found with id: " + orderId, HttpStatus.NOT_FOUND));
        return mapToDetail(order);
    }

    @Transactional(readOnly = true)
    public List<OrderSummaryDetailDto> getOrdersByStatus(OrderStatus status) {
        log.info("Admin fetching orders by status: {}", status);
        List<OrderSummaryDetailDto> ordersByStatus = orderRepository.findByStatus(status).stream()
                .map(this::mapToSummary)
                .collect(Collectors.toList());

        if (ordersByStatus.isEmpty()) {
            throw new AppException("No orders found for the status: " + status, HttpStatus.NOT_FOUND);
        }
        return ordersByStatus;
    }

    @Transactional(readOnly = true)
    public List<OrderSummaryDetailDto> getOrdersByUserId(Long userId) {
        log.info("Admin fetching orders for user: {}", userId);
        List<OrderSummaryDetailDto> ordersById = orderRepository.findByUserId(userId).stream()
                .map(this::mapToSummary)
                .collect(Collectors.toList());

        if (ordersById.isEmpty()) {
            throw new AppException("No orders found for the user with id: " + userId, HttpStatus.NOT_FOUND);
        }
        return ordersById;
    }

    public OrderDetailDto updateOrderStatus(Long orderId, OrderStatus newStatus) {
        log.info("Admin updating order {} status to {}", orderId, newStatus);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException("Order not found with id: " + orderId, HttpStatus.NOT_FOUND));

        if (!order.getStatus().canTransitionTo(newStatus)) {
            throw new AppException("Invalid status transition: " + order.getStatus() + " -> " + newStatus, HttpStatus.BAD_REQUEST);
        }
        order.setStatus(newStatus);
        Order updated = orderRepository.save(order);
        return mapToDetail(updated);
    }

    public OrderDetailDto assignDeliveryStaff(Long orderId, Long staffId) {
        log.info("Admin assigning delivery staff {} to order {}", staffId, orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException("Order not found with id: " + orderId, HttpStatus.NOT_FOUND));

        if (order.getStatus() != OrderStatus.ORDER_PREPARING) {
            throw new AppException("Order must be in ORDER_PREPARING status to assign delivery staff", HttpStatus.BAD_REQUEST);
        }

        User deliveryStaff = userRepository.findById(staffId)
                .orElseThrow(() -> new AppException("Delivery staff not found with id: " + staffId, HttpStatus.NOT_FOUND));

        if (deliveryStaff.getRole() != Role.DELIVERY_STAFF) {
            throw new AppException("User with id " + staffId + " is not a delivery staff", HttpStatus.BAD_REQUEST);
        }

        OrderDeliveryMap deliveryMap = orderDeliveryMapRepository.findByOrderId(orderId)
                .orElseThrow(() -> new AppException("No delivery details found for order: " + orderId, HttpStatus.NOT_FOUND));
        deliveryMap.setDeliveryStaffId(staffId);
        orderDeliveryMapRepository.save(deliveryMap);

        order.setStatus(OrderStatus.ASSIGNED_DELIVERY_STAFF);
        Order updated = orderRepository.save(order);
        return mapToDetail(updated);
    }

    public OrderDetailDto cancelOrder(Long orderId) {
        log.info("Admin force cancelling order: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException("Order not found with id: " + orderId, HttpStatus.NOT_FOUND));

        if (!order.getStatus().isForceCancellable()) {
            throw new AppException("Order is already delivered or cancelled, cannot cancel.", HttpStatus.BAD_REQUEST);
        }
        order.setStatus(OrderStatus.ORDER_CANCELLED);
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
                order.getId(),
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
