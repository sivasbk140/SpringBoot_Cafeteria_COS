package net.breezeware.SpringBootCafeteria.order.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;


/**
 * DTO representing the request payload for placing a direct order.
 *
 * <p>Used by the customer to submit a new order directly (without cart checkout),
 * including the list of items and delivery details in a single request.</p>
 *
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {

    /** ID of the user placing the order. */
    @Schema(description = "user id of the one who made an order ",example = "101")
    private Long user_id;

    /**
     * List of food items included in the order.
     *
     * @see OrderItemRequestDto
     */
    @Schema(description = "List of items included in the order")
    private List<OrderItemRequestDto> items;

    /** Full name of the delivery recipient. */
    @Schema(description = "user's name  the one who made an order ",example = "siva")
    private String deliveryName;

    /** Contact phone number for delivery. */
    @Schema(description = "user's phone number  the one who made an order ",example = "siva")
    private String deliveryPhone;

    /** Delivery address for the order. */
    @Schema(description = "user's address  the one who made an order ",example = "siva")
    private String deliveryAddress;

}
