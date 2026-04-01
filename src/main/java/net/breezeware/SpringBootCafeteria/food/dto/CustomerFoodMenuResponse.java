package net.breezeware.SpringBootCafeteria.food.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.breezeware.SpringBootCafeteria.food.enumeration.MenuDay;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerFoodMenuResponse {

    @Schema(description = "food menu category  ",example = "BREAKFAST")
    private String category;
    @Schema(description = " enter the desired menu day ",example = "MONDAY")
    private MenuDay menuDay;
    @Schema(description = "List of food items in this menu")
    private List<CustomerFoodItemResponse> items;
    @Schema(description = "Date and time when the menu was created", example = "2024-01-01T00:00:00")
    private LocalDateTime createdOn;
}