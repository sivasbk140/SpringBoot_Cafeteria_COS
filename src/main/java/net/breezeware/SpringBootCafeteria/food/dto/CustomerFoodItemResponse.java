package net.breezeware.SpringBootCafeteria.food.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing food item details visible to customers.
 *
 * <p>This is a customer-scoped view of a food item. It excludes
 * admin-only fields such as ID and timestamps, exposing only
 * the information relevant to browsing and ordering.</p>
 *
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerFoodItemResponse {

    /** Name of the food item. */
    @Schema(description = "Name of the food item", example = "Chicken Burger")
    private String name;

    /** Unit price of the food item. */
    @Schema(description = "Price of the food item", example = "9.99")
    private Double price;

    /** Current available stock quantity. */
    @Schema(description = "Available quantity of the food item", example = "50")
    private Integer quantity;

    /** Category of the food item (e.g., MAIN_COURSE, SNACK). */
    @Schema(description = "Category of the food item", example = "MAIN_COURSE")
    private String category;

    /** Optional description of the food item. */
    @Schema(description = "Description of the food item", example = "Grilled chicken with lettuce and sauce")
    private String description;

    /** Whether this food item is currently available for ordering. */
    @Schema(description = "Whether the food item is available", example = "true")
    private boolean isAvailable;
}