package net.breezeware.SpringBootCafeteria.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import net.breezeware.SpringBootCafeteria.order.enumeration.OrderStatus;

import  lombok .*;

/**
 * DTO representing a lightweight summary of a single order.
 *
 * <p>Used in list responses where only key order fields are needed,
 * without the full item breakdown or delivery details.</p>
 *
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor

public class OrderSummaryDetailDto {

    /** Unique identifier of the order. */
    @Schema(description = "order id ", example = "101")
    private int orderId;

    /** ID of the user who placed the order. */
    @Schema(description = "user id who purchased food items", example = "1")
    private int userId;

    /** Current status of the order. */
    @Schema(description = "status of the order ", example = "ORDER_PLACED")
    private OrderStatus status;

    /** Grand total price of all items in the order. */
    @Schema(description = "grandtotal of the order items in an order ", example = "1000.0")
    private double totalPrice;

    /** Timestamp when the order was created, formatted as a string. */
    @Schema(description = "Order creation timestamp", example = "2026-04-01T10:30:00")
    private String createdOn;

}