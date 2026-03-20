package net.breezeware.Spring_Boot_Cafeteria.food.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.breezeware.Spring_Boot_Cafeteria.food.enumeration.MenuDay;

// For Menu Availability

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuAvailabilityResponse {
    private Long id;
    private MenuDay menuDay;
}