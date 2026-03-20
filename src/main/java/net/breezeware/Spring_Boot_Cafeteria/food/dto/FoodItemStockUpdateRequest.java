package net.breezeware.Spring_Boot_Cafeteria.food.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// for staff

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodItemStockUpdateRequest {

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;
}