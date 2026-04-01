package net.breezeware.SpringBootCafeteria.food.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
@Data
@NoArgsConstructor
@AllArgsConstructor

public class FoodMenuItemMapResponse {
    @Schema(description = " menu id  to map with food items ",example = "1")
    private Long id;
    @Schema(description = " food item id   to map with food manu ",example = "1")
    private Long foodItemId;
    @Schema(description = "Name of the food item", example = "Chicken Burger")
    private String foodItemName;
    @Schema(description = "Price of the food item", example = "9.99")
    private Double foodItemPrice;
    @Schema(description = "Whether the food item is available", example = "true")
    private Boolean isAvailable;
}
