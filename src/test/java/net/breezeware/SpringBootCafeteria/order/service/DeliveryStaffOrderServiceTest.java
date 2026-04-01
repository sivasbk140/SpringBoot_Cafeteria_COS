package net.breezeware.SpringBootCafeteria.order.service;

import net.breezeware.SpringBootCafeteria.order.dto.OrderDetailDto;
import net.breezeware.SpringBootCafeteria.order.dto.OrderSummaryDetailDto;
import net.breezeware.SpringBootCafeteria.order.entity.Order;
import net.breezeware.SpringBootCafeteria.order.entity.OrderDeliveryMap;
import net.breezeware.SpringBootCafeteria.order.enumeration.OrderStatus;
import net.breezeware.SpringBootCafeteria.order.repo.OrderDeliveryMapRepository;
import net.breezeware.SpringBootCafeteria.order.repo.OrderRepository;
import net.breezeware.SpringBootCafeteria.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DeliveryStaffOrderServiceTest {


    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderDeliveryMapRepository orderDeliveryMapRepository;

    @InjectMocks
    DeliveryStaffOrderService deliveryStaffOrderService;

    @Test
    void getAssignedOrders_shouldReturnOnlyAssignedOrders() {

        Long orderId = 1L;
        Long userId = 101L;
        Long staffId = 201L;

        User user = new User();
        user.setId(userId);

        Order order = new Order();
        order.setId(orderId);
        order.setUser(user);
        order.setStatus(OrderStatus.ASSIGNED_DELIVERY_STAFF);
        order.setCreatedOn(new Date());

        OrderDeliveryMap map1 = new OrderDeliveryMap();
        map1.setOrder(order);
        map1.setDeliveryStaffId(staffId);

        when(orderDeliveryMapRepository.findByDeliveryStaffId(staffId))
                .thenReturn(List.of(map1));

        // Act
        List<OrderSummaryDetailDto> result =
                deliveryStaffOrderService.getAssignedOrders(staffId);

        // Assert
        assertEquals(1, result.size());

        OrderSummaryDetailDto dto = result.get(0);
        assertEquals(1L, dto.getOrderId());
        assertEquals(OrderStatus.ASSIGNED_DELIVERY_STAFF, dto.getStatus());

        verify(orderDeliveryMapRepository).findByDeliveryStaffId(staffId);
    }

    @Test
    void markOrderDelivered_shouldThrowException_whenOrderNotFound() {

        OrderDeliveryMap map = new OrderDeliveryMap();
        map.setDeliveryStaffId(10L);

        when(orderDeliveryMapRepository.findByOrderId(1L))
                .thenReturn(Optional.of(map));

        when(orderRepository.findById(1L))
                .thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                deliveryStaffOrderService.markOrderDelivered(1L, 10L));


        assertEquals("Order not found with id: 1", ex.getMessage());
    }

    @Test
    void markOrderDelivered_shouldUpdateOrderStatus_toDelivered() {

        // Arrange
        Long orderId = 1L;
        Long staffId = 100L;

        // Create User
        User user = new User();
        user.setId(10L);
        user.setName("Siva");

        // Create Order
        Order order = new Order();
        order.setId(orderId);
        order.setUser(user);
        order.setStatus(OrderStatus.ASSIGNED_DELIVERY_STAFF);


        // Create Delivery Mapping
        OrderDeliveryMap map = new OrderDeliveryMap();
        map.setOrder(order);
        map.setDeliveryStaffId(staffId);

        when(orderDeliveryMapRepository.findByOrderId(orderId))
                .thenReturn(Optional.of(map));

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        OrderDetailDto result =
                deliveryStaffOrderService.markOrderDelivered(orderId, staffId);

        // Assert
        assertNotNull(result);


        assertEquals(OrderStatus.ORDER_DELIVERED, order.getStatus());

        verify(orderRepository).save(order);
    }


    @Test
    void markOrderDelivered_shouldThrowException_whenStatusInvalid() {

        Long orderId = 1L;

        User user = new User();
        user.setId(101L);

        Order order = new Order();
        order.setId(orderId);
        order.setUser(user);
        order.setStatus(OrderStatus.PLACED_ORDER);

        OrderDeliveryMap map = new OrderDeliveryMap();
        map.setDeliveryStaffId(10L);

        when(orderDeliveryMapRepository.findByOrderId(orderId))
                .thenReturn(Optional.of(map));

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                deliveryStaffOrderService.markOrderDelivered(orderId, 10L));
        assertEquals(
                "Order must be in ASSIGNED_DELIVERY_STAFF status to mark as delivered",
                ex.getMessage()
        );
    }
}
