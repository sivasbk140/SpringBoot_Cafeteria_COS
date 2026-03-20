package net.breezeware.Spring_Boot_Cafeteria.food.dto;

import lombok.*;
import net.breezeware.Spring_Boot_Cafeteria.food.enumeration.MenuDay;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FoodMenuRequest {
    private String category;
    private List<Long> foodItemIds;
    private List<MenuDay> availableDays;
}