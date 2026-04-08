package net.breezeware.springbootcafeteria.order.dto;

 import com.fasterxml.jackson.annotation.JsonIgnore;
 import io.swagger.v3.oas.annotations.media.Schema;
 import lombok.*;



/**
 * DTO representing a single item in a customer's in-memory cart.
 *
 * Used during the cart management flow before the customer proceeds
 * to checkout. Holds the food item reference, quantity, and calculated total price.
 *
 * @since 1.0
 */
@Data
 @NoArgsConstructor
 @AllArgsConstructor

public class CartItemDto {

    /** Unique identifier of the food item in the cart (hidden from API response). */
    @JsonIgnore
    private Long foodItemId;

    /** Name of the food item in the cart. */
    @Schema(description = "food item name",example = "Biryani")
    private String foodItemName;

    /** Total price for this cart line (unit price × quantity). */
    @Schema(description = "food item price",example = "200.0")
    private Double totalPrice;

    /** Quantity of the food item added to the cart. */
    @Schema(description = "food item quantity",example = "50")
    private Integer quantity;
}
