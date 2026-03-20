package net.breezeware.Spring_Boot_Cafeteria.food.dto;

import lombok.*;
@Data
@NoArgsConstructor
@AllArgsConstructor

public class FoodMenuItemMapResponse {
    private Long id;
    private Long foodItemId;
    private String foodItemName;
    private Double foodItemPrice;
    private Boolean isAvailable;
}
