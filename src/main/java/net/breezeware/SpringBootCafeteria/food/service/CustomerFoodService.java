package net.breezeware.SpringBootCafeteria.food.service;

import lombok.RequiredArgsConstructor;
import net.breezeware.SpringBootCafeteria.exception.AppCustomException;
import net.breezeware.SpringBootCafeteria.food.dto.CustomerFoodItemResponse;
import net.breezeware.SpringBootCafeteria.food.dto.CustomerFoodMenuResponse;
import net.breezeware.SpringBootCafeteria.food.entity.FoodItem;
import net.breezeware.SpringBootCafeteria.food.entity.FoodMenu;
import net.breezeware.SpringBootCafeteria.food.enumeration.MenuDay;
import net.breezeware.SpringBootCafeteria.food.repo.FoodItemRepository;
import net.breezeware.SpringBootCafeteria.food.repo.FoodMenuRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class responsible for providing read-only food and menu
 * information to customers in the cafeteria system.
 *
 * <p>This service handles browsing of available food menus and food items,
 * including day-based menu filtering, category filtering, and keyword search.
 * All operations are read-only and scoped to available/in-stock items.</p>
 *
 * @author Siva
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerFoodService {

    private final FoodMenuRepository foodMenuRepository;
    private final FoodItemRepository foodItemRepository;

    /**
     * Retrieves all food menus available for a specific day.
     *
     * @param day the menu day (enum) to filter by
     * @return list of CustomerFoodMenuResponse for the given day
     *
     * @throws AppCustomException if no menus are found for the day
     *
     * @implNote Uses MenuDay enum for filtering; only items with quantity &gt; 0 are included.
     */
    public List<CustomerFoodMenuResponse> getMenusForDay(MenuDay day) {
        List<CustomerFoodMenuResponse> menuForDay = foodMenuRepository.findByMenuDay(day).stream()
                .map(this::mapMenuToResponse)
                .collect(Collectors.toList());

        if (menuForDay.isEmpty()) {
            throw new AppCustomException("No menu found for the day: " + day, HttpStatus.NOT_FOUND);
        }
        return menuForDay;
    }

    /**
     * Retrieves all available food menus in the system.
     *
     * @return list of CustomerFoodMenuResponse for all menus
     *
     * @throws AppCustomException if no menus are found
     *
     * @implNote Only menu items with quantity &gt; 0 are included in each menu response.
     */
    public List<CustomerFoodMenuResponse> getAllAvailableMenus() {
        List<CustomerFoodMenuResponse> availableMenu = foodMenuRepository.findAll().stream()
                .map(this::mapMenuToResponse)
                .collect(Collectors.toList());

        if (availableMenu.isEmpty()) {
            throw new AppCustomException("No available menus found", HttpStatus.NOT_FOUND);
        }

        return availableMenu;
    }

    /**
     * Retrieves all food items that are currently available (in stock).
     *
     * @return list of CustomerFoodItemResponse for available items
     *
     * @throws AppCustomException if no available items are found
     *
     * @implNote Uses a repository query to fetch only items marked as available.
     */
    public List<CustomerFoodItemResponse> getAvailableFoodItems() {
        List<CustomerFoodItemResponse> availableItems = foodItemRepository.findAvailableItems().stream()
                .map(this::mapFoodItemToResponse)
                .collect(Collectors.toList());

        if (availableItems.isEmpty()) {
            throw new AppCustomException("No available items found", HttpStatus.NOT_FOUND);
        }
        return availableItems;
    }

    /**
     * Retrieves food items filtered by category, showing only in-stock items.
     *
     * @param category the category name to filter food items
     * @return list of CustomerFoodItemResponse matching the category with quantity &gt; 0
     *
     * @throws AppCustomException if no in-stock items are found for the category
     *
     * @apiNote Category matching depends on DB collation configuration.
     */
    public List<CustomerFoodItemResponse> getFoodItemsByCategory(String category) {
        List<CustomerFoodItemResponse> itemByCat = foodItemRepository.findByCategory(category).stream()
                .filter(item -> item.getQuantity() > 0)
                .map(this::mapFoodItemToResponse)
                .collect(Collectors.toList());

        if (itemByCat.isEmpty()) {
            throw new AppCustomException("No items found for the category: " + category, HttpStatus.NOT_FOUND);
        }
        return itemByCat;
    }

    /**
     * Searches for in-stock food items by name keyword.
     *
     * @param keyword the search keyword to match against food item names
     * @return list of CustomerFoodItemResponse matching the keyword with quantity &gt; 0
     *
     * @throws AppCustomException if no matching in-stock items are found
     *
     * @implNote Uses case-insensitive name matching and filters out out-of-stock items.
     */
    public List<CustomerFoodItemResponse> searchFoodItems(String keyword) {
        List<CustomerFoodItemResponse> searchItems = foodItemRepository.findByNameContainingIgnoreCase(keyword).stream()
                .filter(item -> item.getQuantity() > 0)
                .map(this::mapFoodItemToResponse)
                .collect(Collectors.toList());

        if (searchItems.isEmpty()) {
            throw new AppCustomException("No items found for the keyword: " + keyword, HttpStatus.NOT_FOUND);
        }
        return searchItems;
    }

    /**
     * Maps a FoodItem entity to a CustomerFoodItemResponse DTO.
     *
     * @param foodItem the FoodItem entity to map
     * @return CustomerFoodItemResponse containing customer-visible fields
     *
     * @implNote Internal helper method for DTO conversion.
     */
    private CustomerFoodItemResponse mapFoodItemToResponse(FoodItem foodItem) {
        return new CustomerFoodItemResponse(
                foodItem.getName(),
                foodItem.getPrice(),
                foodItem.getQuantity(),
                foodItem.getCategory(),
                foodItem.getDescription(),
                foodItem.isAvailable()
        );
    }

    /**
     * Maps a FoodMenu entity to a CustomerFoodMenuResponse DTO.
     *
     * @param menu the FoodMenu entity to map
     * @return CustomerFoodMenuResponse with only in-stock menu items included
     *
     * @implNote Filters out menu items with quantity &lt;= 0 during mapping.
     */
    private CustomerFoodMenuResponse mapMenuToResponse(FoodMenu menu) {
        List<CustomerFoodItemResponse> itemResponses = menu.getMenuItems().stream()
                .filter(mapping -> mapping.getFoodItem().getQuantity() > 0)
                .map(mapping -> new CustomerFoodItemResponse(
                        mapping.getFoodItem().getName(),
                        mapping.getFoodItem().getPrice(),
                        mapping.getFoodItem().getQuantity(),
                        mapping.getFoodItem().getCategory(),
                        mapping.getFoodItem().getDescription(),
                        mapping.getIsAvailable()
                ))
                .collect(Collectors.toList());

        return new CustomerFoodMenuResponse(
                menu.getCategory(),
                menu.getMenuDay(),
                itemResponses,
                menu.getCreatedOn()
        );
    }
}
