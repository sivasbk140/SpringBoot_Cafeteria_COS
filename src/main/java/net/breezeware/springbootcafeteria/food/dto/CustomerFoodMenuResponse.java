package net.breezeware.springbootcafeteria.food.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.breezeware.springbootcafeteria.food.enumeration.MenuDay;

import java.time.Instant;
import java.util.List;

/**
 * DTO representing a food menu response for customers.
 *
 * Contains the customer-visible menu details along with only in-stock
 * food items. Admin-only fields such as menu ID are excluded.
 *
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerFoodMenuResponse {

    /** Category of the menu (e.g., BREAKFAST, LUNCH, DINNER). */
    @Schema(description = "food menu category  ",example = "BREAKFAST")
    private String category;

    /** Day of the week this menu is scheduled for. */
    @Schema(description = " enter the desired menu day ",example = "MONDAY")
    private MenuDay menuDay;

    /**
     * List of available food items in this menu.
     *
     * @see CustomerFoodItemResponse
     */
    @Schema(description = "List of food items in this menu")
    private List<CustomerFoodItemResponse> items;

    /** Timestamp when the menu was created. */
    @Schema(description = "Date and time when the menu was created", example = "2024-01-01T00:00:00")
    private Instant createdOn;
}