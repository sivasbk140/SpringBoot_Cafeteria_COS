

package net.breezeware.SpringBootCafeteria.food.service;

import lombok.RequiredArgsConstructor;
import net.breezeware.SpringBootCafeteria.exception.ResourceNotFoundException;
import net.breezeware.SpringBootCafeteria.food.dto.*;
import net.breezeware.SpringBootCafeteria.food.entity.FoodItem;
import net.breezeware.SpringBootCafeteria.food.entity.FoodMenu;
import net.breezeware.SpringBootCafeteria.food.entity.FoodMenuItemMap;
import net.breezeware.SpringBootCafeteria.food.repo.FoodItemRepository;
import net.breezeware.SpringBootCafeteria.food.repo.FoodMenuRepository;
import net.breezeware.SpringBootCafeteria.food.enumeration.MenuDay;
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


    public FoodItemResponse getFoodItemById(Long id) {
        FoodItem foodItem = foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found with id: " + id));
        return mapFoodItemToResponse(foodItem);
    }

    public List<FoodItemResponse> getAllFoodItems() {
        List<FoodItemResponse> items= foodItemRepository.findAll().stream()
                .map(this::mapFoodItemToResponse)
                .collect(Collectors.toList());

        if(items.isEmpty())
        {
            throw new ResourceNotFoundException("No food items found in the system");
        }
        return items;
    }

    public List<FoodItemResponse> getFoodItemsByCategory(String category) {
        return foodItemRepository.findByCategory(category).stream()
                .map(this::mapFoodItemToResponse)
                .collect(Collectors.toList());
    }


    public FoodItemResponse updateFoodItem(Long id, FoodItemRequest request) {
        FoodItem foodItem = foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found with id: " + id));

        if (request.getName() != null) foodItem.setName(request.getName());
        if (request.getPrice() != null) foodItem.setPrice(request.getPrice());
        if (request.getQuantity() != null) foodItem.setQuantity(request.getQuantity());
        if (request.getCategory() != null) foodItem.setCategory(request.getCategory());
        if (request.getDescription() != null) foodItem.setDescription(request.getDescription());

        FoodItem updated = foodItemRepository.save(foodItem);
        return mapFoodItemToResponse(updated);
    }


    public void deleteFoodItem(Long id) {
        if (!foodItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Food item not found with id: " + id);
        }
        foodItemRepository.deleteById(id);
    }


    public AdminFoodMenuResponse createFoodMenu(FoodMenuRequest request) {
        FoodMenu menu = new FoodMenu(request.getCategory(), request.getMenuDay());

        if (request.getFoodItemIds() != null && !request.getFoodItemIds().isEmpty()) {
            for (Long foodItemId : request.getFoodItemIds()) {
                FoodItem foodItem = foodItemRepository.findById(foodItemId)
                        .orElseThrow(() -> new ResourceNotFoundException("Food item not found: " + foodItemId));
                menu.addMenuItem(new FoodMenuItemMap(menu, foodItem));
            }
        }

        FoodMenu saved = foodMenuRepository.save(menu);
        return mapMenuToResponse(saved);
    }


    public AdminFoodMenuResponse getFoodMenuById(Long id) {
        FoodMenu menu = foodMenuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found with id: " + id));
        return mapMenuToResponse(menu);
    }

    public List<AdminFoodMenuResponse> getAllFoodMenus() {
        List<AdminFoodMenuResponse> menus= foodMenuRepository.findAll().stream()
                .map(this::mapMenuToResponse)
                .collect(Collectors.toList());
     if(menus.isEmpty())
     {
         throw  new ResourceNotFoundException("No menus found in the system");
     }

    return menus;
    }

    public List<AdminFoodMenuResponse> getMenusForDay(MenuDay day) {
        List<AdminFoodMenuResponse> menuForDays = foodMenuRepository.findByMenuDay(day).stream()
                .map(this::mapMenuToResponse)
                .collect(Collectors.toList());

        if(menuForDays.isEmpty())
        {
            throw new ResourceNotFoundException("No menu fouund for the day: "+ day);
        }

    return menuForDays;
    }



    public AdminFoodMenuResponse updateFoodMenu(Long id, FoodMenuRequest request) {
        FoodMenu menu = foodMenuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found with id: " + id));

        if (request.getCategory() != null) menu.setCategory(request.getCategory());
        if (request.getMenuDay() != null) menu.setMenuDay(request.getMenuDay());

        if (request.getFoodItemIds() != null && !request.getFoodItemIds().isEmpty()) {
            menu.getMenuItems().clear();
            for (Long foodItemId : request.getFoodItemIds()) {
                FoodItem foodItem = foodItemRepository.findById(foodItemId)
                        .orElseThrow(() -> new ResourceNotFoundException("Food item not found: " + foodItemId));
                menu.addMenuItem(new FoodMenuItemMap(menu, foodItem));
            }
        }

        FoodMenu updated = foodMenuRepository.save(menu);
        return mapMenuToResponse(updated);
    }


    public void deleteFoodMenu(Long id) {
        if (!foodMenuRepository.existsById(id)) {
            throw new ResourceNotFoundException("Menu not found with id: " + id);
        }
        foodMenuRepository.deleteById(id);
    }


    public AdminFoodMenuResponse addFoodItemToMenu(Long menuId, Long foodItemId) {
        FoodMenu menu = foodMenuRepository.findById(menuId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found"));

        FoodItem foodItem = foodItemRepository.findById(foodItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found"));

        FoodMenuItemMap mapping = new FoodMenuItemMap(menu, foodItem);
        menu.addMenuItem(mapping);

        FoodMenu updated = foodMenuRepository.save(menu);
        return mapMenuToResponse(updated);
    }

    public AdminFoodMenuResponse removeFoodItemFromMenu(Long menuId, Long foodItemId) {
        FoodMenu menu = foodMenuRepository.findById(menuId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found"));

        menu.getMenuItems().removeIf(item -> item.getFoodItem().getId().equals(foodItemId));

        FoodMenu updated = foodMenuRepository.save(menu);
        return mapMenuToResponse(updated);
    }


    public List<FoodItemResponse> getLowStockItems(int threshold) {
        List<FoodItemResponse> lowStock = foodItemRepository.findLowStock(threshold).stream()
                .map(this::mapFoodItemToResponse)
                .collect(Collectors.toList());

        if(lowStock.isEmpty())
        {
            throw new ResourceNotFoundException("No low stock items found under the price : "+ threshold);
        }
    return lowStock;
    }

    public List<FoodItemResponse> searchFoodItems(String keyword) {
        List<FoodItemResponse> searchedItems = foodItemRepository.findByNameContainingIgnoreCase(keyword).stream()
                .map(this::mapFoodItemToResponse)
                .collect(Collectors.toList());
        if(searchedItems.isEmpty())
        {
            throw   new ResourceNotFoundException("no itmes Found for the keyword : " +keyword);
        }
    return searchedItems;
    }



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