package net.breezeware.Spring_Boot_Cafeteria.food.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import net.breezeware.Spring_Boot_Cafeteria.food.enumeration.MenuDay;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FoodMenuRequest {
    @Schema(description = " menu category  ",example = "BREAKFAST")
    private String category;
    @Schema(description = " Enter the menu day  ",example = "MONDAY")
    private MenuDay menuDay;
    @Schema(description = "List of food item IDs to include in the menu", example = "[1, 2, 3]")
    private List<Long> foodItemIds;
}