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
import net.breezeware.springbootcafeteria.exception.AppCustomException;
import net.breezeware.springbootcafeteria.order.dto.OrderDetailDto;
import net.breezeware.springbootcafeteria.order.dto.OrderSummaryDetailDto;
import net.breezeware.springbootcafeteria.order.enumeration.OrderStatus;
import net.breezeware.springbootcafeteria.order.service.StaffOrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "Staff Order APIs", description = "APIs for cafeteria staff to view, update status and manage orders")
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
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        [
                          {
                            "orderId": 1,
                            "userId": 1,
                            "status": "PLACED_ORDER",
                            "totalPrice": 120.0,
                            "createdOn": "2026-04-01 10:30:00"
                          }
                        ]
                    """))),
            @ApiResponse(responseCode = "200", description = "No Orders Found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          []
                        }
                    """)))
    })
    @GetMapping
    public ResponseEntity<List<OrderSummaryDetailDto>> getAllOrders() {
        log.info("GET /api/staff/orders - fetching all orders");
        return ResponseEntity.ok(staffOrderService.getAllOrders());
    }

    @Operation(summary = "Get completed orders", description = "Returns all orders with status ORDER_DELIVERED")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Completed orders retrieved",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        [
                          {
                            "orderId": 1,
                            "userId": 1,
                            "status": "ORDER_DELIVERED",
                            "totalPrice": 120.0,
                            "createdOn": "2026-04-01 10:30:00"
                          }
                        ]
                    """))),
            @ApiResponse(responseCode = "200", description = "No Completed Orders Found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          []
                        }
                    """)))
    })
    @GetMapping("/completed")
    public ResponseEntity<List<OrderSummaryDetailDto>> getCompletedOrders() {
        log.info("GET /api/staff/orders/completed - fetching completed orders");
        return ResponseEntity.ok(staffOrderService.getOrdersByStatus(OrderStatus.ORDER_DELIVERED));
    }

    @Operation(summary = "Get cancelled orders", description = "Returns all orders with status ORDER_CANCELLED")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cancelled orders retrieved",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        [
                          {
                            "orderId": 1,
                            "userId": 1,
                            "status": "ORDER_CANCELLED",
                            "totalPrice": 120.0,
                            "createdOn": "2026-04-01 10:30:00"
                          }
                        ]
                    """))),
            @ApiResponse(responseCode = "200", description = "No Cancelled Orders Found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          []
                        }
                    """)))
    })
    @GetMapping("/cancelled")
    public ResponseEntity<List<OrderSummaryDetailDto>> getCancelledOrders() {
        log.info("GET /api/staff/orders/cancelled - fetching cancelled orders");
        return ResponseEntity.ok(staffOrderService.getOrdersByStatus(OrderStatus.ORDER_CANCELLED));
    }

    @Operation(summary = "Get orders by status", description = "Returns orders filtered by status (e.g. PLACED_ORDER, WAITING_FOR_DELIVERY)",
            parameters = {@Parameter(name = "status", description = "Order status to filter by")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders retrieved",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        [
                          {
                            "orderId": 1,
                            "userId": 1,
                            "status": "PLACED_ORDER",
                            "totalPrice": 120.0,
                            "createdOn": "2026-04-01 10:30:00"
                          }
                        ]
                    """))),
            @ApiResponse(responseCode = "400", description = "Invalid order status",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "statusCode": 400,
                          "message": "Invalid order status: INVALID"
                        }
                    """))),
            @ApiResponse(responseCode = "200", description = "No Orders Found For Status",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          []
                        }
                    """)))
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderSummaryDetailDto>> getOrdersByStatus(@PathVariable String status) {
        log.info("GET /api/staff/orders/status/{} - fetching orders by status", status);
        OrderStatus orderStatus = OrderStatus.fromString(status);
        if (orderStatus == null) {
            throw new AppCustomException("Invalid order status: " + status, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(staffOrderService.getOrdersByStatus(orderStatus));
    }

    @Operation(summary = "Get order by ID", description = "Returns full details of a specific order including delivery info",
            parameters = {@Parameter(name = "id", description = "ID of the order to retrieve")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order retrieved",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "orderId": 1,
                          "userId": 1,
                          "userName": "Siva",
                          "status": "PLACED_ORDER",
                          "items": [
                            {
                              "foodItemName": "Dosa",
                              "quantity": 2,
                              "price": 50.0,
                              "totalPrice": 100.0
                            }
                          ],
                          "totalPrice": 100.0,
                          "deliveryName": "Siva",
                          "deliveryPhone": "9876543210",
                          "deliveryAddress": "123, Gandhi Street, Chennai",
                          "createdOn": "2026-04-01 10:30:00"
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
    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailDto> getOrderById(@PathVariable Long id) {
        log.info("GET /api/staff/orders/{} - fetching order by id", id);
        return ResponseEntity.ok(staffOrderService.getOrderById(id));
    }

    @Operation(summary = "Update order status", description = "Updates order status. Valid values: ORDER_CONFIRMED, ORDER_PREPARING, ASSIGNED_DELIVERY_STAFF, ORDER_DELIVERED",
            parameters = {
                    @Parameter(name = "id", description = "ID of the order to update"),
                    @Parameter(name = "status", description = "New order status")
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order status updated",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "orderId": 1,
                          "userId": 1,
                          "userName": "Siva",
                          "status": "ORDER_CONFIRMED",
                          "items": [],
                          "totalPrice": 120.0,
                          "createdOn": "2026-04-01 10:30:00"
                        }
                    """))),
            @ApiResponse(responseCode = "400", description = "Invalid status",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "statusCode": 400,
                          "message": "Invalid order status: INVALID"
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
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderDetailDto> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        log.info("PATCH /api/staff/orders/{}/status - updating to {}", id, status);
        OrderStatus orderStatus = OrderStatus.fromString(status);
        if (orderStatus == null) {
            throw new AppCustomException("Invalid order status: " + status, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(staffOrderService.updateOrderStatus(id, orderStatus));
    }

    @Operation(summary = "Assign delivery staff", description = "Assigns a delivery staff to an order and sets status to ASSIGNED_DELIVERY_STAFF. Order must be in ORDER_PREPARING status.",
            parameters = {
                    @Parameter(name = "id", description = "ID of the order"),
                    @Parameter(name = "staffId", description = "ID of the delivery staff to assign")
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Delivery staff assigned",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "orderId": 1,
                          "userId": 1,
                          "userName": "Siva",
                          "status": "ASSIGNED_DELIVERY_STAFF",
                          "items": [],
                          "totalPrice": 120.0,
                          "createdOn": "2026-04-01 10:30:00"
                        }
                    """))),
            @ApiResponse(responseCode = "400", description = "Order not in ORDER_PREPARING status",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "statusCode": 400,
                          "message": "Order not in ORDER_PREPARING status"
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
    @PatchMapping("/{id}/assign")
    public ResponseEntity<OrderDetailDto> assignDeliveryStaff(@PathVariable Long id, @RequestParam Long staffId) {
        log.info("PATCH /api/staff/orders/{}/assign - assigning staff {}", id, staffId);
        return ResponseEntity.ok(staffOrderService.assignDeliveryStaff(id, staffId));
    }

    @Operation(summary = "Get orders by user", description = "Returns all orders placed by a specific user",
            parameters = {@Parameter(name = "userId", description = "ID of the user whose orders to retrieve")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders retrieved",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        [
                          {
                            "orderId": 1,
                            "userId": 1,
                            "status": "PLACED_ORDER",
                            "totalPrice": 120.0,
                            "createdOn": "2026-04-01 10:30:00"
                          }
                        ]
                    """))),
            @ApiResponse(responseCode = "200", description = "No Orders Found For User",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          []
                        }
                    """)))
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderSummaryDetailDto>> getOrdersByUser(@PathVariable Long userId) {
        log.info("GET /api/staff/orders/user/{} - fetching orders by user", userId);
        return ResponseEntity.ok(staffOrderService.getOrdersByUserId(userId));
    }


    @Operation(summary = "Cancel order", description = "Emergency cancel — staff can cancel from any active status",
            parameters = {@Parameter(name = "id", description = "ID of the order to cancel")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order cancelled",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "orderId": 1,
                          "userId": 1,
                          "userName": "Siva",
                          "status": "ORDER_CANCELLED",
                          "items": [],
                          "totalPrice": 120.0,
                          "createdOn": "2026-04-01 10:30:00"
                        }
                    """))),
            @ApiResponse(responseCode = "400", description = "Order already delivered or cancelled",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "statusCode": 400,
                          "message": "Order already delivered or cancelled"
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
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OrderDetailDto> cancelOrder(@PathVariable Long id) {
        log.info("PATCH /api/staff/orders/{}/cancel - emergency cancel", id);
        return ResponseEntity.ok(staffOrderService.cancelOrder(id));
    }
}
