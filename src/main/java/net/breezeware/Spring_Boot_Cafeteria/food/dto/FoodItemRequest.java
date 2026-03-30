package net.breezeware.Spring_Boot_Cafeteria.food.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class FoodItemRequest {

    @Schema(description = "Name of the food item", example = "Chicken Burger")
    private String name;
    @Schema(description = "Price of the food item", example = "9.99")
    private Double price;
    @Schema(description = "Description of the food item", example = "Grilled chicken with lettuce and sauce")
    private String description;
    @Schema(description = "Available quantity of the food item", example = "50")
    private Integer quantity;
    @Schema(description = "Category of the food item", example = "MAIN_COURSE")
    private String category;



}
