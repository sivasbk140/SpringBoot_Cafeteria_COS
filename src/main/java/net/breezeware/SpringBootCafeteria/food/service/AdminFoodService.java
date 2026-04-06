package net.breezeware.SpringBootCafeteria.food.service;

import lombok.RequiredArgsConstructor;
import net.breezeware.SpringBootCafeteria.exception.AppCustomException;
import net.breezeware.SpringBootCafeteria.food.dto.*;
import net.breezeware.SpringBootCafeteria.food.entity.FoodItem;
import net.breezeware.SpringBootCafeteria.food.entity.FoodMenu;
import net.breezeware.SpringBootCafeteria.food.entity.FoodMenuItemMap;
import net.breezeware.SpringBootCafeteria.food.repo.FoodItemRepository;
import net.breezeware.SpringBootCafeteria.food.repo.FoodMenuRepository;
import net.breezeware.SpringBootCafeteria.food.enumeration.MenuDay;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class responsible for managing food items and food menus
 * in the cafeteria system.
 *
 * <p>This service handles CRUD operations for food items and menus,
 * including category-based filtering, menu-day mapping, stock management,
 * and search functionalities.</p>
 *
 * @author Siva
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AdminFoodService {

    private final FoodItemRepository foodItemRepository;
    private final FoodMenuRepository foodMenuRepository;

    /**
     * Creates a new food item in the system.
     *
     * @param request the request containing food item details
     * @return FoodItemResponse containing saved food item details
     *
     * @apiNote Only admin users are allowed to create food items.
     * @implSpec A new FoodItem entity is created and persisted.
     */
    public FoodItemResponse createFoodItem(FoodItemRequest request) {
        FoodItem foodItem = new FoodItem(
                request.getName(),
                request.getPrice(),
                request.getQuantity(),
                request.getCategory(),
                request.getDescription()
        );

        FoodItem saved = foodItemRepository.save(foodItem);
        return mapFoodItemToResponse(saved);
    }

    /**
     * Retrieves a food item by its ID.
     *
     * @param id the unique identifier of the food item
     * @return FoodItemResponse containing food item details
     *
     * @throws AppCustomException if food item is not found
     *
     * @apiNote Returns HTTP 404 if the item does not exist.
     */
    public FoodItemResponse getFoodItemById(Long id) {
        FoodItem foodItem = foodItemRepository.findById(id)
                .orElseThrow(() -> new AppCustomException("Food item not found with id: " + id, HttpStatus.NOT_FOUND));
        return mapFoodItemToResponse(foodItem);
    }

    /**
     * Retrieves all food items available in the system.
     *
     * @return list of FoodItemResponse
     *
     * @throws AppCustomException if no food items are found
     *
     * @implNote Uses stream mapping to convert entities to DTOs.
     */
    public List<FoodItemResponse> getAllFoodItems() {
        List<FoodItemResponse> items = foodItemRepository.findAll().stream()
                .map(this::mapFoodItemToResponse)
                .collect(Collectors.toList());

        if (items.isEmpty()) {
            throw new AppCustomException("No food items found in the system", HttpStatus.NOT_FOUND);
        }
        return items;
    }

    /**
     * Retrieves food items filtered by category.
     *
     * @param category the category to filter food items
     * @return list of FoodItemResponse matching the category
     *
     * @apiNote Category matching is case-sensitive depending on DB configuration.
     */
    public List<FoodItemResponse> getFoodItemsByCategory(String category) {
        return foodItemRepository.findByCategory(category).stream()
                .map(this::mapFoodItemToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing food item partially.
     *
     * @param id      the ID of the food item to update
     * @param request the request containing fields to update
     * @return updated FoodItemResponse
     *
     * @throws AppCustomException if food item is not found
     *
     * @implNote Supports partial updates (only non-null fields are updated).
     */
    public FoodItemResponse updateFoodItem(Long id, FoodItemRequest request) {
        FoodItem foodItem = foodItemRepository.findById(id)
                .orElseThrow(() -> new AppCustomException("Food item not found with id: " + id, HttpStatus.NOT_FOUND));

        if (request.getName() != null) foodItem.setName(request.getName());
        if (request.getPrice() != null) foodItem.setPrice(request.getPrice());
        if (request.getQuantity() != null) foodItem.setQuantity(request.getQuantity());
        if (request.getCategory() != null) foodItem.setCategory(request.getCategory());
        if (request.getDescription() != null) foodItem.setDescription(request.getDescription());

        FoodItem updated = foodItemRepository.save(foodItem);
        return mapFoodItemToResponse(updated);
    }

    /**
     * Deletes a food item by its ID.
     *
     * @param id the ID of the food item to delete
     *
     * @throws AppCustomException if food item does not exist
     *
     * @apiNote This operation is irreversible.
     */
    public void deleteFoodItem(Long id) {
        if (!foodItemRepository.existsById(id)) {
            throw new AppCustomException("Food item not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        foodItemRepository.deleteById(id);
    }

    /**
     * Creates a new food menu and maps food items to it.
     *
     * @param request the request containing menu details and food item IDs
     * @return AdminFoodMenuResponse containing created menu details
     *
     * @throws AppCustomException if any food item is not found
     *
     * @implSpec Each food item is mapped using FoodMenuItemMap entity.
     */
    public AdminFoodMenuResponse createFoodMenu(FoodMenuRequest request) {
        FoodMenu menu = new FoodMenu(request.getCategory(), request.getMenuDay());

        if (request.getFoodItemIds() != null && !request.getFoodItemIds().isEmpty()) {
            for (Long foodItemId : request.getFoodItemIds()) {
                FoodItem foodItem = foodItemRepository.findById(foodItemId)
                        .orElseThrow(() -> new AppCustomException("Food item not found: " + foodItemId, HttpStatus.NOT_FOUND));
                menu.addMenuItem(new FoodMenuItemMap(menu, foodItem));
            }
        }

        FoodMenu saved = foodMenuRepository.save(menu);
        return mapMenuToResponse(saved);
    }

    /**
     * Retrieves a food menu by ID.
     *
     * @param id the ID of the menu
     * @return AdminFoodMenuResponse containing menu details
     *
     * @throws AppCustomException if menu is not found
     */
    public AdminFoodMenuResponse getFoodMenuById(Long id) {
        FoodMenu menu = foodMenuRepository.findById(id)
                .orElseThrow(() -> new AppCustomException("Menu not found with id: " + id, HttpStatus.NOT_FOUND));
        return mapMenuToResponse(menu);
    }

    /**
     * Retrieves all food menus in the system.
     *
     * @return list of AdminFoodMenuResponse
     *
     * @throws AppCustomException if no menus exist
     */
    public List<AdminFoodMenuResponse> getAllFoodMenus() {
        List<AdminFoodMenuResponse> menus = foodMenuRepository.findAll().stream()
                .map(this::mapMenuToResponse)
                .collect(Collectors.toList());

        if (menus.isEmpty()) {
            throw new AppCustomException("No menus found in the system", HttpStatus.NOT_FOUND);
        }

        return menus;
    }

    /**
     * Retrieves menus for a specific day.
     *
     * @param day the menu day (enum)
     * @return list of menus for the given day
     *
     * @throws AppCustomException if no menus found for the day
     *
     * @implNote Uses MenuDay enum for filtering.
     */
    public List<AdminFoodMenuResponse> getMenusForDay(MenuDay day) {
        List<AdminFoodMenuResponse> menuForDays = foodMenuRepository.findByMenuDay(day).stream()
                .map(this::mapMenuToResponse)
                .collect(Collectors.toList());

        if (menuForDays.isEmpty()) {
            throw new AppCustomException("No menu found for the day: " + day, HttpStatus.NOT_FOUND);
        }

        return menuForDays;
    }

    /**
     * Updates an existing food menu.
     *
     * @param id      the menu ID
     * @param request the request containing updated values
     * @return updated AdminFoodMenuResponse
     *
     * @throws AppCustomException if menu or food items not found
     *
     * @implSpec Existing menu items are cleared and replaced if new IDs are provided.
     */
    public AdminFoodMenuResponse updateFoodMenu(Long id, FoodMenuRequest request) {
        FoodMenu menu = foodMenuRepository.findById(id)
                .orElseThrow(() -> new AppCustomException("Menu not found with id: " + id, HttpStatus.NOT_FOUND));

        if (request.getCategory() != null) menu.setCategory(request.getCategory());
        if (request.getMenuDay() != null) menu.setMenuDay(request.getMenuDay());

        if (request.getFoodItemIds() != null && !request.getFoodItemIds().isEmpty()) {
            menu.getMenuItems().clear();
            for (Long foodItemId : request.getFoodItemIds()) {
                FoodItem foodItem = foodItemRepository.findById(foodItemId)
                        .orElseThrow(() -> new AppCustomException("Food item not found: " + foodItemId, HttpStatus.NOT_FOUND));
                menu.addMenuItem(new FoodMenuItemMap(menu, foodItem));
            }
        }

        FoodMenu updated = foodMenuRepository.save(menu);
        return mapMenuToResponse(updated);
    }

    /**
     * Deletes a food menu by ID.
     *
     * @param id the ID of the menu
     *
     * @throws AppCustomException if menu not found
     */
    public void deleteFoodMenu(Long id) {
        if (!foodMenuRepository.existsById(id)) {
            throw new AppCustomException("Menu not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        foodMenuRepository.deleteById(id);
    }

    /**
     * Adds a food item to an existing menu.
     *
     * @param menuId     the ID of the menu
     * @param foodItemId the ID of the food item
     * @return updated AdminFoodMenuResponse
     *
     * @throws AppCustomException if menu or food item not found
     *
     * @implNote Creates a new FoodMenuItemMap entry.
     */
    public AdminFoodMenuResponse addFoodItemToMenu(Long menuId, Long foodItemId) {
        FoodMenu menu = foodMenuRepository.findById(menuId)
                .orElseThrow(() -> new AppCustomException("Menu not found", HttpStatus.NOT_FOUND));

        FoodItem foodItem = foodItemRepository.findById(foodItemId)
                .orElseThrow(() -> new AppCustomException("Food item not found", HttpStatus.NOT_FOUND));

        FoodMenuItemMap mapping = new FoodMenuItemMap(menu, foodItem);
        menu.addMenuItem(mapping);

        FoodMenu updated = foodMenuRepository.save(menu);
        return mapMenuToResponse(updated);
    }

    /**
     * Removes a food item from a menu.
     *
     * @param menuId     the ID of the menu
     * @param foodItemId the ID of the food item
     * @return updated AdminFoodMenuResponse
     *
     * @throws AppCustomException if menu not found
     *
     * @implSpec Removal is performed using predicate filtering.
     */
    public AdminFoodMenuResponse removeFoodItemFromMenu(Long menuId, Long foodItemId) {
        FoodMenu menu = foodMenuRepository.findById(menuId)
                .orElseThrow(() -> new AppCustomException("Menu not found", HttpStatus.NOT_FOUND));

        menu.getMenuItems().removeIf(item -> item.getFoodItem().getId().equals(foodItemId));

        FoodMenu updated = foodMenuRepository.save(menu);
        return mapMenuToResponse(updated);
    }

    /**
     * Retrieves food items with stock below a given threshold.
     *
     * @param threshold the stock limit
     * @return list of low stock food items
     *
     * @throws AppCustomException if no items found under threshold
     */
    public List<FoodItemResponse> getLowStockItems(int threshold) {
        List<FoodItemResponse> lowStock = foodItemRepository.findLowStock(threshold).stream()
                .map(this::mapFoodItemToResponse)
                .collect(Collectors.toList());

        if (lowStock.isEmpty()) {
            throw new AppCustomException("No low stock items found under the threshold: " + threshold, HttpStatus.NOT_FOUND);
        }
        return lowStock;
    }

    /**
     * Searches food items by keyword (name).
     *
     * @param keyword the search keyword
     * @return list of matching food items
     *
     * @throws AppCustomException if no matching items found
     *
     * @implNote Uses case-insensitive search.
     */
    public List<FoodItemResponse> searchFoodItems(String keyword) {
        List<FoodItemResponse> searchedItems = foodItemRepository.findByNameContainingIgnoreCase(keyword).stream()
                .map(this::mapFoodItemToResponse)
                .collect(Collectors.toList());

        if (searchedItems.isEmpty()) {
            throw new AppCustomException("No items found for the keyword: " + keyword, HttpStatus.NOT_FOUND);
        }
        return searchedItems;
    }

    /**
     * Maps FoodItem entity to FoodItemResponse DTO.
     *
     * @param foodItem the entity
     * @return mapped response
     *
     * @implNote Internal helper method for DTO conversion.
     */
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

    /**
     * Maps FoodMenu entity to AdminFoodMenuResponse DTO.
     *
     * @param menu the entity
     * @return mapped response
     *
     * @implNote Includes nested mapping of menu items.
     */
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
