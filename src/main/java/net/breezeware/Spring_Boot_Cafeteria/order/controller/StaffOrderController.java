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
import net.breezeware.Spring_Boot_Cafeteria.order.service.StaffOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * STAFF ORDER CONTROLLER
 * Endpoints for staff to view and update order delivery status
 */
@Slf4j
@RestController
@RequestMapping("/api/staff/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StaffOrderController {

    private final StaffOrderService staffOrderService;

    @Operation(summary = "Get all orders", description = "Returns a summary of all orders for staff to manage")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders retrieved",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = OrderSummaryDetailDto.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<OrderSummaryDetailDto>> getAllOrders() {
        log.info("GET /api/staff/orders - fetching all orders");
        return ResponseEntity.ok(staffOrderService.getAllOrders());
    }

    @Operation(summary = "Get orders by status", description = "Returns orders filtered by status (e.g. PLACED_ORDER, WAITING_FOR_DELIVERY)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders retrieved",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = OrderSummaryDetailDto.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid order status", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderSummaryDetailDto>> getOrdersByStatus(@PathVariable String status) {
        log.info("GET /api/staff/orders/status/{} - fetching orders by status", status);
        OrderStatus orderStatus = OrderStatus.fromString(status);
        if (orderStatus == null) {
            throw new RuntimeException("Invalid order status: " + status);
        }
        return ResponseEntity.ok(staffOrderService.getOrdersByStatus(orderStatus));
    }

    @Operation(summary = "Get order by ID", description = "Returns full details of a specific order including delivery info")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDetailDto.class))),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailDto> getOrderById(@PathVariable Long id) {
        log.info("GET /api/staff/orders/{} - fetching order by id", id);
        return ResponseEntity.ok(staffOrderService.getOrderById(id));
    }

    @Operation(summary = "Update order status", description = "Updates order delivery status. Valid transitions: PLACED_ORDER → WAITING_FOR_DELIVERY → PENDING_DELIVERY → ORDER_DELIVERED. Staff cannot cancel orders.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order status updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDetailDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid status or staff attempted to cancel", content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderDetailDto> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        log.info("PATCH /api/staff/orders/{}/status - updating to {}", id, status);
        OrderStatus orderStatus = OrderStatus.fromString(status);
        if (orderStatus == null) {
            throw new RuntimeException("Invalid order status: " + status);
        }
        return ResponseEntity.ok(staffOrderService.updateOrderStatus(id, orderStatus));
    }
}
