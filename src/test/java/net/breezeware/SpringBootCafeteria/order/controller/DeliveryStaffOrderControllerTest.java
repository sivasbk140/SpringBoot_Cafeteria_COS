
package net.breezeware.SpringBootCafeteria.order.controller;

import net.breezeware.SpringBootCafeteria.order.dto.OrderDetailDto;
import net.breezeware.SpringBootCafeteria.order.dto.OrderSummaryDetailDto;
import net.breezeware.SpringBootCafeteria.order.enumeration.OrderStatus;
import net.breezeware.SpringBootCafeteria.order.service.DeliveryStaffOrderService;
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
class DeliveryStaffOrderControllerTest {

    @Mock
    private DeliveryStaffOrderService deliveryStaffOrderService;

    @InjectMocks
    private DeliveryStaffOrderController deliveryStaffOrderController;

    // =========================================================
    // getAssignedOrders - SUCCESS
    // =========================================================

    @Test
    void getAssignedOrders_shouldReturnAssignedOrders() {

        Long staffId = 201L;

        OrderSummaryDetailDto dto = new OrderSummaryDetailDto();
        dto.setOrderId(1);
        dto.setStatus(OrderStatus.ASSIGNED_DELIVERY_STAFF);

        when(deliveryStaffOrderService.getAssignedOrders(staffId))
                .thenReturn(List.of(dto));

        ResponseEntity<List<OrderSummaryDetailDto>> response =
                deliveryStaffOrderController.getAssignedOrders(staffId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(OrderStatus.ASSIGNED_DELIVERY_STAFF,
                response.getBody().get(0).getStatus());

        verify(deliveryStaffOrderService).getAssignedOrders(staffId);
    }

    // =========================================================
    // getAssignedOrders - EMPTY LIST
    // =========================================================

    @Test
    void getAssignedOrders_shouldReturnEmptyList_whenNoOrders() {

        Long staffId = 201L;

        when(deliveryStaffOrderService.getAssignedOrders(staffId))
                .thenReturn(List.of());

        ResponseEntity<List<OrderSummaryDetailDto>> response =
                deliveryStaffOrderController.getAssignedOrders(staffId);

        assertTrue(response.getBody().isEmpty());

        verify(deliveryStaffOrderService).getAssignedOrders(staffId);
    }

    // =========================================================
    // getAssignedOrders - FAILURE
    // =========================================================

    @Test
    void getAssignedOrders_shouldThrowException_whenServiceFails() {

        Long staffId = 201L;

        when(deliveryStaffOrderService.getAssignedOrders(staffId))
                .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> deliveryStaffOrderController.getAssignedOrders(staffId));

        assertEquals("Database error", exception.getMessage());

        verify(deliveryStaffOrderService).getAssignedOrders(staffId);
    }

    // =========================================================
    // markOrderDelivered - SUCCESS
    // =========================================================

    @Test
    void markOrderDelivered_shouldReturnUpdatedOrder() {

        Long orderId = 1L;
        Long staffId = 201L;

        OrderDetailDto dto = new OrderDetailDto();
        dto.setOrderId(orderId);
        dto.setStatus(OrderStatus.ORDER_DELIVERED);

        when(deliveryStaffOrderService.markOrderDelivered(orderId, staffId))
                .thenReturn(dto);

        ResponseEntity<OrderDetailDto> response =
                deliveryStaffOrderController.markOrderDelivered(orderId, staffId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(OrderStatus.ORDER_DELIVERED,
                response.getBody().getStatus());

        verify(deliveryStaffOrderService)
                .markOrderDelivered(orderId, staffId);
    }

    // =========================================================
    // markOrderDelivered - FAILURE (NOT ASSIGNED / INVALID)
    // =========================================================

    @Test
    void markOrderDelivered_shouldThrowException_whenNotAssigned() {

        Long orderId = 1L;
        Long staffId = 201L;

        when(deliveryStaffOrderService.markOrderDelivered(orderId, staffId))
                .thenThrow(new RuntimeException("Order not assigned to this staff"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> deliveryStaffOrderController.markOrderDelivered(orderId, staffId));

        assertEquals("Order not assigned to this staff", exception.getMessage());

        verify(deliveryStaffOrderService)
                .markOrderDelivered(orderId, staffId);
    }

    // =========================================================
    // markOrderDelivered - FAILURE (ORDER NOT FOUND)
    // =========================================================

    @Test
    void markOrderDelivered_shouldThrowException_whenOrderNotFound() {

        Long orderId = 99L;
        Long staffId = 201L;

        when(deliveryStaffOrderService.markOrderDelivered(orderId, staffId))
                .thenThrow(new RuntimeException("Order not found"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> deliveryStaffOrderController.markOrderDelivered(orderId, staffId));

        assertEquals("Order not found", exception.getMessage());

        verify(deliveryStaffOrderService)
                .markOrderDelivered(orderId, staffId);
    }
}
