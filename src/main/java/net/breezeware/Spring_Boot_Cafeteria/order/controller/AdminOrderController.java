package net.breezeware.Spring_Boot_Cafeteria.order.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.breezeware.Spring_Boot_Cafeteria.order.dto.OrderDetailDto;
import net.breezeware.Spring_Boot_Cafeteria.order.dto.OrderSummaryDetailDto;
import net.breezeware.Spring_Boot_Cafeteria.order.enumeration.OrderStatus;
import net.breezeware.Spring_Boot_Cafeteria.order.service.AdminOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    @GetMapping
    public ResponseEntity<List<OrderSummaryDetailDto>> getAllOrders() {
        log.info("GET /api/admin/orders - fetching all orders");
        return ResponseEntity.ok(adminOrderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailDto> getOrderById(@PathVariable Long id) {
        log.info("GET /api/admin/orders/{} - fetching order by id", id);
        return ResponseEntity.ok(adminOrderService.getOrderById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderSummaryDetailDto>> getOrdersByStatus(@PathVariable String status) {
        log.info("GET /api/admin/orders/status/{} - fetching orders by status", status);
        OrderStatus orderStatus = OrderStatus.fromString(status);
        if (orderStatus == null) {
            throw new RuntimeException("Invalid order status: " + status);
        }
        return ResponseEntity.ok(adminOrderService.getOrdersByStatus(orderStatus));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderSummaryDetailDto>> getOrdersByUser(@PathVariable Long userId) {
        log.info("GET /api/admin/orders/user/{} - fetching orders by user", userId);
        return ResponseEntity.ok(adminOrderService.getOrdersByUserId(userId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderDetailDto> updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        log.info("PATCH /api/admin/orders/{}/status - updating to {}", id, status);
        OrderStatus orderStatus = OrderStatus.fromString(status);
        if (orderStatus == null) {
            throw new RuntimeException("Invalid order status: " + status);
        }
        return ResponseEntity.ok(adminOrderService.updateOrderStatus(id, orderStatus));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OrderDetailDto> cancelOrder(@PathVariable Long id) {
        log.info("PATCH /api/admin/orders/{}/cancel - cancelling order", id);
        return ResponseEntity.ok(adminOrderService.cancelOrder(id));
    }
}