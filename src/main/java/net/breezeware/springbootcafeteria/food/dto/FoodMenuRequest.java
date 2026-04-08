package net.breezeware.springbootcafeteria.food.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import net.breezeware.springbootcafeteria.food.enumeration.MenuDay;

import java.util.List;

/**
 * DTO representing the request payload for creating or updating a food menu.
 *
 * This object is used to transfer menu input from the client to the service layer,
 * including which food items should be linked to the menu.
 *
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FoodMenuRequest {

    /** Category of the menu (e.g., BREAKFAST, LUNCH, DINNER). */
    @Schema(description = " menu category  ",example = "BREAKFAST")
    private String category;

    /** Day of the week this menu is applicable for. */
    @Schema(description = " Enter the menu day  ",example = "MONDAY")
    private MenuDay menuDay;

    /**
     * List of food item IDs to associate with this menu.
     *
     * @see net.breezeware.springbootcafeteria.food.entity.FoodMenuItemMap
     */
    @Schema(description = "List of food item IDs to include in the menu", example = "[1, 2, 3]")
    private List<Long> foodItemIds;
}