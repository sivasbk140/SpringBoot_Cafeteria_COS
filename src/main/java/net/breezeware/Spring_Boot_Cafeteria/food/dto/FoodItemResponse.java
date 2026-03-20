package net.breezeware.Spring_Boot_Cafeteria.food.dto;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodItemResponse {
    private Long id;
    private String name;
    private Double price;
    private Integer quantity;
    private String category;
    private String description;
    private boolean isAvailable;
    private Date createdOn;
    private Date updatedOn;
}