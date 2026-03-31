package order.controller;

import net.breezeware.Spring_Boot_Cafeteria.order.controller.AdminOrderController;
import net.breezeware.Spring_Boot_Cafeteria.order.dto.OrderDetailDto;
import net.breezeware.Spring_Boot_Cafeteria.order.dto.OrderSummaryDetailDto;
import net.breezeware.Spring_Boot_Cafeteria.order.enumeration.OrderStatus;
import net.breezeware.Spring_Boot_Cafeteria.order.service.AdminOrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminOrderControllerTest {
@Mock
    private AdminOrderService adminOrderService;

@InjectMocks
    private AdminOrderController adminOrderController;


    @Test
    void getAllOrders_successCase() {
        // Arrange
        List<OrderSummaryDetailDto> orderList = new ArrayList<>();
        OrderSummaryDetailDto dto = new OrderSummaryDetailDto();
        dto.setOrderId(1);
        dto.setStatus(OrderStatus.ASSIGNED_DELIVERY_STAFF);
        orderList.add(dto);

        when(adminOrderService.getAllOrders()).thenReturn(orderList);

        // Act
        ResponseEntity<List<OrderSummaryDetailDto>> response = adminOrderController.getAllOrders();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getOrderId());

        // Verify service called
        verify(adminOrderService).getAllOrders();
    }



    @Test
    void getAllOrders_ShouldReturnCompletedOrders() {

        // Arrange
        List<OrderSummaryDetailDto> orderList = new ArrayList<>();
        OrderSummaryDetailDto dto = new OrderSummaryDetailDto();
        dto.setOrderId(1); // int matches DTO
        dto.setStatus(OrderStatus.ORDER_DELIVERED);
        orderList.add(dto);

        when(adminOrderService.getAllOrders()).thenReturn(orderList); // mock existing method

        // Act
        ResponseEntity<List<OrderSummaryDetailDto>> response = adminOrderController.getAllOrders();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(1, response.getBody().get(0).getOrderId());
        assertEquals(OrderStatus.ORDER_DELIVERED, response.getBody().get(0).getStatus());

        // Verify
        verify(adminOrderService).getAllOrders();
    }


    @Test
    void getOrderById_successCase() {

        // Arrange
        OrderDetailDto dto = new OrderDetailDto();
        dto.setOrderId(1);
        dto.setStatus(OrderStatus.ORDER_DELIVERED);

        when(adminOrderService.getOrderById(1L)).thenReturn(dto);

        // Act
        ResponseEntity<OrderDetailDto> response =
                adminOrderController.getOrderById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        assertEquals(1, response.getBody().getOrderId());
        assertEquals(OrderStatus.ORDER_DELIVERED, response.getBody().getStatus());

        // Verify
        verify(adminOrderService).getOrderById(1L);
    }




    @Test
    void getCancelledOrders_successCase() {

        // Arrange
        List<OrderSummaryDetailDto> orderList = new ArrayList<>();
        OrderSummaryDetailDto dto = new OrderSummaryDetailDto();
        dto.setOrderId(1);
        dto.setStatus(OrderStatus.ORDER_CANCELLED);
        orderList.add(dto);

        when(adminOrderService.getOrdersByStatus(OrderStatus.ORDER_CANCELLED))
                .thenReturn(orderList);

        // Act
        ResponseEntity<List<OrderSummaryDetailDto>> response =
                adminOrderController.getCancelledOrders();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(OrderStatus.ORDER_CANCELLED,
                response.getBody().get(0).getStatus());

        // Verify
        verify(adminOrderService).getOrdersByStatus(OrderStatus.ORDER_CANCELLED);
    }

    }

