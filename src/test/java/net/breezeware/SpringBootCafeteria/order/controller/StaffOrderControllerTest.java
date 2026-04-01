package net.breezeware.SpringBootCafeteria.order.controller;

import net.breezeware.SpringBootCafeteria.order.dto.OrderDetailDto;
import net.breezeware.SpringBootCafeteria.order.dto.OrderSummaryDetailDto;
import net.breezeware.SpringBootCafeteria.order.enumeration.OrderStatus;
import net.breezeware.SpringBootCafeteria.order.service.AdminOrderService;
import net.breezeware.SpringBootCafeteria.order.service.StaffOrderService;
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
public class StaffOrderControllerTest {


    @Mock
    private StaffOrderService staffOrderService;

    @InjectMocks
    private StaffOrderController staffOrderController;


    @Mock
    private AdminOrderService adminOrderService;

    @InjectMocks
    private AdminOrderController adminOrderController;

    OrderSummaryDetailDto summaryDto1, summaryDto2, summaryDto3;
    OrderDetailDto orderDetailDto1, orderDetailDto2;

    @BeforeEach
    void setUp() {

        summaryDto1 = new OrderSummaryDetailDto();
        summaryDto1.setOrderId(1);
        summaryDto1.setUserId(101);
        summaryDto1.setStatus(OrderStatus.PLACED_ORDER);
        summaryDto1.setTotalPrice(250.0);

        summaryDto2 = new OrderSummaryDetailDto();
        summaryDto2.setOrderId(2);
        summaryDto2.setUserId(102);
        summaryDto2.setStatus(OrderStatus.ORDER_DELIVERED);
        summaryDto2.setTotalPrice(150.0);

        summaryDto3 = new OrderSummaryDetailDto();
        summaryDto3.setOrderId(3);
        summaryDto3.setUserId(101);
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

    // ========================= ADMIN TESTS =========================

    @Test
    void getAllOrders_shouldReturn200_withListOfOrders() {

        when(adminOrderService.getAllOrders())
                .thenReturn(List.of(summaryDto1, summaryDto2));

        ResponseEntity<List<OrderSummaryDetailDto>> response =
                adminOrderController.getAllOrders();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());

        verify(adminOrderService).getAllOrders();
    }

    @Test
    void getOrderById_shouldReturn200_whenExists() {

        when(adminOrderService.getOrderById(1L)).thenReturn(orderDetailDto1);

        ResponseEntity<OrderDetailDto> response =
                adminOrderController.getOrderById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getOrderId());
    }

    @Test
    void getCompletedOrders_shouldReturnDeliveredOrders() {

        when(adminOrderService.getOrdersByStatus(OrderStatus.ORDER_DELIVERED))
                .thenReturn(List.of(summaryDto2));

        ResponseEntity<List<OrderSummaryDetailDto>> response =
                adminOrderController.getCompletedOrders();

        assertEquals(OrderStatus.ORDER_DELIVERED,
                response.getBody().get(0).getStatus());
    }

    @Test
    void getCancelledOrders_shouldReturnCancelledOrders() {

        when(adminOrderService.getOrdersByStatus(OrderStatus.ORDER_CANCELLED))
                .thenReturn(List.of(summaryDto3));

        ResponseEntity<List<OrderSummaryDetailDto>> response =
                adminOrderController.getCancelledOrders();

        assertEquals(OrderStatus.ORDER_CANCELLED,
                response.getBody().get(0).getStatus());
    }

    @Test
    void updateOrderStatus_shouldReturnUpdatedOrder() {

        orderDetailDto1.setStatus(OrderStatus.ORDER_CONFIRMED);

        when(adminOrderService.updateOrderStatus(1L, OrderStatus.ORDER_CONFIRMED))
                .thenReturn(orderDetailDto1);

        ResponseEntity<OrderDetailDto> response =
                adminOrderController.updateOrderStatus(1L, "ORDER_CONFIRMED");

        assertEquals(OrderStatus.ORDER_CONFIRMED,
                response.getBody().getStatus());
    }

    @Test
    void assignDeliveryStaff_shouldWork() {

        orderDetailDto1.setStatus(OrderStatus.ASSIGNED_DELIVERY_STAFF);

        when(adminOrderService.assignDeliveryStaff(1L, 5L))
                .thenReturn(orderDetailDto1);

        ResponseEntity<OrderDetailDto> response =
                adminOrderController.assignDeliveryStaff(1L, 5L);

        assertEquals(OrderStatus.ASSIGNED_DELIVERY_STAFF,
                response.getBody().getStatus());
    }

    @Test
    void cancelOrder_shouldWork() {

        orderDetailDto1.setStatus(OrderStatus.ORDER_CANCELLED);

        when(adminOrderService.cancelOrder(1L))
                .thenReturn(orderDetailDto1);

        ResponseEntity<OrderDetailDto> response =
                adminOrderController.cancelOrder(1L);

        assertEquals(OrderStatus.ORDER_CANCELLED,
                response.getBody().getStatus());
    }

    // ========================= STAFF TESTS =========================

    @Test
    void staff_getAllOrders_shouldReturnOrders() {

        when(staffOrderService.getAllOrders())
                .thenReturn(List.of(summaryDto1));

        ResponseEntity<List<OrderSummaryDetailDto>> response =
                staffOrderController.getAllOrders();

        assertEquals(1, response.getBody().size());
    }

    @Test
    void staff_getOrderById_shouldReturnOrder() {

        when(staffOrderService.getOrderById(1L))
                .thenReturn(orderDetailDto1);

        ResponseEntity<OrderDetailDto> response =
                staffOrderController.getOrderById(1L);

        assertEquals(1L, response.getBody().getOrderId());
    }

    @Test
    void staff_getOrdersByStatus_shouldWork() {

        when(staffOrderService.getOrdersByStatus(OrderStatus.PLACED_ORDER))
                .thenReturn(List.of(summaryDto1));

        ResponseEntity<List<OrderSummaryDetailDto>> response =
                staffOrderController.getOrdersByStatus("PLACED_ORDER");

        assertEquals(1, response.getBody().size());
    }

    @Test
    void staff_getOrdersByUser_shouldWork() {

        when(staffOrderService.getOrdersByUserId(101L))
                .thenReturn(List.of(summaryDto1, summaryDto3));

        ResponseEntity<List<OrderSummaryDetailDto>> response =
                staffOrderController.getOrdersByUser(101L);

        assertEquals(2, response.getBody().size());
    }
}