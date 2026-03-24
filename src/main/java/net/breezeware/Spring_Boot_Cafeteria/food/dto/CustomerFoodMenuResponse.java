package net.breezeware.Spring_Boot_Cafeteria.food.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.breezeware.Spring_Boot_Cafeteria.food.enumeration.MenuDay;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerFoodMenuResponse {

    private String category;
    private MenuDay menuDay;
    private List<CustomerFoodItemResponse> items;
    private LocalDateTime createdOn;
}