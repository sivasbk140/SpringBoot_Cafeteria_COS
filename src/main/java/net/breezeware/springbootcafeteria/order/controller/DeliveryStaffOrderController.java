package net.breezeware.springbootcafeteria.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.breezeware.springbootcafeteria.order.dto.OrderDetailDto;
import net.breezeware.springbootcafeteria.order.dto.OrderSummaryDetailDto;
import net.breezeware.springbootcafeteria.order.service.DeliveryStaffOrderService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Delivery Staff Order APIs", description = "APIs for delivery staff to view assigned orders and mark them as delivered")
@Slf4j
@RestController
@RequestMapping("/api/delivery/order")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DeliveryStaffOrderController {

    private final DeliveryStaffOrderService deliveryStaffOrderService;

    @Operation(summary = "Get assigned orders", description = "Returns all orders currently assigned to the delivery staff member",
            parameters = {@Parameter(name = "staffId", description = "ID of the delivery staff member")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Assigned orders retrieved",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        [
                          {
                            "orderId": 1,
                            "userId": 1,
                            "status": "ASSIGNED_DELIVERY_STAFF",
                            "totalPrice": 120.0,
                            "createdOn": "2026-04-01 10:30:00"
                          }
                        ]
                    """))),
            @ApiResponse(responseCode = "200", description = "No Assigned Orders Found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          []
                        }
                    """)))
    })
    @GetMapping("/assignedstaff/{staffId}")
    public ResponseEntity<List<OrderSummaryDetailDto>> getAssignedOrders(@PathVariable Long staffId) {
        log.info("GET /api/delivery/orders/assigned/{} - fetching assigned orders", staffId);
        return ResponseEntity.ok(deliveryStaffOrderService.getAssignedOrders(staffId));
    }

    @Operation(summary = "Mark order as delivered", description = "Delivery staff marks an assigned order as ORDER_DELIVERED. Only the assigned staff can update this order.",
            parameters = {
                    @Parameter(name = "orderId", description = "ID of the order to mark as delivered"),
                    @Parameter(name = "staffId", description = "ID of the delivery staff member")
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order marked as delivered",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "orderId": 1,
                          "userId": 1,
                          "userName": "Siva",
                          "status": "ORDER_DELIVERED",
                          "items": [
                            {
                              "foodItemName": "Biryani",
                              "quantity": 2,
                              "price": 200.0,
                              "totalPrice": 400.0
                            }
                          ],
                          "totalPrice": 400.0,
                          "deliveryName": "siva",
                          "deliveryPhone": "9876543210",
                          "deliveryAddress": "123, Gandhi Street, Chennai",
                          "createdOn": "2026-04-01 10:30:00"
                        }
                    """))),
            @ApiResponse(responseCode = "400", description = "Order not assigned to this staff or invalid status",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "statusCode": 400,
                          "message": "Order not assigned to this staff or invalid status"
                        }
                    """))),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "statusCode": 404,
                          "message": "NOT_FOUND",
                          "details": ["Order with ID not found"]
                        }
                    """))),
    })
    @PatchMapping("/{orderId}/deliver")
    public ResponseEntity<OrderDetailDto> markOrderDelivered(
            @PathVariable Long orderId,
            @RequestParam Long staffId) {
        log.info("PATCH /api/delivery/orders/{}/deliver - staff {} marking as delivered", orderId, staffId);
        return ResponseEntity.ok(deliveryStaffOrderService.markOrderDelivered(orderId, staffId));
    }
}
