package net.breezeware.Spring_Boot_Cafeteria.food.service;

import lombok.RequiredArgsConstructor;
import net.breezeware.Spring_Boot_Cafeteria.food.dto.*;
import net.breezeware.Spring_Boot_Cafeteria.food.entity.*;
import net.breezeware.Spring_Boot_Cafeteria.food.enumeration.MenuDay;
import net.breezeware.Spring_Boot_Cafeteria.food.repo.FoodItemRepository;
import net.breezeware.Spring_Boot_Cafeteria.food.repo.FoodMenuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerFoodService {

    private final FoodMenuRepository foodMenuRepository;
    private final FoodItemRepository foodItemRepository;

    // ═══════════════════════════════════════════════════════
    // STORY 9: View Food Menu for Specific Day
    // ═══════════════════════════════════════════════════════
    public List<FoodMenuResponse> getMenusForDay(MenuDay day) {
        List<FoodMenu> allMenus = foodMenuRepository.findAll();

        return allMenus.stream()
                .filter(menu -> menu.getAvailabilities().stream()
                        .anyMatch(avail -> avail.getMenuDay() == day))
                .map(this::mapMenuToResponse)
                .collect(Collectors.toList());
    }

    // ═══════════════════════════════════════════════════════
    // Additional Customer Operations
    // ═══════════════════════════════════════════════════════

    public FoodMenuResponse getMenuByCategory(String category) {
        FoodMenu menu = foodMenuRepository.findByCategory(category)
                .orElseThrow(() -> new RuntimeException("Menu not found for category: " + category));
        return mapMenuToResponse(menu);
    }

    public List<FoodMenuResponse> getAllAvailableMenus() {
        return foodMenuRepository.findAll().stream()
                .map(this::mapMenuToResponse)
                .collect(Collectors.toList());
    }

    public List<FoodItemResponse> getAvailableFoodItems() {
        return foodItemRepository.findAvailableItems().stream()
                .map(this::mapFoodItemToResponse)
                .collect(Collectors.toList());
    }

    public List<FoodItemResponse> getFoodItemsByCategory(String category) {
        return foodItemRepository.findByCategory(category).stream()
                .filter(item -> item.getQuantity() > 0)
                .map(this::mapFoodItemToResponse)
                .collect(Collectors.toList());
    }

    public List<FoodItemResponse> searchFoodItems(String keyword) {
        return foodItemRepository.findByNameContainingIgnoreCase(keyword).stream()
                .filter(item -> item.getQuantity() > 0)
                .map(this::mapFoodItemToResponse)
                .collect(Collectors.toList());
    }

    public FoodItemResponse getFoodItemById(Long id) {
        FoodItem foodItem = foodItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Food item not found with id: " + id));
        return mapFoodItemToResponse(foodItem);
    }

    // ═══════════════════════════════════════════════════════
    // Helper Methods
    // ═══════════════════════════════════════════════════════

    private FoodItemResponse mapFoodItemToResponse(FoodItem foodItem) {
        return new FoodItemResponse(
                foodItem.getId(),
                foodItem.getName(),
                foodItem.getPrice(),
                foodItem.getQuantity(),
                foodItem.getCategory(),
                foodItem.getDescription(),
                foodItem.isAvailable(),
                foodItem.getCreatedOn(),
                foodItem.getUpdatedOn()
        );
    }

    private FoodMenuResponse mapMenuToResponse(FoodMenu menu) {
        // Only return available items to customers
        List<FoodMenuItemMapResponse> itemResponses = menu.getMenuItems().stream()
                .filter(mapping -> mapping.getIsAvailable() && mapping.getFoodItem().getQuantity() > 0)
                .map(mapping -> new FoodMenuItemMapResponse(
                        mapping.getId(),
                        mapping.getFoodItem().getId(),
                        mapping.getFoodItem().getName(),
                        mapping.getFoodItem().getPrice(),
                        mapping.getIsAvailable()
                ))
                .collect(Collectors.toList());

        List<MenuDay> availableDays = menu.getAvailabilities().stream()
                .map(AvailabilityMap::getMenuDay)
                .collect(Collectors.toList());

        return new FoodMenuResponse(
                menu.getId(),
                menu.getCategory(),
                itemResponses,
                availableDays,
                menu.getCreatedOn()
        );
    }
}