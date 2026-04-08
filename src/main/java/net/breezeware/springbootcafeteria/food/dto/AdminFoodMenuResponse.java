package net.breezeware.springbootcafeteria.food.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import net.breezeware.springbootcafeteria.food.enumeration.MenuDay;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO representing a food menu response for admin users.
 *
 * Contains full menu details including the list of mapped food items
 * with their availability status and pricing.
 *
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminFoodMenuResponse {

    /** Unique identifier of the food menu. */
    @Schema(description = "Unique identifier of the food menu", example = "1")
    private Long id;

    /** Category of the menu (e.g., BREAKFAST, LUNCH, DINNER). */
    @Schema(description = "admin menu category ",example = "BREAKFAST")
    private String category;

    /** Day of the week this menu is scheduled for. */
    @Schema(description = "admin menu Day",example = "MONDAY")
    private MenuDay menuDay;

    /**
     * List of food items mapped to this menu.
     *
     * @see FoodMenuItemMapResponse
     */
    @Schema(description = "List of food items mapped to this menu")
    private List<FoodMenuItemMapResponse> items;

    /** Timestamp when the menu was created. */
    @Schema(description = "Date and time when the menu was created", example = "2024-01-01T00:00:00")
    private LocalDateTime createdOn;
}