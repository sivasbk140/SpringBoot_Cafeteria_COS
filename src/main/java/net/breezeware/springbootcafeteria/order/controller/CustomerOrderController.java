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
import net.breezeware.springbootcafeteria.order.dto.CartItemDto;
import net.breezeware.springbootcafeteria.order.dto.OrderDeliveryRequest;
import net.breezeware.springbootcafeteria.order.dto.OrderDetailDto;
import net.breezeware.springbootcafeteria.order.dto.OrderSummaryDetailDto;
import net.breezeware.springbootcafeteria.order.service.CustomerOrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Customer Order APIs", description = "APIs for customers to manage cart, place orders, view order history and cancel orders")
@Slf4j
@RestController
@RequestMapping("/api/customer/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CustomerOrderController {

    private final CustomerOrderService customerOrderService;


    @Operation(summary = "Add item to cart", description = "Adds a food item to the customer's cart. If item already exists, quantity is increased.",
            parameters = {
                    @Parameter(name = "userId", description = "ID of the customer"),
                    @Parameter(name = "foodItemName", description = "Name of the food item to add"),
                    @Parameter(name = "quantity", description = "Quantity to add")
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item added to cart",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        [
                          {
                            "foodItemName": "Biryani",
                            "totalPrice": 400.0,
                            "quantity": 2
                          }
                        ]
                    """))),
            @ApiResponse(responseCode = "400", description = "Insufficient stock",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "statusCode": 400,
                          "message": "Insufficient stock"
                        }
                    """))),
            @ApiResponse(responseCode = "404", description = "Food item not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "statusCode": 404,
                          "message": "NOT_FOUND",
                          "details": ["Food item not found"]
                        }
                    """))),
    })
    @PostMapping("/cart/add")
    public ResponseEntity<List<CartItemDto>> addToCart(
            @RequestParam Long userId,
            @RequestParam String foodItemName,
            @RequestParam int quantity) {
        log.info("POST /api/customer/orders/cart/add - userId: {}, foodItemName: {}, qty: {}", userId, foodItemName, quantity);
        return ResponseEntity.ok(customerOrderService.addToCart(userId, foodItemName, quantity));
    }

    @Operation(summary = "View cart", description = "Returns all items currently in the customer's cart",
            parameters = {@Parameter(name = "userId", description = "ID of the customer")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart retrieved",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        [
                          {
                            "foodItemName": "Biryani",
                            "totalPrice": 400.0,
                            "quantity": 2
                          }
                        ]
                    """))),
            @ApiResponse(responseCode = "200", description = "Cart Is Empty",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          []
                        }
                    """)))
    })
    @GetMapping("/cart/{userId}")
    public ResponseEntity<List<CartItemDto>> viewCart(@PathVariable Long userId) {
        log.info("GET /api/customer/orders/cart/{} - viewing cart", userId);
        return ResponseEntity.ok(customerOrderService.viewCart(userId));
    }

    @Operation(summary = "Remove item from cart", description = "Removes a specific food item from the customer's cart",
            parameters = {
                    @Parameter(name = "userId", description = "ID of the customer"),
                    @Parameter(name = "foodItemName", description = "Name of the food item to remove"),
                    @Parameter(name = "quantity", description = "Quantity to remove")
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item removed from cart",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        [
                          {
                            "foodItemName": "Biryani",
                            "totalPrice": 200.0,
                            "quantity": 1
                          }
                        ]
                    """))),
    })
    @DeleteMapping("/cart/{userId}/remove")
    public ResponseEntity<List<CartItemDto>> removeFromCart(
            @PathVariable Long userId,
            @RequestParam String foodItemName,
            @RequestParam int quantity) {
        log.info("DELETE /api/customer/orders/cart/{}/remove - removing {} unit(s) of item: {}", userId, quantity, foodItemName);
        return ResponseEntity.ok(customerOrderService.removeFromCart(userId, foodItemName, quantity));
    }

    @Operation(summary = "Checkout cart", description = "Converts all cart items into a placed order. Clears the cart on success.",
            parameters = {@Parameter(name = "userId", description = "ID of the customer")})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Delivery details for the order",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = OrderDeliveryRequest.class),
                    examples = {
                            @ExampleObject(
                                    name = "Delivery to home",
                                    value = """
                                        {
                                          "name": "siva",
                                          "phone": "9876543210",
                                          "address": "123, Gandhi Street, Chennai"
                                        }
                                        """
                            ),
                            @ExampleObject(
                                    name = "Delivery to office",
                                    value = """
                                        {
                                          "name": "ravi",
                                          "phone": "9123456789",
                                          "address": "Breezeware Office, Coimbatore"
                                        }
                                        """
                            )
                    }
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order placed from cart",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "orderId": 1,
                          "userId": 1,
                          "userName": "Siva",
                          "status": "PLACED_ORDER",
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
            @ApiResponse(responseCode = "400", description = "Cart is empty or insufficient stock",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "statusCode": 400,
                          "message": "Cart is empty"
                        }
                    """))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "statusCode": 404,
                          "message": "NOT_FOUND",
                          "details": ["User not found"]
                        }
                    """))),
    })
    @PostMapping("/cart/{userId}/checkout")
    public ResponseEntity<OrderDetailDto> checkout(
            @PathVariable Long userId,
            @RequestBody OrderDeliveryRequest deliveryRequest) {
        log.info("POST /api/customer/orders/cart/{}/checkout - placing order from cart", userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(customerOrderService.checkout(userId, deliveryRequest));
    }


    @Operation(summary = "Get my orders", description = "Returns all orders placed by the customer",
            parameters = {@Parameter(name = "userId", description = "ID of the customer")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders retrieved",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        [
                          {
                            "orderId": 1,
                            "userId": 1,
                            "status": "PLACED_ORDER",
                            "totalPrice": 400.0,
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
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderSummaryDetailDto>> getMyOrders(@PathVariable Long userId) {
        log.info("GET /api/customer/orders/user/{} - fetching user orders", userId);
        return ResponseEntity.ok(customerOrderService.getMyOrders(userId));
    }

    @Operation(summary = "Get order detail", description = "Returns full details of an order. Customer can only view their own orders.",
            parameters = {
                    @Parameter(name = "orderId", description = "ID of the order"),
                    @Parameter(name = "userId", description = "ID of the customer (for ownership validation)")
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order detail retrieved",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "orderId": 1,
                          "userId": 1,
                          "userName": "Siva",
                          "status": "PLACED_ORDER",
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
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "statusCode": 403,
                          "message": "FORBIDDEN",
                          "details": ["Order does not belong to this user"]
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
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailDto> getOrderDetail(
            @PathVariable Long orderId,
            @RequestParam Long userId) {
        log.info("GET /api/customer/orders/{} - fetching order detail for userId: {}", orderId, userId);
        return ResponseEntity.ok(customerOrderService.getOrderDetail(orderId, userId));
    }

    @Operation(summary = "Cancel order", description = "Cancels an order. Only orders in PLACED_ORDER status can be cancelled. Stock is restored on cancellation.",
            parameters = {
                    @Parameter(name = "orderId", description = "ID of the order to cancel"),
                    @Parameter(name = "userId", description = "ID of the customer (for ownership validation)")
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order cancelled",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "orderId": 1,
                          "userId": 1,
                          "userName": "Siva",
                          "status": "ORDER_CANCELLED",
                          "items": [],
                          "totalPrice": 400.0,
                          "createdOn": "2026-04-01 10:30:00"
                        }
                    """))),
            @ApiResponse(responseCode = "400", description = "Order cannot be cancelled in current status",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "statusCode": 400,
                          "message": "Order cannot be cancelled in current status"
                        }
                    """))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "statusCode": 403,
                          "message": "FORBIDDEN",
                          "details": ["Order does not belong to this user"]
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
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<OrderDetailDto> cancelOrder(
            @PathVariable Long orderId,
            @RequestParam Long userId) {
        log.info("PATCH /api/customer/orders/{}/cancel - userId: {}", orderId, userId);
        return ResponseEntity.ok(customerOrderService.cancelOrder(orderId, userId));
    }
}
