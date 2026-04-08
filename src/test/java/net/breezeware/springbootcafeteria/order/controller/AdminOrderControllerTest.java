package net.breezeware.springbootcafeteria.order.controller;

import net.breezeware.springbootcafeteria.order.dto.OrderDetailDto;
import net.breezeware.springbootcafeteria.order.dto.OrderSummaryDetailDto;
import net.breezeware.springbootcafeteria.order.enumeration.OrderStatus;
import net.breezeware.springbootcafeteria.order.service.AdminOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminOrderControllerTest {

    @Mock
    private AdminOrderService adminOrderService;

    @InjectMocks
    private AdminOrderController adminOrderController;

    OrderSummaryDetailDto summaryDto1, summaryDto2, summaryDto3;
    OrderDetailDto orderDetailDto1, orderDetailDto2;

    @BeforeEach
    void setUp() {

        summaryDto1 = new OrderSummaryDetailDto();
        summaryDto1.setOrderId(1L);
        summaryDto1.setUserId(101L);
        summaryDto1.setStatus(OrderStatus.PLACED_ORDER);
        summaryDto1.setTotalPrice(250.0);

        summaryDto2 = new OrderSummaryDetailDto();
        summaryDto2.setOrderId(2L);
        summaryDto2.setUserId(102L);
        summaryDto2.setStatus(OrderStatus.ORDER_DELIVERED);
        summaryDto2.setTotalPrice(150.0);

        summaryDto3 = new OrderSummaryDetailDto();
        summaryDto3.setOrderId(3L);
        summaryDto3.setUserId(101L);
        summaryDto3.setStatus(OrderStatus.ORDER_CANCELLED);
        summaryDto3.setTotalPrice(100.0);

        orderDetailDto1 = new OrderDetailDto();
        orderDetailDto1.setOrderId(1L);
        orderDetailDto1.setStatus(OrderStatus.PLACED_ORDER);
        orderDetailDto1.setTotalPrice(250.0);

        orderDetailDto2 = new OrderDetailDto();
        orderDetailDto2.setOrderId(2L);
        orderDetailDto2.setStatus(OrderStatus.ORDER_DELIVERED);
        orderDetailDto2.setTotalPrice(150.0);
    }


    @Test
    void getAllOrders_shouldReturn200_withListOfOrders() {

        when(adminOrderService.getAllOrders()).thenReturn(List.of(summaryDto1, summaryDto2));

        ResponseEntity<List<OrderSummaryDetailDto>> response = adminOrderController.getAllOrders();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getOrderId());
        assertEquals(2L, response.getBody().get(1).getOrderId());

        verify(adminOrderService, times(1)).getAllOrders();
    }

    @Test
    void getAllOrders_shouldReturn200_withEmptyList_whenNoOrdersExist() {

        when(adminOrderService.getAllOrders()).thenReturn(List.of());

        ResponseEntity<List<OrderSummaryDetailDto>> response = adminOrderController.getAllOrders();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(adminOrderService, times(1)).getAllOrders();
    }

    @Test
    void getAllOrders_shouldThrowException_whenServiceFails() {

        when(adminOrderService.getAllOrders()).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> adminOrderController.getAllOrders());

        assertEquals("Database error", exception.getMessage());

        verify(adminOrderService, times(1)).getAllOrders();
    }


    @Test
    void getOrderById_shouldReturn200_withOrderDetail_whenOrderExists() {

        when(adminOrderService.getOrderById(1L)).thenReturn(orderDetailDto1);

        ResponseEntity<OrderDetailDto> response = adminOrderController.getOrderById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getOrderId());
        assertEquals(OrderStatus.PLACED_ORDER, response.getBody().getStatus());

        verify(adminOrderService, times(1)).getOrderById(1L);
    }

    @Test
    void getOrderById_shouldThrowException_whenOrderNotFound() {

        when(adminOrderService.getOrderById(99L))
                .thenThrow(new RuntimeException("Order not found with id: 99"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> adminOrderController.getOrderById(99L));

        assertEquals("Order not found with id: 99", exception.getMessage());

        verify(adminOrderService, times(1)).getOrderById(99L);
    }


    @Test
    void getCompletedOrders_shouldReturn200_withDeliveredOrders() {

        when(adminOrderService.getOrdersByStatus(OrderStatus.ORDER_DELIVERED))
                .thenReturn(List.of(summaryDto2));

        ResponseEntity<List<OrderSummaryDetailDto>> response = adminOrderController.getCompletedOrders();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(OrderStatus.ORDER_DELIVERED, response.getBody().get(0).getStatus());

        verify(adminOrderService, times(1)).getOrdersByStatus(OrderStatus.ORDER_DELIVERED);
    }

    @Test
    void getCompletedOrders_shouldReturn200_withEmptyList_whenNoCompletedOrders() {

        when(adminOrderService.getOrdersByStatus(OrderStatus.ORDER_DELIVERED))
                .thenReturn(List.of());

        ResponseEntity<List<OrderSummaryDetailDto>> response = adminOrderController.getCompletedOrders();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());

        verify(adminOrderService, times(1)).getOrdersByStatus(OrderStatus.ORDER_DELIVERED);
    }

    @Test
    void getCompletedOrders_shouldThrowException_whenServiceFails() {

        when(adminOrderService.getOrdersByStatus(OrderStatus.ORDER_DELIVERED))
                .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> adminOrderController.getCompletedOrders());

        assertEquals("Database error", exception.getMessage());

        verify(adminOrderService, times(1)).getOrdersByStatus(OrderStatus.ORDER_DELIVERED);
    }


    @Test
    void getCancelledOrders_shouldReturn200_withCancelledOrders() {

        when(adminOrderService.getOrdersByStatus(OrderStatus.ORDER_CANCELLED))
                .thenReturn(List.of(summaryDto3));

        ResponseEntity<List<OrderSummaryDetailDto>> response = adminOrderController.getCancelledOrders();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(OrderStatus.ORDER_CANCELLED, response.getBody().get(0).getStatus());

        verify(adminOrderService, times(1)).getOrdersByStatus(OrderStatus.ORDER_CANCELLED);
    }

    @Test
    void getCancelledOrders_shouldReturn200_withEmptyList_whenNoCancelledOrders() {

        when(adminOrderService.getOrdersByStatus(OrderStatus.ORDER_CANCELLED))
                .thenReturn(List.of());

        ResponseEntity<List<OrderSummaryDetailDto>> response = adminOrderController.getCancelledOrders();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());

        verify(adminOrderService, times(1)).getOrdersByStatus(OrderStatus.ORDER_CANCELLED);
    }

    @Test
    void getCancelledOrders_shouldThrowException_whenServiceFails() {

        when(adminOrderService.getOrdersByStatus(OrderStatus.ORDER_CANCELLED))
                .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> adminOrderController.getCancelledOrders());

        assertEquals("Database error", exception.getMessage());

        verify(adminOrderService, times(1)).getOrdersByStatus(OrderStatus.ORDER_CANCELLED);
    }


    @Test
    void getOrdersByStatus_shouldReturn200_withMatchingOrders_whenStatusIsValid() {

        when(adminOrderService.getOrdersByStatus(OrderStatus.PLACED_ORDER))
                .thenReturn(List.of(summaryDto1));

        ResponseEntity<List<OrderSummaryDetailDto>> response =
                adminOrderController.getOrdersByStatus("PLACED_ORDER");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(OrderStatus.PLACED_ORDER, response.getBody().get(0).getStatus());

        verify(adminOrderService, times(1)).getOrdersByStatus(OrderStatus.PLACED_ORDER);
    }

    @Test
    void getOrdersByStatus_shouldReturn200_withEmptyList_whenNoOrdersMatchStatus() {

        when(adminOrderService.getOrdersByStatus(OrderStatus.ORDER_PREPARING))
                .thenReturn(List.of());

        ResponseEntity<List<OrderSummaryDetailDto>> response =
                adminOrderController.getOrdersByStatus("ORDER_PREPARING");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());

        verify(adminOrderService, times(1)).getOrdersByStatus(OrderStatus.ORDER_PREPARING);
    }

    @Test
    void getOrdersByStatus_shouldThrowException_whenStatusStringIsInvalid() {

        // OrderStatus.fromString returns null for unknown values → controller throws RuntimeException
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> adminOrderController.getOrdersByStatus("INVALID_STATUS"));

        assertEquals("Invalid order status: INVALID_STATUS", exception.getMessage());

        verify(adminOrderService, never()).getOrdersByStatus(any());
    }

    @Test
    void getOrdersByStatus_shouldThrowException_whenServiceFails() {

        when(adminOrderService.getOrdersByStatus(OrderStatus.PLACED_ORDER))
                .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> adminOrderController.getOrdersByStatus("PLACED_ORDER"));

        assertEquals("Database error", exception.getMessage());

        verify(adminOrderService, times(1)).getOrdersByStatus(OrderStatus.PLACED_ORDER);
    }


    @Test
    void getOrdersByUser_shouldReturn200_withOrdersForGivenUser() {

        when(adminOrderService.getOrdersByUserId(101L))
                .thenReturn(List.of(summaryDto1, summaryDto3));

        ResponseEntity<List<OrderSummaryDetailDto>> response =
                adminOrderController.getOrdersByUser(101L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getOrderId());
        assertEquals(3L, response.getBody().get(1).getOrderId());

        verify(adminOrderService, times(1)).getOrdersByUserId(101L);
    }

    @Test
    void getOrdersByUser_shouldReturn200_withEmptyList_whenUserHasNoOrders() {

        when(adminOrderService.getOrdersByUserId(999L)).thenReturn(List.of());

        ResponseEntity<List<OrderSummaryDetailDto>> response =
                adminOrderController.getOrdersByUser(999L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());

        verify(adminOrderService, times(1)).getOrdersByUserId(999L);
    }

    @Test
    void getOrdersByUser_shouldThrowException_whenServiceFails() {

        when(adminOrderService.getOrdersByUserId(101L))
                .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> adminOrderController.getOrdersByUser(101L));

        assertEquals("Database error", exception.getMessage());

        verify(adminOrderService, times(1)).getOrdersByUserId(101L);
    }


    @Test
    void updateOrderStatus_shouldReturn200_withUpdatedOrder_whenStatusIsValid() {

        orderDetailDto1.setStatus(OrderStatus.ORDER_CONFIRMED);

        when(adminOrderService.updateOrderStatus(1L, OrderStatus.ORDER_CONFIRMED))
                .thenReturn(orderDetailDto1);

        ResponseEntity<OrderDetailDto> response =
                adminOrderController.updateOrderStatus(1L, "ORDER_CONFIRMED");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getOrderId());
        assertEquals(OrderStatus.ORDER_CONFIRMED, response.getBody().getStatus());

        verify(adminOrderService, times(1)).updateOrderStatus(1L, OrderStatus.ORDER_CONFIRMED);
    }

    @Test
    void updateOrderStatus_shouldThrowException_whenStatusStringIsInvalid() {

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> adminOrderController.updateOrderStatus(1L, "INVALID_STATUS"));

        assertEquals("Invalid order status: INVALID_STATUS", exception.getMessage());

        verify(adminOrderService, never()).updateOrderStatus(anyLong(), any());
    }

    @Test
    void updateOrderStatus_shouldThrowException_whenOrderNotFound() {

        when(adminOrderService.updateOrderStatus(99L, OrderStatus.ORDER_CONFIRMED))
                .thenThrow(new RuntimeException("Order not found with id: 99"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> adminOrderController.updateOrderStatus(99L, "ORDER_CONFIRMED"));

        assertEquals("Order not found with id: 99", exception.getMessage());

        verify(adminOrderService, times(1)).updateOrderStatus(99L, OrderStatus.ORDER_CONFIRMED);
    }


    @Test
    void assignDeliveryStaff_shouldReturn200_withUpdatedOrder_whenAssignmentSucceeds() {

        orderDetailDto1.setStatus(OrderStatus.ASSIGNED_DELIVERY_STAFF);

        when(adminOrderService.assignDeliveryStaff(1L, 5L)).thenReturn(orderDetailDto1);

        ResponseEntity<OrderDetailDto> response =
                adminOrderController.assignDeliveryStaff(1L, 5L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getOrderId());
        assertEquals(OrderStatus.ASSIGNED_DELIVERY_STAFF, response.getBody().getStatus());

        verify(adminOrderService, times(1)).assignDeliveryStaff(1L, 5L);
    }

    @Test
    void assignDeliveryStaff_shouldThrowException_whenOrderNotInPreparingStatus() {

        when(adminOrderService.assignDeliveryStaff(1L, 5L))
                .thenThrow(new RuntimeException("Order must be in ORDER_PREPARING status to assign staff"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> adminOrderController.assignDeliveryStaff(1L, 5L));

        assertEquals("Order must be in ORDER_PREPARING status to assign staff", exception.getMessage());

        verify(adminOrderService, times(1)).assignDeliveryStaff(1L, 5L);
    }

    @Test
    void assignDeliveryStaff_shouldThrowException_whenOrderNotFound() {

        when(adminOrderService.assignDeliveryStaff(99L, 5L))
                .thenThrow(new RuntimeException("Order not found with id: 99"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> adminOrderController.assignDeliveryStaff(99L, 5L));

        assertEquals("Order not found with id: 99", exception.getMessage());

        verify(adminOrderService, times(1)).assignDeliveryStaff(99L, 5L);
    }


    @Test
    void cancelOrder_shouldReturn200_withCancelledOrder_whenOrderIsPlaced() {

        orderDetailDto1.setStatus(OrderStatus.ORDER_CANCELLED);

        when(adminOrderService.cancelOrder(1L)).thenReturn(orderDetailDto1);

        ResponseEntity<OrderDetailDto> response = adminOrderController.cancelOrder(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getOrderId());
        assertEquals(OrderStatus.ORDER_CANCELLED, response.getBody().getStatus());

        verify(adminOrderService, times(1)).cancelOrder(1L);
    }

    @Test
    void cancelOrder_shouldThrowException_whenOrderIsAlreadyDelivered() {

        when(adminOrderService.cancelOrder(2L))
                .thenThrow(new RuntimeException("Order is already delivered or cancelled, cannot cancel."));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> adminOrderController.cancelOrder(2L));

        assertEquals("Order is already delivered or cancelled, cannot cancel.", exception.getMessage());

        verify(adminOrderService, times(1)).cancelOrder(2L);
    }

    @Test
    void cancelOrder_shouldThrowException_whenOrderNotFound() {

        when(adminOrderService.cancelOrder(99L))
                .thenThrow(new RuntimeException("Order not found with id: 99"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> adminOrderController.cancelOrder(99L));

        assertEquals("Order not found with id: 99", exception.getMessage());

        verify(adminOrderService, times(1)).cancelOrder(99L);
    }
}