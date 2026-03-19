package net.breezeware.Spring_Boot_Cafeteria.food.service;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import net.breezeware.Spring_Boot_Cafeteria.food.dto.FoodMenuRequestDto;
import net.breezeware.Spring_Boot_Cafeteria.food.dto.FoodMenuResponseDto;
import net.breezeware.Spring_Boot_Cafeteria.food.entity.FoodMenu;
import net.breezeware.Spring_Boot_Cafeteria.food.enumeration.MenuDay;
import net.breezeware.Spring_Boot_Cafeteria.food.repo.FoodMenuRepository;

import java.util.List;

import static java.awt.SystemColor.menu;

@Data
@Slf4j
@RequiredArgsConstructor
public class FoodMenuService {

    private final FoodMenuRepository foodMenuRepository;


// create food menu
    public FoodMenuResponseDto createMenu(FoodMenuRequestDto request) {

        log.info("Creating food menu");

        FoodMenu menu = new FoodMenu();
        menu.setCategory(request.getCategory());


        FoodMenu savedMenu = foodMenuRepository.save(menu);

        return mapToResponse(savedMenu);
    }
// find menu by day
    public List<FoodMenuResponseDto> viewMenuByDay(MenuDay day) {

        log.info("View menu by day in service layer");

        List<FoodMenu> menus = foodMenuRepository.findByAvailabilities_MenuDay(day);

        if (menus.isEmpty()) {
            throw new RuntimeException("Menu for the day not found");
        }

        return menus.stream()
                .map(this::mapToResponse)
                .toList();
    }


    private FoodMenuResponseDto mapToResponse(FoodMenu savedMenu) {

        return new FoodMenuResponseDto(
                savedMenu.getCategory()

        );
    }
}
