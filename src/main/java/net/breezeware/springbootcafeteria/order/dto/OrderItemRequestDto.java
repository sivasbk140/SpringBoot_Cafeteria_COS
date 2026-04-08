package net.breezeware.springbootcafeteria.order.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
/**
 * DTO representing a single food item line within a direct order request.
 *
 * Used inside {@link OrderRequestDto} to specify which food item
 * is being ordered, how many units, and at what price.
 *
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor

public class OrderItemRequestDto {

    /** Unique identifier of the food item being ordered. */
    @Schema(description =  "order id",example = "1")
    private Long id;

    /** Quantity of the food item to order. */
    @Schema(description =  "order item quantity",example = "3")
    private Long quantity;

    /** Unit price of the food item at the time of ordering. */
    @Schema(description = "price of the orderitem",example = "250.0")
    private Double price;
}
