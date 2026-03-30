package order.service;

import net.breezeware.Spring_Boot_Cafeteria.order.dto.OrderDetailDto;
import net.breezeware.Spring_Boot_Cafeteria.order.dto.OrderSummaryDetailDto;
import net.breezeware.Spring_Boot_Cafeteria.order.entity.Order;
import net.breezeware.Spring_Boot_Cafeteria.order.enumeration.OrderStatus;
import net.breezeware.Spring_Boot_Cafeteria.order.repo.OrderDeliveryMapRepository;
import net.breezeware.Spring_Boot_Cafeteria.order.repo.OrderRepository;
import net.breezeware.Spring_Boot_Cafeteria.order.service.AdminOrderService;
import net.breezeware.Spring_Boot_Cafeteria.user.entity.User;

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

        // assuming service filters only PLACED_ORDER
        assertEquals(3, result.size());

        OrderSummaryDetailDto dto1 = result.get(0);

        assertEquals(1L, dto1.getOrderId());
        assertEquals(101L, dto1.getUserId());
        assertEquals(OrderStatus.PLACED_ORDER, dto1.getStatus());
        assertEquals(0.0, dto1.getTotalPrice());

        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void getAllOrders_shouldReturnEmptyList_whenNoOrdersExist() {

        when(orderRepository.findAll()).thenReturn(List.of());

        List<OrderSummaryDetailDto> result = adminOrderService.getAllOrders();

        assertNotNull(result);
        assertTrue(result.isEmpty());

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

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adminOrderService.getOrderById(1L);
        });

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

        verify(orderRepository, times(1))
                .findByStatus(OrderStatus.PLACED_ORDER);
    }

    @Test
    void cancelOrder_shouldCancelOrder_whenOrderIsCancellable() {

        // Arrange
        order1.setStatus(OrderStatus.PLACED_ORDER); // cancellable

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order1));
        when(orderRepository.save(any(Order.class))).thenReturn(order1);

        // Act
        OrderDetailDto result = adminOrderService.cancelOrder(1L);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.ORDER_CANCELLED, result.getStatus());

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(order1);
    }

    @Test
    void cancelOrder_shouldCancelOrder_whenOrderIsForceCancelable() {

        // Arrange
        order1.setStatus(OrderStatus.ORDER_PREPARING); // isForceCcancellable

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order1));
        when(orderRepository.save(any(Order.class))).thenReturn(order1);

        // Act
        OrderDetailDto result = adminOrderService.cancelOrder(1L);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.ORDER_CANCELLED, result.getStatus());

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(order1);
    }


    @Test
    void cancelOrder_shouldNotCancelOrder_whenOrderIsDelivered() {

        // Arrange
        order1.setStatus(OrderStatus.ORDER_DELIVERED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order1));

        // Act + Assert
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

        // Act
        List<OrderSummaryDetailDto> result =
                adminOrderService.getOrdersByUserId(101L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(1L, result.get(0).getOrderId());
        assertEquals(4L, result.get(1).getOrderId());


        for (OrderSummaryDetailDto dto : result) {
            assertNotNull(dto.getOrderId());
        }

        // Verify
        verify(orderRepository, times(1)).findByUserId(101L);
    }

    @Test
    void getOrdersByUserId_shouldReturnEmptyList_whenUserHasNoOrders() {

        // Arrange
        when(orderRepository.findByUserId(101L)).thenReturn(List.of());

        // Act
        List<OrderSummaryDetailDto> result =
                adminOrderService.getOrdersByUserId(101L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify
        verify(orderRepository, times(1)).findByUserId(101L);
    }

    @Test
    void updateOrderStatus_shouldUpdateStatus_whenValidTransition() {

        // Arrange
        order1.setStatus(OrderStatus.PLACED_ORDER);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order1));
        when(orderRepository.save(any(Order.class))).thenReturn(order1);

        // Act
        OrderDetailDto result =
                adminOrderService.updateOrderStatus(1L, OrderStatus.ORDER_CONFIRMED);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.ORDER_CONFIRMED, result.getStatus());

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(order1);
    }


    @Test
    void updateOrderStatus_shouldThrowException_whenStatusIsCancelled() {

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                adminOrderService.updateOrderStatus(1L, OrderStatus.ORDER_CANCELLED)
        );

        assertEquals("Order not found with id: 1", exception.getMessage());

        verify(orderRepository, never()).findById(any());
        verify(orderRepository, never()).save(any());
    }
}



