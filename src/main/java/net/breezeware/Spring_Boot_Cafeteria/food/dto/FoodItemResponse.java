package net.breezeware.Spring_Boot_Cafeteria.food.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodItemResponse {
    @Schema(description = "Unique identifier of the food item", example = "1")
    private Long id;
    @Schema(description = "Name of the food item", example = "Chicken Burger")
    private String name;
    @Schema(description = "Price of the food item", example = "9.99")
    private Double price;
    @Schema(description = "Available quantity of the food item", example = "50")
    private Integer quantity;
    @Schema(description = "Category of the food item", example = "MAIN_COURSE")
    private String category;
    @Schema(description = "Description of the food item", example = "Grilled chicken with lettuce and sauce")
    private String description;
    @Schema(description = "Whether the food item is available", example = "true")
    private boolean isAvailable;
    @Schema(description = "Date when the food item was created", example = "2024-01-01T00:00:00.000+00:00")
    private Date createdOn;
    @Schema(description = "Date when the food item was last updated", example = "2024-01-01T00:00:00.000+00:00")
    private Date updatedOn;
}