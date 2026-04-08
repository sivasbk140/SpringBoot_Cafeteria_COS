package net.breezeware.springbootcafeteria.food.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
/**
 * DTO representing a single food item entry within a food menu mapping.
 *
 * Used to convey the mapping between a menu and a food item,
 * including the item's availability status within that menu context.
 *
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor

public class FoodMenuItemMapResponse {

    /** Unique identifier of the menu-item mapping record. */
    @Schema(description = " menu id  to map with food items ",example = "1")
    private Long id;

    /** Unique identifier of the mapped food item. */
    @Schema(description = " food item id   to map with food manu ",example = "1")
    private Long foodItemId;

    /** Name of the mapped food item. */
    @Schema(description = "Name of the food item", example = "Chicken Burger")
    private String foodItemName;

    /** Price of the mapped food item. */
    @Schema(description = "Price of the food item", example = "9.99")
    private Double foodItemPrice;

    /** Whether this food item is available within the menu. */
    @Schema(description = "Whether the food item is available", example = "true")
    private Boolean isAvailable;
}
