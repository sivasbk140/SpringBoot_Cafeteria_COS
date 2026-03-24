package net.breezeware.Spring_Boot_Cafeteria.food.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerFoodItemResponse {
    private String name;
    private Double price;
    private Integer quantity;
    private String category;
    private String description;
    private boolean isAvailable;
}