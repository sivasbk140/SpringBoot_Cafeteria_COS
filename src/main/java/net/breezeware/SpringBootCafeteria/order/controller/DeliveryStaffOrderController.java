package net.breezeware.SpringBootCafeteria.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.breezeware.SpringBootCafeteria.order.dto.OrderDetailDto;
import net.breezeware.SpringBootCafeteria.order.dto.OrderSummaryDetailDto;
import net.breezeware.SpringBootCafeteria.order.service.DeliveryStaffOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/delivery/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DeliveryStaffOrderController {

    private final DeliveryStaffOrderService deliveryStaffOrderService;

    @Operation(summary = "Get assigned orders", description = "Returns all orders currently assigned to the delivery staff member")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Assigned orders retrieved",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = OrderSummaryDetailDto.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/assigned/{staffId}")
    public ResponseEntity<List<OrderSummaryDetailDto>> getAssignedOrders(@PathVariable Long staffId) {
        log.info("GET /api/delivery/orders/assigned/{} - fetching assigned orders", staffId);
        return ResponseEntity.ok(deliveryStaffOrderService.getAssignedOrders(staffId));
    }

    @Operation(summary = "Mark order as delivered", description = "Delivery staff marks an assigned order as ORDER_DELIVERED. Only the assigned staff can update this order.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order marked as delivered",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDetailDto.class))),
            @ApiResponse(responseCode = "400", description = "Order not assigned to this staff or invalid status", content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PatchMapping("/{orderId}/deliver")
    public ResponseEntity<OrderDetailDto> markOrderDelivered(
            @PathVariable Long orderId,
            @RequestParam Long staffId) {
        log.info("PATCH /api/delivery/orders/{}/deliver - staff {} marking as delivered", orderId, staffId);
        return ResponseEntity.ok(deliveryStaffOrderService.markOrderDelivered(orderId, staffId));
    }
}
