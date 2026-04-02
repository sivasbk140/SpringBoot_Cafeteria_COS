package net.breezeware.SpringBootCafeteria.food.service;

import lombok.RequiredArgsConstructor;
import net.breezeware.SpringBootCafeteria.food.dto.CustomerFoodItemResponse;
import net.breezeware.SpringBootCafeteria.food.dto.CustomerFoodMenuResponse;
import net.breezeware.SpringBootCafeteria.food.entity.FoodItem;
import net.breezeware.SpringBootCafeteria.food.entity.FoodMenu;
import net.breezeware.SpringBootCafeteria.food.enumeration.MenuDay;
import net.breezeware.SpringBootCafeteria.food.repo.FoodItemRepository;
import net.breezeware.SpringBootCafeteria.food.repo.FoodMenuRepository;
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


    public List<CustomerFoodMenuResponse> getMenusForDay(MenuDay day) {
        return foodMenuRepository.findByMenuDay(day).stream()
                .map(this::mapMenuToResponse)
                .collect(Collectors.toList());
    }

    public List<CustomerFoodMenuResponse> getAllAvailableMenus() {
        return foodMenuRepository.findAll().stream()
                .map(this::mapMenuToResponse)
                .collect(Collectors.toList());
    }

    public List<CustomerFoodItemResponse> getAvailableFoodItems() {
        return foodItemRepository.findAvailableItems().stream()
                .map(this::mapFoodItemToResponse)
                .collect(Collectors.toList());
    }

    public List<CustomerFoodItemResponse> getFoodItemsByCategory(String category) {
        return foodItemRepository.findByCategory(category).stream()
                .filter(item -> item.getQuantity() > 0)
                .map(this::mapFoodItemToResponse)
                .collect(Collectors.toList());
    }

    public List<CustomerFoodItemResponse> searchFoodItems(String keyword) {
        return foodItemRepository.findByNameContainingIgnoreCase(keyword).stream()
                .filter(item -> item.getQuantity() > 0)
                .map(this::mapFoodItemToResponse)
                .collect(Collectors.toList());
    }



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