

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
public class AdminFoodService {

    private final FoodItemRepository foodItemRepository;
    private final FoodMenuRepository foodMenuRepository;

    // ═══════════════════════════════════════════════════════
    // STORY 1: Create Food Item
    // ═══════════════════════════════════════════════════════
    public FoodItemResponse createFoodItem(FoodItemRequest request) {
        FoodItem foodItem = new FoodItem(
                request.getName(),
                request.getPrice(),
                request.getQuantity(),
                request.getCategory()
        );
        foodItem.setDescription(request.getDescription());

        FoodItem saved = foodItemRepository.save(foodItem);
        return mapFoodItemToResponse(saved);
    }

    // ═══════════════════════════════════════════════════════
    // STORY 2: View Food Items
    // ═══════════════════════════════════════════════════════
    public FoodItemResponse getFoodItemById(Long id) {
        FoodItem foodItem = foodItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Food item not found with id: " + id));
        return mapFoodItemToResponse(foodItem);
    }

    public List<FoodItemResponse> getAllFoodItems() {
        return foodItemRepository.findAll().stream()
                .map(this::mapFoodItemToResponse)
                .collect(Collectors.toList());
    }

    public List<FoodItemResponse> getFoodItemsByCategory(String category) {
        return foodItemRepository.findByCategory(category).stream()
                .map(this::mapFoodItemToResponse)
                .collect(Collectors.toList());
    }

    // ═══════════════════════════════════════════════════════
    // STORY 3: Update Food Item
    // ═══════════════════════════════════════════════════════
    public FoodItemResponse updateFoodItem(Long id, FoodItemRequest request) {
        FoodItem foodItem = foodItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Food item not found with id: " + id));

        if (request.getName() != null) foodItem.setName(request.getName());
        if (request.getPrice() != null) foodItem.setPrice(request.getPrice());
        if (request.getQuantity() != null) foodItem.setQuantity(request.getQuantity());
        if (request.getCategory() != null) foodItem.setCategory(request.getCategory());
        if (request.getDescription() != null) foodItem.setDescription(request.getDescription());

        FoodItem updated = foodItemRepository.save(foodItem);
        return mapFoodItemToResponse(updated);
    }

    // ═══════════════════════════════════════════════════════
    // STORY 4: Delete Food Item
    // ═══════════════════════════════════════════════════════
    public void deleteFoodItem(Long id) {
        if (!foodItemRepository.existsById(id)) {
            throw new RuntimeException("Food item not found with id: " + id);
        }
        foodItemRepository.deleteById(id);
    }

    // ═══════════════════════════════════════════════════════
    // STORY 5: Create Food Menu
    // ═══════════════════════════════════════════════════════
    public AdminFoodMenuResponse createFoodMenu(FoodMenuRequest request) {
        FoodMenu menu = new FoodMenu(request.getCategory(), request.getMenuDay());

        if (request.getFoodItemIds() != null && !request.getFoodItemIds().isEmpty()) {
            for (Long foodItemId : request.getFoodItemIds()) {
                FoodItem foodItem = foodItemRepository.findById(foodItemId)
                        .orElseThrow(() -> new RuntimeException("Food item not found: " + foodItemId));
                menu.addMenuItem(new FoodMenuItemMap(menu, foodItem));
            }
        }

        FoodMenu saved = foodMenuRepository.save(menu);
        return mapMenuToResponse(saved);
    }

    // ═══════════════════════════════════════════════════════
    // STORY 6: View Food Menu
    // ═══════════════════════════════════════════════════════
    public AdminFoodMenuResponse getFoodMenuById(Long id) {
        FoodMenu menu = foodMenuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu not found with id: " + id));
        return mapMenuToResponse(menu);
    }

    public List<AdminFoodMenuResponse> getAllFoodMenus() {
        return foodMenuRepository.findAll().stream()
                .map(this::mapMenuToResponse)
                .collect(Collectors.toList());
    }

    public List<AdminFoodMenuResponse> getMenusForDay(MenuDay day) {
        return foodMenuRepository.findByMenuDay(day).stream()
                .map(this::mapMenuToResponse)
                .collect(Collectors.toList());
    }


    // ═══════════════════════════════════════════════════════
    // STORY 7: Update Food Menu
    // ═══════════════════════════════════════════════════════
    public AdminFoodMenuResponse updateFoodMenu(Long id, FoodMenuRequest request) {
        FoodMenu menu = foodMenuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu not found with id: " + id));

        if (request.getCategory() != null) menu.setCategory(request.getCategory());
        if (request.getMenuDay() != null) menu.setMenuDay(request.getMenuDay());

        if (request.getFoodItemIds() != null && !request.getFoodItemIds().isEmpty()) {
            menu.getMenuItems().clear();
            for (Long foodItemId : request.getFoodItemIds()) {
                FoodItem foodItem = foodItemRepository.findById(foodItemId)
                        .orElseThrow(() -> new RuntimeException("Food item not found: " + foodItemId));
                menu.addMenuItem(new FoodMenuItemMap(menu, foodItem));
            }
        }

        FoodMenu updated = foodMenuRepository.save(menu);
        return mapMenuToResponse(updated);
    }

    // ═══════════════════════════════════════════════════════
    // STORY 8: Delete Food Menu
    // ═══════════════════════════════════════════════════════
    public void deleteFoodMenu(Long id) {
        if (!foodMenuRepository.existsById(id)) {
            throw new RuntimeException("Menu not found with id: " + id);
        }
        foodMenuRepository.deleteById(id);
    }

    // ═══════════════════════════════════════════════════════
    // Additional Admin Operations
    // ═══════════════════════════════════════════════════════

    public AdminFoodMenuResponse addFoodItemToMenu(Long menuId, Long foodItemId) {
        FoodMenu menu = foodMenuRepository.findById(menuId)
                .orElseThrow(() -> new RuntimeException("Menu not found"));

        FoodItem foodItem = foodItemRepository.findById(foodItemId)
                .orElseThrow(() -> new RuntimeException("Food item not found"));

        FoodMenuItemMap mapping = new FoodMenuItemMap(menu, foodItem);
        menu.addMenuItem(mapping);

        FoodMenu updated = foodMenuRepository.save(menu);
        return mapMenuToResponse(updated);
    }

    public AdminFoodMenuResponse removeFoodItemFromMenu(Long menuId, Long foodItemId) {
        FoodMenu menu = foodMenuRepository.findById(menuId)
                .orElseThrow(() -> new RuntimeException("Menu not found"));

        menu.getMenuItems().removeIf(item -> item.getFoodItem().getId().equals(foodItemId));

        FoodMenu updated = foodMenuRepository.save(menu);
        return mapMenuToResponse(updated);
    }


    public List<FoodItemResponse> getLowStockItems(int threshold) {
        return foodItemRepository.findLowStock(threshold).stream()
                .map(this::mapFoodItemToResponse)
                .collect(Collectors.toList());
    }

    public List<FoodItemResponse> searchFoodItems(String keyword) {
        return foodItemRepository.findByNameContainingIgnoreCase(keyword).stream()
                .map(this::mapFoodItemToResponse)
                .collect(Collectors.toList());
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

    private AdminFoodMenuResponse mapMenuToResponse(FoodMenu menu) {
        List<FoodMenuItemMapResponse> itemResponses = menu.getMenuItems().stream()
                .map(mapping -> new FoodMenuItemMapResponse(
                        mapping.getId(),
                        mapping.getFoodItem().getId(),
                        mapping.getFoodItem().getName(),
                        mapping.getFoodItem().getPrice(),
                        mapping.getIsAvailable()
                ))
                .collect(Collectors.toList());

        return new AdminFoodMenuResponse(
                menu.getId(),
                menu.getCategory(),
                menu.getMenuDay(),
                itemResponses,
                menu.getCreatedOn()
        );
    }
}