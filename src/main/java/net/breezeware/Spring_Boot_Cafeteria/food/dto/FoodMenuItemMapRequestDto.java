package net.breezeware.Spring_Boot_Cafeteria.food.dto;

import lombok .*;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodMenuItemMapRequestDto {
    private Long foodItemId;

    private Boolean isAvailable = true;
}
