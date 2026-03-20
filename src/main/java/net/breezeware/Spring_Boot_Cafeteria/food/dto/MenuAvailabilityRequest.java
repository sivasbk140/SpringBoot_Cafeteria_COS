package net.breezeware.Spring_Boot_Cafeteria.food.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.breezeware.Spring_Boot_Cafeteria.food.enumeration.MenuDay;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuAvailabilityRequest {

    @NotNull(message = "Days are required")
    @NotEmpty(message = "At least one day must be specified")
    private List<MenuDay> days;
}