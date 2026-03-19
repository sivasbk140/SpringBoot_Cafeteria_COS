package net.breezeware.Spring_Boot_Cafeteria.food.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import net.breezeware.Spring_Boot_Cafeteria.food.enumeration.MenuDay;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class FoodMenuResponseDto {
    private Long id;
    private String category;
    private List<FoodMenuItemMapResponseDto> items;
    private List<MenuDay> availableDays;

    public FoodMenuResponseDto(@NotBlank(message = "Category is required") @NonNull String category) {
    }
}
