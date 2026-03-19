package net.breezeware.Spring_Boot_Cafeteria.food.dto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class FoodItemResponseDto {
    private Long id;
    private  String name;
    private Double price;
    private String description;
    private Integer quantity;
    private String category;

}
