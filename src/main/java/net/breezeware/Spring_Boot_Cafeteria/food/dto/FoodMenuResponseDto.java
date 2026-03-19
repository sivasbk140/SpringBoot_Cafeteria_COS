package net.breezeware.Spring_Boot_Cafeteria.food.dto;

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
}
