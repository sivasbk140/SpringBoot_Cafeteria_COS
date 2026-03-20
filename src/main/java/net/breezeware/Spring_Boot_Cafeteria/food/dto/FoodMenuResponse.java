package net.breezeware.Spring_Boot_Cafeteria.food.dto;

import lombok.*;
import net.breezeware.Spring_Boot_Cafeteria.food.enumeration.MenuDay;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodMenuResponse {
    private Long id;
    private String category;
    private List<FoodMenuItemMapResponse> items;
    private List<MenuDay> availableDays;
    private LocalDateTime createdOn;
}