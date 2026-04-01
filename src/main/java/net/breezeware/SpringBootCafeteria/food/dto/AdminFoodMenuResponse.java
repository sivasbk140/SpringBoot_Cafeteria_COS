package net.breezeware.SpringBootCafeteria.food.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import net.breezeware.SpringBootCafeteria.food.enumeration.MenuDay;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminFoodMenuResponse {

    @Schema(description = "Unique identifier of the food menu", example = "1")
    private Long id;
    @Schema(description = "admin menu category ",example = "BREAKFAST")
    private String category;
    @Schema(description = "admin menu Day",example = "MONDAY")
    private MenuDay menuDay;
    @Schema(description = "List of food items mapped to this menu")
    private List<FoodMenuItemMapResponse> items;
    @Schema(description = "Date and time when the menu was created", example = "2024-01-01T00:00:00")
    private LocalDateTime createdOn;
}