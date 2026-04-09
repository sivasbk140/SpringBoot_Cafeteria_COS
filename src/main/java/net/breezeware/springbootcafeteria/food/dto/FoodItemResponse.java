package net.breezeware.springbootcafeteria.food.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.Instant;

/**
 * DTO representing the response data for a food item (admin view).
 *
 * This object is returned to the client containing full food item details
 * including stock quantity, timestamps, and availability status.
 *
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodItemResponse {

    /** Unique identifier of the food item. */
    @Schema(description = "Unique identifier of the food item", example = "1")
    private Long id;

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

    /** Whether the food item is currently available (quantity &gt; 0). */
    @Schema(description = "Whether the food item is available", example = "true")
    private boolean isAvailable;

    /** Timestamp when the food item record was created. */
    @Schema(description = "Date when the food item was created", example = "2024-01-01T00:00:00.000+00:00")
    private Instant createdOn;

    /** Timestamp when the food item record was last updated. */
    @Schema(description = "Date when the food item was last updated", example = "2024-01-01T00:00:00.000+00:00")
    private Instant updatedOn;
}