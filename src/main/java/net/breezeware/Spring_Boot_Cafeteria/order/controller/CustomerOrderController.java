package net.breezeware.Spring_Boot_Cafeteria.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.breezeware.Spring_Boot_Cafeteria.order.dto.CartItemDto;
import net.breezeware.Spring_Boot_Cafeteria.order.dto.OrderDeliveryRequest;
import net.breezeware.Spring_Boot_Cafeteria.order.dto.OrderDetailDto;
import net.breezeware.Spring_Boot_Cafeteria.order.dto.OrderRequestDto;
import net.breezeware.Spring_Boot_Cafeteria.order.dto.OrderSummaryDetailDto;
import net.breezeware.Spring_Boot_Cafeteria.order.entity.OrderDeliveryMap;
import net.breezeware.Spring_Boot_Cafeteria.order.service.CustomerOrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CUSTOMER ORDER CONTROLLER
 * Endpoints for customers to manage cart, place, view, and cancel orders
 */
@Slf4j
@RestController
@RequestMapping("/api/customer/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CustomerOrderController {

    private final CustomerOrderService customerOrderService;

    // ═══════════════════════════════════════════════════════
    // Cart Endpoints
    // ═══════════════════════════════════════════════════════

    @Operation(summary = "Add item to cart", description = "Adds a food item to the customer's cart. If item already exists, quantity is increased.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item added to cart",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CartItemDto.class)))),
            @ApiResponse(responseCode = "400", description = "Insufficient stock", content = @Content),
            @ApiResponse(responseCode = "404", description = "Food item not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/cart/add")
    public ResponseEntity<List<CartItemDto>> addToCart(
            @RequestParam Long userId,
            @RequestParam String foodItemName,
            @RequestParam int quantity) {
        log.info("POST /api/customer/orders/cart/add - userId: {}, foodItemName: {}, qty: {}", userId, foodItemName, quantity);
        return ResponseEntity.ok(customerOrderService.addToCart(userId, foodItemName, quantity));
    }

    @Operation(summary = "View cart", description = "Returns all items currently in the customer's cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart retrieved",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CartItemDto.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/cart/{userId}")
    public ResponseEntity<List<CartItemDto>> viewCart(@PathVariable Long userId) {
        log.info("GET /api/customer/orders/cart/{} - viewing cart", userId);
        return ResponseEntity.ok(customerOrderService.viewCart(userId));
    }

    @Operation(summary = "Remove item from cart", description = "Removes a specific food item from the customer's cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item removed from cart",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CartItemDto.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @DeleteMapping("/cart/{userId}/remove")
    public ResponseEntity<List<CartItemDto>> removeFromCart(
            @PathVariable Long userId,
            @RequestParam String foodItemName) {
        log.info("DELETE /api/customer/orders/cart/{}/remove - removing item: {}", userId, foodItemName);
        return ResponseEntity.ok(customerOrderService.removeFromCart(userId, foodItemName));
    }

    @Operation(summary = "Checkout cart", description = "Converts all cart items into a placed order. Clears the cart on success.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order placed from cart",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDetailDto.class))),
            @ApiResponse(responseCode = "400", description = "Cart is empty or insufficient stock", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/cart/{userId}/checkout")
    public ResponseEntity<OrderDetailDto> checkout(
            @PathVariable Long userId,
            @RequestBody OrderDeliveryRequest deliveryRequest) {
        log.info("POST /api/customer/orders/cart/{}/checkout - placing order from cart", userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(customerOrderService.checkout(userId, deliveryRequest));
    }

    // ═══════════════════════════════════════════════════════
    // Order Endpoints
    // ═══════════════════════════════════════════════════════

    @Operation(summary = "Place order directly", description = "Places a new order directly with a list of items (bypasses cart)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order placed",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDetailDto.class))),
            @ApiResponse(responseCode = "400", description = "Empty items or insufficient stock", content = @Content),
            @ApiResponse(responseCode = "404", description = "User or food item not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<OrderDetailDto> placeOrder(@RequestBody OrderRequestDto request) {
        log.info("POST /api/customer/orders - placing new order for userId: {}", request.getUser_id());
        return ResponseEntity.status(HttpStatus.CREATED).body(customerOrderService.placeOrder(request));
    }

    @Operation(summary = "Get my orders", description = "Returns all orders placed by the customer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders retrieved",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = OrderSummaryDetailDto.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderSummaryDetailDto>> getMyOrders(@PathVariable Long userId) {
        log.info("GET /api/customer/orders/user/{} - fetching user orders", userId);
        return ResponseEntity.ok(customerOrderService.getMyOrders(userId));
    }

    @Operation(summary = "Get order detail", description = "Returns full details of an order. Customer can only view their own orders.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order detail retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDetailDto.class))),
            @ApiResponse(responseCode = "403", description = "Access denied — order does not belong to this user", content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailDto> getOrderDetail(
            @PathVariable Long orderId,
            @RequestParam Long userId) {
        log.info("GET /api/customer/orders/{} - fetching order detail for userId: {}", orderId, userId);
        return ResponseEntity.ok(customerOrderService.getOrderDetail(orderId, userId));
    }

    @Operation(summary = "Cancel order", description = "Cancels an order. Only orders in PLACED_ORDER status can be cancelled. Stock is restored on cancellation.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order cancelled",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDetailDto.class))),
            @ApiResponse(responseCode = "400", description = "Order cannot be cancelled in current status", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied — order does not belong to this user", content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<OrderDetailDto> cancelOrder(
            @PathVariable Long orderId,
            @RequestParam Long userId) {
        log.info("PATCH /api/customer/orders/{}/cancel - userId: {}", orderId, userId);
        return ResponseEntity.ok(customerOrderService.cancelOrder(orderId, userId));
    }
    @Operation(summary = "Add delivery details", description = "Adds delivery details (name, phone, address) to a placed order")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Delivery details added",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDeliveryMap.class))),
            @ApiResponse(responseCode = "400", description = "Delivery details already exist for this order", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/{orderId}/delivery")
    public ResponseEntity<OrderDeliveryMap> addDeliveryDetails(
            @PathVariable Long orderId,
            @RequestParam Long userId,
            @RequestBody OrderDeliveryRequest request) {
        log.info("POST /api/customer/orders/{}/delivery - userId: {}", orderId, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerOrderService.addDeliveryDetails(orderId, userId, request));
    }


}