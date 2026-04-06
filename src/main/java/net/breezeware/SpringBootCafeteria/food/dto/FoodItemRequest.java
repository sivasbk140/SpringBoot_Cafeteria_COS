package net.breezeware.SpringBootCafeteria.food.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * DTO representing the request payload for creating or updating a food item.
 *
 * <p>This object is used to transfer food item input from the client
 * to the backend service layer. Supports partial updates — null fields are ignored.</p>
 *
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor

public class FoodItemRequest {

    /** Name of the food item (e.g., "Chicken Burger"). */
    @Schema(description = "Name of the food item", example = "Chicken Burger")
    private String name;

    /** Unit price of the food item. */
    @Schema(description = "Price of the food item", example = "9.99")
    private Double price;

    /** Optional description providing details about the food item. */
    @Schema(description = "Description of the food item", example = "Grilled chicken with lettuce and sauce")
    private String description;

    /** Available stock quantity of the food item. */
    @Schema(description = "Available quantity of the food item", example = "50")
    private Integer quantity;

    /** Category of the food item (e.g., MAIN_COURSE, SNACK). */
    @Schema(description = "Category of the food item", example = "MAIN_COURSE")
    private String category;



}
