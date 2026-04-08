package net.breezeware.springbootcafeteria.order.service;

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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminOrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderDeliveryMapRepository orderDeliveryMapRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminOrderService adminOrderService;

    User user1, user2, user3, user4;
    Order order1, order2, order3, order4;

    @BeforeEach
    void setUp() {

        user1 = new User();
        user1.setId(101L);

        user2 = new User();
        user2.setId(102L);

        user3 = new User();
        user3.setId(103L);

        user4 = new User();
        user4.setId(104L);

        order1 = new Order();
        order1.setId(1L);
        order1.setUser(user1);
        order1.setStatus(OrderStatus.PLACED_ORDER);

        order2 = new Order();
        order2.setId(2L);
        order2.setUser(user2);
        order2.setStatus(OrderStatus.PLACED_ORDER);

        order3 = new Order();
        order3.setId(3L);
        order3.setUser(user3);
        order3.setStatus(OrderStatus.ORDER_DELIVERED);

        order4 = new Order();
        order4.setId(4L);
        order4.setUser(user1);
        order4.setStatus(OrderStatus.PLACED_ORDER);
    }

    @Test
    void getAllOrders_shouldReturnMappedOrderSummary() {

        when(orderRepository.findAll()).thenReturn(List.of(order1, order2, order3));

        List<OrderSummaryDetailDto> result = adminOrderService.getAllOrders();

        assertNotNull(result);
        assertEquals(3, result.size());

        OrderSummaryDetailDto dto1 = result.get(0);
        assertEquals(1L, dto1.getOrderId());
        assertEquals(101L, dto1.getUserId());
        assertEquals(OrderStatus.PLACED_ORDER, dto1.getStatus());
        assertEquals(0.0, dto1.getTotalPrice());

        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void getAllOrders_shouldThrowException_whenNoOrdersExist() {

        when(orderRepository.findAll()).thenReturn(List.of());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                adminOrderService.getAllOrders());

        assertEquals("No orders found in the system", exception.getMessage());

        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void getOrderById_shouldReturnOrder_whenOrderExists() {

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order1));

        OrderDetailDto result = adminOrderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getOrderId());
        assertEquals(OrderStatus.PLACED_ORDER, result.getStatus());

        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void getOrderById_shouldThrowException_whenOrderNotFound() {

        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> adminOrderService.getOrderById(1L));

        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void getOrdersByStatus_shouldReturnOrders_whenStatusIsPlaced() {

        when(orderRepository.findByStatus(OrderStatus.PLACED_ORDER))
                .thenReturn(List.of(order1, order2));

        List<OrderSummaryDetailDto> result =
                adminOrderService.getOrdersByStatus(OrderStatus.valueOf("PLACED_ORDER"));

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(OrderStatus.PLACED_ORDER, result.get(0).getStatus());
        assertEquals(OrderStatus.PLACED_ORDER, result.get(1).getStatus());

        verify(orderRepository, times(1)).findByStatus(OrderStatus.PLACED_ORDER);
    }

    @Test
    void cancelOrder_shouldCancelOrder_whenOrderIsCancellable() {

        order1.setStatus(OrderStatus.PLACED_ORDER);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order1));
        when(orderRepository.save(any(Order.class))).thenReturn(order1);

        OrderDetailDto result = adminOrderService.cancelOrder(1L);

        assertNotNull(result);
        assertEquals(OrderStatus.ORDER_CANCELLED, result.getStatus());

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(order1);
    }

    @Test
    void cancelOrder_shouldCancelOrder_whenOrderIsForceCancelable() {

        order1.setStatus(OrderStatus.ORDER_PREPARING);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order1));
        when(orderRepository.save(any(Order.class))).thenReturn(order1);

        OrderDetailDto result = adminOrderService.cancelOrder(1L);

        assertNotNull(result);
        assertEquals(OrderStatus.ORDER_CANCELLED, result.getStatus());

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(order1);
    }

    @Test
    void cancelOrder_shouldNotCancelOrder_whenOrderIsDelivered() {

        order1.setStatus(OrderStatus.ORDER_DELIVERED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order1));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> adminOrderService.cancelOrder(1L));

        assertEquals("Order is already delivered or cancelled, cannot cancel.",
                exception.getMessage());

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void getOrderByUserId_shouldReturnMultipleOrders_forSameUser() {

        when(orderRepository.findByUserId(101L))
                .thenReturn(List.of(order1, order4));

        List<OrderSummaryDetailDto> result =
                adminOrderService.getOrdersByUserId(101L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getOrderId());
        assertEquals(4L, result.get(1).getOrderId());

        verify(orderRepository, times(1)).findByUserId(101L);
    }

    @Test
    void getOrdersByUserId_shouldThrowException_whenUserHasNoOrders() {

        when(orderRepository.findByUserId(101L)).thenReturn(List.of());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                adminOrderService.getOrdersByUserId(101L));

        assertEquals("No orders found for the user with id: 101", exception.getMessage());

        verify(orderRepository, times(1)).findByUserId(101L);
    }

    @Test
    void updateOrderStatus_shouldUpdateStatus_whenValidTransition() {

        order1.setStatus(OrderStatus.PLACED_ORDER);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order1));
        when(orderRepository.save(any(Order.class))).thenReturn(order1);

        OrderDetailDto result =
                adminOrderService.updateOrderStatus(1L, OrderStatus.ORDER_CONFIRMED);

        assertNotNull(result);
        assertEquals(OrderStatus.ORDER_CONFIRMED, result.getStatus());

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(order1);
    }

    @Test
    void updateOrderStatus_shouldThrowException_whenOrderNotFound() {

        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                adminOrderService.updateOrderStatus(1L, OrderStatus.ORDER_CONFIRMED));

        assertEquals("Order not found with id: 1", exception.getMessage());

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void assignDeliveryStaff_success() {

        order1.setStatus(OrderStatus.ORDER_PREPARING);

        User deliveryStaff = new User();
        deliveryStaff.setId(200L);
        deliveryStaff.setRole(Role.DELIVERY_STAFF);

        OrderDeliveryMap deliveryMap = new OrderDeliveryMap();
        deliveryMap.setOrder(order1);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order1));
        when(userRepository.findById(200L)).thenReturn(Optional.of(deliveryStaff));
        when(orderDeliveryMapRepository.findByOrderId(1L)).thenReturn(Optional.of(deliveryMap));
        when(orderRepository.save(any(Order.class))).thenReturn(order1);

        OrderDetailDto result = adminOrderService.assignDeliveryStaff(1L, 200L);

        assertNotNull(result);
        assertEquals(OrderStatus.ASSIGNED_DELIVERY_STAFF, result.getStatus());
        assertEquals(200L, deliveryMap.getDeliveryStaffId());

        verify(userRepository, times(1)).findById(200L);
        verify(orderDeliveryMapRepository, times(1)).save(deliveryMap);
    }

    @Test
    void assignDeliveryStaff_shouldThrowException_whenDeliveryStaffNotFound() {

        order1.setStatus(OrderStatus.ORDER_PREPARING);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order1));
        when(userRepository.findById(200L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                adminOrderService.assignDeliveryStaff(1L, 200L));

        assertEquals("Delivery staff not found with id: 200", exception.getMessage());

        verify(userRepository, times(1)).findById(200L);
        verify(orderDeliveryMapRepository, never()).findByOrderId(any());
    }

    @Test
    void assignDeliveryStaff_shouldThrowException_whenUserIsNotDeliveryStaff() {

        order1.setStatus(OrderStatus.ORDER_PREPARING);

        User nonDeliveryUser = new User();
        nonDeliveryUser.setId(200L);
        nonDeliveryUser.setRole(Role.CUSTOMER);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order1));
        when(userRepository.findById(200L)).thenReturn(Optional.of(nonDeliveryUser));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                adminOrderService.assignDeliveryStaff(1L, 200L));

        assertEquals("User with id 200 is not a delivery staff", exception.getMessage());

        verify(userRepository, times(1)).findById(200L);
        verify(orderDeliveryMapRepository, never()).findByOrderId(any());
    }
}
