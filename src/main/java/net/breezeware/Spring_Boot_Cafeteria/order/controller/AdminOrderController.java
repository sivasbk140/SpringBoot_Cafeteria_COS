package net.breezeware.Spring_Boot_Cafeteria.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@CrossOrigin(origins = "*")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    @Operation(summary = "Get all orders", description = "Returns a summary list of all orders in the system")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders retrieved",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = OrderSummaryDetailDto.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<OrderSummaryDetailDto>> getAllOrders() {
        log.info("GET /api/admin/orders - fetching all orders");
        return ResponseEntity.ok(adminOrderService.getAllOrders());
    }

    @Operation(summary = "Get order by ID", description = "Returns full details of a specific order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDetailDto.class))),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailDto> getOrderById(@PathVariable Long id) {
        log.info("GET /api/admin/orders/{} - fetching order by id", id);
        return ResponseEntity.ok(adminOrderService.getOrderById(id));
    }

    @Operation(summary = "Get orders by status", description = "Returns all orders filtered by the given status (e.g. PLACED_ORDER, ORDER_DELIVERED)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders retrieved",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = OrderSummaryDetailDto.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid order status", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderSummaryDetailDto>> getOrdersByStatus(@PathVariable String status) {
        log.info("GET /api/admin/orders/status/{} - fetching orders by status", status);
        OrderStatus orderStatus = OrderStatus.fromString(status);
        if (orderStatus == null) {
            throw new RuntimeException("Invalid order status: " + status);
        }
        return ResponseEntity.ok(adminOrderService.getOrdersByStatus(orderStatus));
    }

    @Operation(summary = "Get orders by user", description = "Returns all orders placed by a specific user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders retrieved",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = OrderSummaryDetailDto.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderSummaryDetailDto>> getOrdersByUser(@PathVariable Long userId) {
        log.info("GET /api/admin/orders/user/{} - fetching orders by user", userId);
        return ResponseEntity.ok(adminOrderService.getOrdersByUserId(userId));
    }

    @Operation(summary = "Update order status", description = "Updates the status of an order to any valid OrderStatus value")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order status updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDetailDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid order status", content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderDetailDto> updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        log.info("PATCH /api/admin/orders/{}/status - updating to {}", id, status);
        OrderStatus orderStatus = OrderStatus.fromString(status);
        if (orderStatus == null) {
            throw new RuntimeException("Invalid order status: " + status);
        }
        return ResponseEntity.ok(adminOrderService.updateOrderStatus(id, orderStatus));
    }

    @Operation(summary = "Cancel order", description = "Cancels an order. Only orders in PLACED_ORDER status can be cancelled.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order cancelled",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDetailDto.class))),
            @ApiResponse(responseCode = "400", description = "Order cannot be cancelled in current status", content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OrderDetailDto> cancelOrder(@PathVariable Long id) {
        log.info("PATCH /api/admin/orders/{}/cancel - cancelling order", id);
        return ResponseEntity.ok(adminOrderService.cancelOrder(id));
    }
}