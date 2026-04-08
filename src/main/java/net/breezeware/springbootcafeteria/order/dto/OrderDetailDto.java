package net.breezeware.springbootcafeteria.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import net.breezeware.springbootcafeteria.order.enumeration.OrderStatus;

import java.util.List;

import lombok.*;
/**
 * DTO representing the full details of a single order.
 *
 * Returned in single-order fetch responses. Contains the complete order
 * breakdown including item details, delivery information, and order metadata.
 *
 * @since 1.0
 */
@Data@NoArgsConstructor
@AllArgsConstructor

public class OrderDetailDto {

    /** Unique identifier of the order. */
    @Schema(description = "Unique identifier of the order", example = "1")
    private Long orderId;

    /** ID of the user who placed the order. */
    @Schema(description = "ID of the user who placed the order", example = "101")
    private Long userId;

    /** Full name of the user who placed the order. */
    @Schema(description = "Name of the user who placed the order", example = "Siva ")
    private String userName;

    /** Current status of the order. */
    @Schema(description = "Current status of the order", example = "ORDER_CONFIRMED")
    private OrderStatus status;

    /**
     * List of food items included in this order with quantity and pricing.
     *
     * @see OrderItemDetailDTO
     */
    @Schema(description = "List of items included in the order")
    private List<OrderItemDetailDTO> items;

    /** Grand total price of all items in the order. */
    @Schema(description = "Total price of the order", example = "250.75")
    private double totalPrice;

    /** Full name of the delivery recipient. */
    @Schema(description = "Name of the delivery recipient", example = "Siva")
    private String deliveryName;

    /** Contact phone number for the delivery. */
    @Schema(description = "Contact phone number for delivery", example = "9876543210")
    private String deliveryPhone;

    /** Delivery address for the order. */
    @Schema(description = "Delivery address of the order", example = "123, Gandhi Street, Chennai")
    private String deliveryAddress;

    /** Timestamp when the order was created, formatted as a string. */
    @Schema(description = "Order creation timestamp", example = "2026-04-01T10:30:00")
    private String createdOn;

    /**
     * Nested DTO representing a single food item line within an order detail response.
     *
     * <p>Contains the item name, ordered quantity, unit price, and calculated total price.</p>
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDetailDTO {

        /** Name of the food item. */
        private String foodItemName;

        /** Quantity of the food item ordered. */
        private int quantity;

        /** Unit price of the food item at the time of ordering. */
        private double price;

        /** Total price for this line item (unit price × quantity). */
        private double totalPrice;
    }

}
