package net.breezeware.Spring_Boot_Cafeteria.food.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodMenuItemMapRequest {

    @NotNull(message = "Food item ID is required")
    private Long foodItemId;

    private Boolean isAvailable = true;
}