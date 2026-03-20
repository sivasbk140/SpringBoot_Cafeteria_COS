package net.breezeware.Spring_Boot_Cafeteria.food.service;

import lombok.RequiredArgsConstructor;
import net.breezeware.Spring_Boot_Cafeteria.food.dto.*;
import net.breezeware.Spring_Boot_Cafeteria.food.entity.*;
import net.breezeware.Spring_Boot_Cafeteria.food.enumeration.MenuDay;
import net.breezeware.Spring_Boot_Cafeteria.food.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class StaffFoodService {

    private final FoodItemRepository foodItemRepository;
    private final FoodMenuRepository foodMenuRepository;
    private final FoodMenuItemMapRepository menuItemMapRepository;

    // ═══════════════════════════════════════════════════════
    // View Operations
    // ═══════════════════════════════════════════════════════

    public List<FoodItemResponse> getAllFoodItems() {
        return foodItemRepository.findAll().stream()
                .map(this::mapFoodItemToResponse)
                .collect(Collectors.toList());
    }

    public FoodItemResponse getFoodItemById(Long id) {
        FoodItem foodItem = foodItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Food item not found with id: " + id));
        return mapFoodItemToResponse(foodItem);
    }

    public List<FoodItemResponse> getAvailableFoodItems() {
        return foodItemRepository.findAvailableItems().stream()
                .map(this::mapFoodItemToResponse)
                .collect(Collectors.toList());
    }

    public List<FoodItemResponse> getLowStockItems(int threshold) {
        return foodItemRepository.findLowStock(threshold).stream()
                .map(this::mapFoodItemToResponse)
                .collect(Collectors.toList());
    }

    public List<FoodItemResponse> getFoodItemsByCategory(String category) {
        return foodItemRepository.findByCategory(category).stream()
                .map(this::mapFoodItemToResponse)
                .collect(Collectors.toList());
    }

    // ═══════════════════════════════════════════════════════
    // Inventory Management
    // ═══════════════════════════════════════════════════════

    public FoodItemResponse updateStock(Long foodItemId, Integer newQuantity) {
        FoodItem foodItem = foodItemRepository.findById(foodItemId)
                .orElseThrow(() -> new RuntimeException("Food item not found with id: " + foodItemId));

        foodItem.setQuantity(newQuantity);

        FoodItem updated = foodItemRepository.save(foodItem);
        return mapFoodItemToResponse(updated);
    }

    public FoodItemResponse reduceStock(Long foodItemId, Integer amount) {
        FoodItem foodItem = foodItemRepository.findById(foodItemId)
                .orElseThrow(() -> new RuntimeException("Food item not found with id: " + foodItemId));

        foodItem.reduceStock(amount);

        FoodItem updated = foodItemRepository.save(foodItem);
        return mapFoodItemToResponse(updated);
    }

    public FoodItemResponse restoreStock(Long foodItemId, Integer amount) {
        FoodItem foodItem = foodItemRepository.findById(foodItemId)
                .orElseThrow(() -> new RuntimeException("Food item not found with id: " + foodItemId));

        foodItem.restoreStock(amount);

        FoodItem updated = foodItemRepository.save(foodItem);
        return mapFoodItemToResponse(updated);
    }

    // ═══════════════════════════════════════════════════════
    // Menu Management
    // ═══════════════════════════════════════════════════════

    public List<FoodMenuResponse> getMenusForDay(MenuDay day) {
        List<FoodMenu> allMenus = foodMenuRepository.findAll();

        return allMenus.stream()
                .filter(menu -> menu.getAvailabilities().stream()
                        .anyMatch(avail -> avail.getMenuDay() == day))
                .map(this::mapMenuToResponse)
                .collect(Collectors.toList());
    }

    public List<FoodMenuResponse> getAllMenus() {
        return foodMenuRepository.findAll().stream()
                .map(this::mapMenuToResponse)
                .collect(Collectors.toList());
    }

    public FoodMenuResponse getMenuById(Long id) {
        FoodMenu menu = foodMenuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu not found with id: " + id));
        return mapMenuToResponse(menu);
    }

    // ═══════════════════════════════════════════════════════
    // Menu Item Availability
    // ═══════════════════════════════════════════════════════

    public void toggleMenuItemAvailability(Long menuItemMapId) {
        FoodMenuItemMap mapping = menuItemMapRepository.findById(menuItemMapId)
                .orElseThrow(() -> new RuntimeException("Menu item mapping not found"));

        mapping.toggleAvailability();
        menuItemMapRepository.save(mapping);
    }

    public void makeMenuItemAvailable(Long menuItemMapId) {
        FoodMenuItemMap mapping = menuItemMapRepository.findById(menuItemMapId)
                .orElseThrow(() -> new RuntimeException("Menu item mapping not found"));

        mapping.makeAvailable();
        menuItemMapRepository.save(mapping);
    }

    public void makeMenuItemUnavailable(Long menuItemMapId) {
        FoodMenuItemMap mapping = menuItemMapRepository.findById(menuItemMapId)
                .orElseThrow(() -> new RuntimeException("Menu item mapping not found"));

        mapping.makeUnavailable();
        menuItemMapRepository.save(mapping);
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
        List<FoodMenuItemMapResponse> itemResponses = menu.getMenuItems().stream()
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