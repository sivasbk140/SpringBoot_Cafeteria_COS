package net.breezeware.Spring_Boot_Cafeteria.food.dto;

import lombok.*;
import net.breezeware.Spring_Boot_Cafeteria.food.enumeration.MenuDay;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminFoodMenuResponse {
    private Long id;
    private String category;
    private MenuDay menuDay;
    private List<FoodMenuItemMapResponse> items;
    private LocalDateTime createdOn;
}