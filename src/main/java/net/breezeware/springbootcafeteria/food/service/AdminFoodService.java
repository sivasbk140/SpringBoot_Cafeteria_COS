package net.breezeware.springbootcafeteria.food.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.breezeware.springbootcafeteria.exception.AppCustomException;
import net.breezeware.springbootcafeteria.food.dto.*;
import net.breezeware.springbootcafeteria.food.entity.FoodItem;
import net.breezeware.springbootcafeteria.food.entity.FoodMenu;
import net.breezeware.springbootcafeteria.food.entity.FoodMenuItemMap;
import net.breezeware.springbootcafeteria.food.dao.FoodItemRepository;
import net.breezeware.springbootcafeteria.food.dao.FoodMenuRepository;
import net.breezeware.springbootcafeteria.food.enumeration.MenuDay;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class responsible for managing food items and food menus
 * in the cafeteria system.
 *
 * This service handles CRUD operations for food items and menus,
 * including category-based filtering, menu-day mapping, stock management,
 * and search functionalities.
 *
 * @author Siva
 * @version 1.0
 * @since 1.0
 */
@Slf4j
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
        log.info("Creating food item in service layer");
        FoodItem foodItem = new FoodItem(
                request.getName(),
                request.getPrice(),
                request.getQuantity(),
                request.getCategory(),
                request.getDescription()
        );

        FoodItem saved = foodItemRepository.save(foodItem);
        log.info("Food item created successfully with name: {}", saved.getName());
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
        log.info("Fetching food item by id in service layer");
        FoodItem foodItem = foodItemRepository.findById(id)
                .orElseThrow(() -> { log.error("Food item not found for id: {}", id);
                    return new AppCustomException("Food item not found with id: " + id, HttpStatus.NOT_FOUND); });
        log.info("Food item found with id: {}", id);
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
        log.info("Fetching all food items in service layer");
        List<FoodItemResponse> items = foodItemRepository.findAll().stream()
                .map(this::mapFoodItemToResponse)
                .collect(Collectors.toList());

        log.info("Returning {} food items", items.size());
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
        log.info("Fetching food items by category: {}", category);
        List<FoodItemResponse> items = foodItemRepository.findByCategory(category).stream()
                .map(this::mapFoodItemToResponse)
                .collect(Collectors.toList());
        log.info("Returning {} food items for category: {}", items.size(), category);
        return items;
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
        log.info("Updating food item with id: {} in service layer", id);
        FoodItem foodItem = foodItemRepository.findById(id)
                .orElseThrow(() -> { log.error("Food item not found for id: {}", id);
                    return new AppCustomException("Food item not found with id: " + id, HttpStatus.NOT_FOUND); });

        if (request.getName() != null) foodItem.setName(request.getName());
        if (request.getPrice() != null) foodItem.setPrice(request.getPrice());
        if (request.getQuantity() != null) foodItem.setQuantity(request.getQuantity());
        if (request.getCategory() != null) foodItem.setCategory(request.getCategory());
        if (request.getDescription() != null) foodItem.setDescription(request.getDescription());

        FoodItem updated = foodItemRepository.save(foodItem);
        log.info("Food item updated successfully with id: {}", id);
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
        log.info("Deleting food item with id: {} in service layer", id);
        if (!foodItemRepository.existsById(id)) {
            log.error("Food item not found for id: {}", id);
            throw new AppCustomException("Food item not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        foodItemRepository.deleteById(id);
        log.info("Food item deleted successfully with id: {}", id);
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
        log.info("Creating food menu in service layer");
        FoodMenu menu = new FoodMenu(request.getCategory(), request.getMenuDay());

        if (request.getFoodItemIds() != null && !request.getFoodItemIds().isEmpty()) {
            for (Long foodItemId : request.getFoodItemIds()) {
                FoodItem foodItem = foodItemRepository.findById(foodItemId)
                        .orElseThrow(() -> { log.error("Food item not found for id: {}", foodItemId);
                            return new AppCustomException("Food item not found: " + foodItemId, HttpStatus.NOT_FOUND); });
                menu.getMenuItems().add(new FoodMenuItemMap(menu, foodItem));
            }
        }

        FoodMenu saved = foodMenuRepository.save(menu);
        log.info("Food menu created successfully with id: {}", saved.getId());
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
        log.info("Fetching food menu by id in service layer");
        FoodMenu menu = foodMenuRepository.findById(id)
                .orElseThrow(() -> { log.error("Menu not found for id: {}", id);
                    return new AppCustomException("Menu not found with id: " + id, HttpStatus.NOT_FOUND); });
        log.info("Menu found with id: {}", id);
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
        log.info("Fetching all food menus in service layer");
        List<AdminFoodMenuResponse> menus = foodMenuRepository.findAll().stream()
                .map(this::mapMenuToResponse)
                .collect(Collectors.toList());

        if (menus.isEmpty()) {
            log.error("No menus found in the system");
            throw new AppCustomException("No menus found in the system", HttpStatus.NOT_FOUND);
        }

        log.info("Returning {} menus", menus.size());
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
        log.info("Fetching menus for day: {} in service layer", day);
        List<AdminFoodMenuResponse> menuForDays = foodMenuRepository.findByMenuDay(day).stream()
                .map(this::mapMenuToResponse)
                .collect(Collectors.toList());

        if (menuForDays.isEmpty()) {
            log.error("No menu found for the day: {}", day);
            throw new AppCustomException("No menu found for the day: " + day, HttpStatus.NOT_FOUND);
        }

        log.info("Returning {} menus for day: {}", menuForDays.size(), day);
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
        log.info("Updating food menu with id: {} in service layer", id);
        FoodMenu menu = foodMenuRepository.findById(id)
                .orElseThrow(() -> { log.error("Menu not found for id: {}", id);
                    return new AppCustomException("Menu not found with id: " + id, HttpStatus.NOT_FOUND); });

        if (request.getCategory() != null) menu.setCategory(request.getCategory());
        if (request.getMenuDay() != null) menu.setMenuDay(request.getMenuDay());

        if (request.getFoodItemIds() != null && !request.getFoodItemIds().isEmpty()) {
            menu.getMenuItems().clear();
            for (Long foodItemId : request.getFoodItemIds()) {
                FoodItem foodItem = foodItemRepository.findById(foodItemId)
                        .orElseThrow(() -> { log.error("Food item not found for id: {}", foodItemId);
                            return new AppCustomException("Food item not found: " + foodItemId, HttpStatus.NOT_FOUND); });
                menu.getMenuItems().add(new FoodMenuItemMap(menu, foodItem));
            }
        }

        FoodMenu updated = foodMenuRepository.save(menu);
        log.info("Food menu updated successfully with id: {}", id);
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
        log.info("Deleting food menu with id: {} in service layer", id);
        if (!foodMenuRepository.existsById(id)) {
            log.error("Menu not found for id: {}", id);
            throw new AppCustomException("Menu not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        foodMenuRepository.deleteById(id);
        log.info("Food menu deleted successfully with id: {}", id);
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
        log.info("Adding food item {} to menu {} in service layer", foodItemId, menuId);
        FoodMenu menu = foodMenuRepository.findById(menuId)
                .orElseThrow(() -> { log.error("Menu not found for id: {}", menuId);
                    return new AppCustomException("Menu not found", HttpStatus.NOT_FOUND); });

        FoodItem foodItem = foodItemRepository.findById(foodItemId)
                .orElseThrow(() -> { log.error("Food item not found for id: {}", foodItemId);
                    return new AppCustomException("Food item not found", HttpStatus.NOT_FOUND); });

        FoodMenuItemMap mapping = new FoodMenuItemMap(menu, foodItem);
        menu.getMenuItems().add(mapping);

        FoodMenu updated = foodMenuRepository.save(menu);
        log.info("Food item {} added to menu {} successfully", foodItemId, menuId);
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
        log.info("Removing food item {} from menu {} in service layer", foodItemId, menuId);
        FoodMenu menu = foodMenuRepository.findById(menuId)
                .orElseThrow(() -> { log.error("Menu not found for id: {}", menuId);
                    return new AppCustomException("Menu not found", HttpStatus.NOT_FOUND); });

        menu.getMenuItems().removeIf(item -> item.getFoodItem().getId().equals(foodItemId));

        FoodMenu updated = foodMenuRepository.save(menu);
        log.info("Food item {} removed from menu {} successfully", foodItemId, menuId);
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
        log.info("Fetching low stock items with threshold: {} in service layer", threshold);
        List<FoodItemResponse> lowStock = foodItemRepository.findLowStock(threshold).stream()
                .map(this::mapFoodItemToResponse)
                .collect(Collectors.toList());

        if (lowStock.isEmpty()) {
            log.error("No low stock items found under the threshold: {}", threshold);
            throw new AppCustomException("No low stock items found under the threshold: " + threshold, HttpStatus.NOT_FOUND);
        }
        log.info("Returning {} low stock items", lowStock.size());
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
        log.info("Searching food items with keyword: {} in service layer", keyword);
        List<FoodItemResponse> searchedItems = foodItemRepository.findByNameContainingIgnoreCase(keyword).stream()
                .map(this::mapFoodItemToResponse)
                .collect(Collectors.toList());

        if (searchedItems.isEmpty()) {
            log.error("No items found for the keyword: {}", keyword);
            throw new AppCustomException("No items found for the keyword: " + keyword, HttpStatus.NOT_FOUND);
        }
        log.info("Returning {} items for keyword: {}", searchedItems.size(), keyword);
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
        log.debug("Mapping food item to response: {}", foodItem.getId());
        return new FoodItemResponse(
                foodItem.getId(),
                foodItem.getName(),
                foodItem.getPrice(),
                foodItem.getQuantity(),
                foodItem.getCategory(),
                foodItem.getDescription(),
                foodItem.getQuantity() > 0,
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
        log.debug("Mapping food menu to response: {}", menu.getId());
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
