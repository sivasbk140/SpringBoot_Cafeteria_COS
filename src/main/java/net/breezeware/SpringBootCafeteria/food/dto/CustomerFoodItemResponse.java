package net.breezeware.SpringBootCafeteria.food.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerFoodItemResponse {

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
}