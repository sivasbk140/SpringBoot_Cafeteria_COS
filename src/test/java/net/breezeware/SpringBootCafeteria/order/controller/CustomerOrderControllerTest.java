package net.breezeware.SpringBootCafeteria.order.controller;

import net.breezeware.SpringBootCafeteria.order.dto.CartItemDto;
import net.breezeware.SpringBootCafeteria.order.dto.OrderDeliveryRequest;
import net.breezeware.SpringBootCafeteria.order.dto.OrderDetailDto;
import net.breezeware.SpringBootCafeteria.order.dto.OrderRequestDto;
import static org.mockito.ArgumentMatchers.any;
import net.breezeware.SpringBootCafeteria.order.dto.OrderSummaryDetailDto;
import net.breezeware.SpringBootCafeteria.order.entity.OrderDeliveryMap;
import net.breezeware.SpringBootCafeteria.order.enumeration.OrderStatus;
import net.breezeware.SpringBootCafeteria.order.service.CustomerOrderService;
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
public class CustomerOrderControllerTest {

    @Mock
    private CustomerOrderService customerOrderService;

    @InjectMocks
    private CustomerOrderController customerOrderController;

    OrderSummaryDetailDto summaryDto1, summaryDto2, summaryDto3;
    OrderDetailDto orderDetailDto1, orderDetailDto2, orderDetailDto3;
    CartItemDto cartItemDto1, cartItemDto2;
    OrderDeliveryRequest deliveryRequest;
    OrderDeliveryMap deliveryMap;
    OrderRequestDto orderRequestDto;

    @BeforeEach
    void setUp() {

        summaryDto1 = new OrderSummaryDetailDto();
        summaryDto1.setOrderId(1);
        summaryDto1.setUserId(101);
        summaryDto1.setStatus(OrderStatus.PLACED_ORDER);
        summaryDto1.setTotalPrice(250.0);

        summaryDto2 = new OrderSummaryDetailDto();
        summaryDto2.setOrderId(2);
        summaryDto2.setUserId(101);
        summaryDto2.setStatus(OrderStatus.ORDER_DELIVERED);
        summaryDto2.setTotalPrice(150.0);

        summaryDto3 = new OrderSummaryDetailDto();
        summaryDto3.setOrderId(3);
        summaryDto3.setUserId(102);
        summaryDto3.setStatus(OrderStatus.ORDER_CANCELLED);
        summaryDto3.setTotalPrice(350.0);

        orderDetailDto1 = new OrderDetailDto();
        orderDetailDto1.setOrderId(1L);
        orderDetailDto1.setStatus(OrderStatus.PLACED_ORDER);
        orderDetailDto1.setTotalPrice(250.0);

        orderDetailDto2 = new OrderDetailDto();
        orderDetailDto2.setOrderId(2L);
        orderDetailDto2.setStatus(OrderStatus.ORDER_DELIVERED);
        orderDetailDto2.setTotalPrice(150.0);

        orderDetailDto3 = new OrderDetailDto();
        orderDetailDto3.setOrderId(3L);
        orderDetailDto3.setStatus(OrderStatus.ORDER_CANCELLED);
        orderDetailDto3.setTotalPrice(350.0);

        cartItemDto1 = new CartItemDto();
        cartItemDto1.setFoodItemId(1L);
        cartItemDto1.setFoodItemName("Burger");
        cartItemDto1.setQuantity(2L);

        cartItemDto2 = new CartItemDto();
        cartItemDto2.setFoodItemId(2L);
        cartItemDto2.setFoodItemName("Pizza");
        cartItemDto2.setQuantity(1L);

        deliveryRequest = new OrderDeliveryRequest();
        deliveryRequest.setName("John Doe");
        deliveryRequest.setPhone("9876543210");
        deliveryRequest.setAddress("123 Main St");

        deliveryMap = new OrderDeliveryMap();
        deliveryMap.setId(1L);
        deliveryMap.setName("John Doe");
        deliveryMap.setPhone("9876543210");
        deliveryMap.setAddress("123 Main St");

        orderRequestDto = new OrderRequestDto();
        orderRequestDto.setUser_id(101L);
    }

    // =========================================================================
    // ADD TO CART
    // =========================================================================

    @Test
    void addToCart_successCase() {
        when(customerOrderService.addToCart(101L, "Burger", 2))
                .thenReturn(List.of(cartItemDto1));

        ResponseEntity<List<CartItemDto>> response = customerOrderController.addToCart(101L, "Burger", 2);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Burger", response.getBody().get(0).getFoodItemName());
        assertEquals(2L, response.getBody().get(0).getQuantity());
        verify(customerOrderService, times(1)).addToCart(101L, "Burger", 2);
    }

    @Test
    void addToCart_multipleItemsInCart_successCase() {
        when(customerOrderService.addToCart(101L, "Pizza", 1))
                .thenReturn(List.of(cartItemDto1, cartItemDto2));

        ResponseEntity<List<CartItemDto>> response = customerOrderController.addToCart(101L, "Pizza", 1);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(customerOrderService, times(1)).addToCart(101L, "Pizza", 1);
    }

    @Test
    void addToCart_foodItemNotFound_throwsException() {
        when(customerOrderService.addToCart(101L, "UnknownItem", 1))
                .thenThrow(new RuntimeException("Food item not found"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> customerOrderController.addToCart(101L, "UnknownItem", 1));

        assertEquals("Food item not found", exception.getMessage());
        verify(customerOrderService, times(1)).addToCart(101L, "UnknownItem", 1);
    }

    @Test
    void addToCart_insufficientStock_throwsException() {
        when(customerOrderService.addToCart(101L, "Burger", 500))
                .thenThrow(new IllegalArgumentException("Insufficient stock"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> customerOrderController.addToCart(101L, "Burger", 500));

        assertEquals("Insufficient stock", exception.getMessage());
        verify(customerOrderService, times(1)).addToCart(101L, "Burger", 500);
    }

    // =========================================================================
    // VIEW CART
    // =========================================================================

    @Test
    void viewCart_successCase() {
        when(customerOrderService.viewCart(101L))
                .thenReturn(List.of(cartItemDto1, cartItemDto2));

        ResponseEntity<List<CartItemDto>> response = customerOrderController.viewCart(101L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Burger", response.getBody().get(0).getFoodItemName());
        assertEquals("Pizza", response.getBody().get(1).getFoodItemName());
        verify(customerOrderService, times(1)).viewCart(101L);
    }

    @Test
    void viewCart_emptyCart_returnsEmptyList() {
        when(customerOrderService.viewCart(101L)).thenReturn(List.of());

        ResponseEntity<List<CartItemDto>> response = customerOrderController.viewCart(101L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());
        verify(customerOrderService, times(1)).viewCart(101L);
    }

    @Test
    void viewCart_serviceThrowsException() {
        when(customerOrderService.viewCart(101L))
                .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> customerOrderController.viewCart(101L));

        assertEquals("Database error", exception.getMessage());
        verify(customerOrderService, times(1)).viewCart(101L);
    }

    // =========================================================================
    // REMOVE FROM CART
    // =========================================================================

    @Test
    void removeFromCart_successCase() {
        when(customerOrderService.removeFromCart(101L, "Burger"))
                .thenReturn(List.of(cartItemDto2));

        ResponseEntity<List<CartItemDto>> response = customerOrderController.removeFromCart(101L, "Burger");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Pizza", response.getBody().get(0).getFoodItemName());
        verify(customerOrderService, times(1)).removeFromCart(101L, "Burger");
    }

    @Test
    void removeFromCart_lastItem_returnsEmptyCart() {
        when(customerOrderService.removeFromCart(101L, "Burger"))
                .thenReturn(List.of());

        ResponseEntity<List<CartItemDto>> response = customerOrderController.removeFromCart(101L, "Burger");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
        verify(customerOrderService, times(1)).removeFromCart(101L, "Burger");
    }

    @Test
    void removeFromCart_serviceThrowsException() {
        when(customerOrderService.removeFromCart(101L, "Burger"))
                .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> customerOrderController.removeFromCart(101L, "Burger"));

        assertEquals("Database error", exception.getMessage());
        verify(customerOrderService, times(1)).removeFromCart(101L, "Burger");
    }

    // =========================================================================
    // CHECKOUT
    // =========================================================================

    @Test
    void checkout_successCase() {
        when(customerOrderService.checkout(101L, deliveryRequest))
                .thenReturn(orderDetailDto1);

        ResponseEntity<OrderDetailDto> response = customerOrderController.checkout(101L, deliveryRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getOrderId());
        assertEquals(OrderStatus.PLACED_ORDER, response.getBody().getStatus());
        verify(customerOrderService, times(1)).checkout(101L, deliveryRequest);
    }

    @Test
    void checkout_emptyCart_throwsException() {
        when(customerOrderService.checkout(101L, deliveryRequest))
                .thenThrow(new IllegalStateException("Cart is empty"));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> customerOrderController.checkout(101L, deliveryRequest));

        assertEquals("Cart is empty", exception.getMessage());
        verify(customerOrderService, times(1)).checkout(101L, deliveryRequest);
    }

    @Test
    void checkout_insufficientStock_throwsException() {
        when(customerOrderService.checkout(101L, deliveryRequest))
                .thenThrow(new IllegalArgumentException("Insufficient stock for item"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> customerOrderController.checkout(101L, deliveryRequest));

        assertEquals("Insufficient stock for item", exception.getMessage());
        verify(customerOrderService, times(1)).checkout(101L, deliveryRequest);
    }

    @Test
    void checkout_userNotFound_throwsException() {
        when(customerOrderService.checkout(999L, deliveryRequest))
                .thenThrow(new RuntimeException("User not found"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> customerOrderController.checkout(999L, deliveryRequest));

        assertEquals("User not found", exception.getMessage());
        verify(customerOrderService, times(1)).checkout(999L, deliveryRequest);
    }

    // =========================================================================
    // PLACE ORDER
    // =========================================================================

    @Test
    void placeOrder_successCase() {
        when(customerOrderService.placeOrder(any(OrderRequestDto.class)))
                .thenReturn(orderDetailDto1);

        ResponseEntity<OrderDetailDto> response = customerOrderController.placeOrder(orderRequestDto);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getOrderId());
        assertEquals(OrderStatus.PLACED_ORDER, response.getBody().getStatus());
        assertEquals(250.0, response.getBody().getTotalPrice());
        verify(customerOrderService, times(1)).placeOrder(any(OrderRequestDto.class));
    }

    @Test
    void placeOrder_emptyItems_throwsException() {
        when(customerOrderService.placeOrder(any(OrderRequestDto.class)))
                .thenThrow(new IllegalArgumentException("Order must have at least one item"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> customerOrderController.placeOrder(orderRequestDto));

        assertEquals("Order must have at least one item", exception.getMessage());
        verify(customerOrderService, times(1)).placeOrder(any(OrderRequestDto.class));
    }

    @Test
    void placeOrder_userNotFound_throwsException() {
        when(customerOrderService.placeOrder(any(OrderRequestDto.class)))
                .thenThrow(new RuntimeException("User not found"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> customerOrderController.placeOrder(orderRequestDto));

        assertEquals("User not found", exception.getMessage());
        verify(customerOrderService, times(1)).placeOrder(any(OrderRequestDto.class));
    }

    @Test
    void placeOrder_foodItemNotFound_throwsException() {
        when(customerOrderService.placeOrder(any(OrderRequestDto.class)))
                .thenThrow(new RuntimeException("Food item not found"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> customerOrderController.placeOrder(orderRequestDto));

        assertEquals("Food item not found", exception.getMessage());
        verify(customerOrderService, times(1)).placeOrder(any(OrderRequestDto.class));
    }

    // =========================================================================
    // GET MY ORDERS
    // =========================================================================

    @Test
    void getMyOrders_successCase() {
        when(customerOrderService.getMyOrders(101L))
                .thenReturn(List.of(summaryDto1, summaryDto2, summaryDto3));

        ResponseEntity<List<OrderSummaryDetailDto>> response = customerOrderController.getMyOrders(101L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getOrderId());
        assertEquals(2L, response.getBody().get(1).getOrderId());
        assertEquals(OrderStatus.PLACED_ORDER, response.getBody().get(0).getStatus());
        verify(customerOrderService, times(1)).getMyOrders(101L);
    }

    @Test
    void getMyOrders_noOrders_returnsEmptyList() {
        when(customerOrderService.getMyOrders(101L)).thenReturn(List.of());

        ResponseEntity<List<OrderSummaryDetailDto>> response = customerOrderController.getMyOrders(101L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
        verify(customerOrderService, times(1)).getMyOrders(101L);
    }

    @Test
    void getMyOrders_serviceThrowsException() {
        when(customerOrderService.getMyOrders(101L))
                .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> customerOrderController.getMyOrders(101L));

        assertEquals("Database error", exception.getMessage());
        verify(customerOrderService, times(1)).getMyOrders(101L);
    }

    // =========================================================================
    // GET ORDER DETAIL
    // =========================================================================

    @Test
    void getOrderDetail_successCase() {
        when(customerOrderService.getOrderDetail(1L, 101L)).thenReturn(orderDetailDto1);

        ResponseEntity<OrderDetailDto> response = customerOrderController.getOrderDetail(1L, 101L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getOrderId());
        assertEquals(OrderStatus.PLACED_ORDER, response.getBody().getStatus());
        assertEquals(250.0, response.getBody().getTotalPrice());
        verify(customerOrderService, times(1)).getOrderDetail(1L, 101L);
    }

    @Test
    void getOrderDetail_orderNotFound_throwsException() {
        when(customerOrderService.getOrderDetail(999L, 101L))
                .thenThrow(new RuntimeException("Order not found"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> customerOrderController.getOrderDetail(999L, 101L));

        assertEquals("Order not found", exception.getMessage());
        verify(customerOrderService, times(1)).getOrderDetail(999L, 101L);
    }

    @Test
    void getOrderDetail_accessDenied_throwsException() {
        when(customerOrderService.getOrderDetail(1L, 999L))
                .thenThrow(new RuntimeException("Access denied"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> customerOrderController.getOrderDetail(1L, 999L));

        assertEquals("Access denied", exception.getMessage());
        verify(customerOrderService, times(1)).getOrderDetail(1L, 999L);
    }

    // =========================================================================
    // CANCEL ORDER
    // =========================================================================

    @Test
    void cancelOrder_successCase() {
        when(customerOrderService.cancelOrder(3L, 102L)).thenReturn(orderDetailDto3);

        ResponseEntity<OrderDetailDto> response = customerOrderController.cancelOrder(3L, 102L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3L, response.getBody().getOrderId());
        assertEquals(OrderStatus.ORDER_CANCELLED, response.getBody().getStatus());
        verify(customerOrderService, times(1)).cancelOrder(3L, 102L);
    }

    @Test
    void cancelOrder_alreadyDelivered_throwsException() {
        when(customerOrderService.cancelOrder(2L, 101L))
                .thenThrow(new IllegalStateException("Cannot cancel order in current status"));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> customerOrderController.cancelOrder(2L, 101L));

        assertEquals("Cannot cancel order in current status", exception.getMessage());
        verify(customerOrderService, times(1)).cancelOrder(2L, 101L);
    }

    @Test
    void cancelOrder_orderNotFound_throwsException() {
        when(customerOrderService.cancelOrder(999L, 101L))
                .thenThrow(new RuntimeException("Order not found"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> customerOrderController.cancelOrder(999L, 101L));

        assertEquals("Order not found", exception.getMessage());
        verify(customerOrderService, times(1)).cancelOrder(999L, 101L);
    }

    @Test
    void cancelOrder_accessDenied_throwsException() {
        when(customerOrderService.cancelOrder(1L, 999L))
                .thenThrow(new RuntimeException("Access denied"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> customerOrderController.cancelOrder(1L, 999L));

        assertEquals("Access denied", exception.getMessage());
        verify(customerOrderService, times(1)).cancelOrder(1L, 999L);
    }

    // =========================================================================
    // ADD DELIVERY DETAILS
    // =========================================================================

    @Test
    void addDeliveryDetails_successCase() {
        when(customerOrderService.addDeliveryDetails(1L, 101L, deliveryRequest))
                .thenReturn(deliveryMap);

        ResponseEntity<OrderDeliveryMap> response =
                customerOrderController.addDeliveryDetails(1L, 101L, deliveryRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John Doe", response.getBody().getName());
        assertEquals("9876543210", response.getBody().getPhone());
        assertEquals("123 Main St", response.getBody().getAddress());
        verify(customerOrderService, times(1)).addDeliveryDetails(1L, 101L, deliveryRequest);
    }

    @Test
    void addDeliveryDetails_alreadyExists_throwsException() {
        when(customerOrderService.addDeliveryDetails(1L, 101L, deliveryRequest))
                .thenThrow(new IllegalStateException("Delivery details already exist for this order"));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> customerOrderController.addDeliveryDetails(1L, 101L, deliveryRequest));

        assertEquals("Delivery details already exist for this order", exception.getMessage());
        verify(customerOrderService, times(1)).addDeliveryDetails(1L, 101L, deliveryRequest);
    }

    @Test
    void addDeliveryDetails_orderNotFound_throwsException() {
        when(customerOrderService.addDeliveryDetails(999L, 101L, deliveryRequest))
                .thenThrow(new RuntimeException("Order not found"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> customerOrderController.addDeliveryDetails(999L, 101L, deliveryRequest));

        assertEquals("Order not found", exception.getMessage());
        verify(customerOrderService, times(1)).addDeliveryDetails(999L, 101L, deliveryRequest);
    }

    @Test
    void addDeliveryDetails_accessDenied_throwsException() {
        when(customerOrderService.addDeliveryDetails(1L, 999L, deliveryRequest))
                .thenThrow(new RuntimeException("Access denied"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> customerOrderController.addDeliveryDetails(1L, 999L, deliveryRequest));

        assertEquals("Access denied", exception.getMessage());
        verify(customerOrderService, times(1)).addDeliveryDetails(1L, 999L, deliveryRequest);
    }
}