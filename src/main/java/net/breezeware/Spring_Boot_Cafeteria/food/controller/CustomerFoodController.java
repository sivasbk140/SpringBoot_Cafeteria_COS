package net.breezeware.Spring_Boot_Cafeteria.food.controller;

import lombok.RequiredArgsConstructor;
import net.breezeware.Spring_Boot_Cafeteria.food.dto.*;
import net.breezeware.Spring_Boot_Cafeteria.food.enumeration.MenuDay;
import net.breezeware.Spring_Boot_Cafeteria.food.service.CustomerFoodService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CUSTOMER FOOD CONTROLLER
 * Endpoints for customers to view menus and food items
 * User Story: 9
 */
@RestController
@RequestMapping("/api/customer/food")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CustomerFoodController {

    private final CustomerFoodService customerFoodService;

    // ═══════════════════════════════════════════════════════
    // STORY 9: View Menu for Specific Day
    // ═══════════════════════════════════════════════════════

    /**
     * STORY 9: View menus available for specific day
     * GET /api/customer/food/menus/day/{day}
     * Example: GET /api/customer/food/menus/day/MONDAY
     */
    @GetMapping("/menus/day/{day}")
    public ResponseEntity<List<FoodMenuResponse>> getMenusForDay(@PathVariable MenuDay day) {
        List<FoodMenuResponse> menus = customerFoodService.getMenusForDay(day);
        return ResponseEntity.ok(menus);
    }

    /**
     * View menu by category
     * GET /api/customer/food/menus/category/{category}
     */
    @GetMapping("/menus/category/{category}")
    public ResponseEntity<FoodMenuResponse> getMenuByCategory(@PathVariable String category) {
        FoodMenuResponse menu = customerFoodService.getMenuByCategory(category);
        return ResponseEntity.ok(menu);
    }

    /**
     * View all available menus
     * GET /api/customer/food/menus
     */
    @GetMapping("/menus")
    public ResponseEntity<List<FoodMenuResponse>> getAllAvailableMenus() {
        List<FoodMenuResponse> menus = customerFoodService.getAllAvailableMenus();
        return ResponseEntity.ok(menus);
    }

    /**
     * View available food items only (quantity > 0)
     * GET /api/customer/food/items/available
     */
    @GetMapping("/items/available")
    public ResponseEntity<List<FoodItemResponse>> getAvailableFoodItems() {
        List<FoodItemResponse> items = customerFoodService.getAvailableFoodItems();
        return ResponseEntity.ok(items);
    }

    /**
     * View food items by category
     * GET /api/customer/food/items/category/{category}
     */
    @GetMapping("/items/category/{category}")
    public ResponseEntity<List<FoodItemResponse>> getFoodItemsByCategory(
            @PathVariable String category) {
        List<FoodItemResponse> items = customerFoodService.getFoodItemsByCategory(category);
        return ResponseEntity.ok(items);
    }

    /**
     * Search food items by name
     * GET /api/customer/food/items/search?keyword=burger
     */
    @GetMapping("/items/search")
    public ResponseEntity<List<FoodItemResponse>> searchFoodItems(
            @RequestParam String keyword) {
        List<FoodItemResponse> items = customerFoodService.searchFoodItems(keyword);
        return ResponseEntity.ok(items);
    }

    /**
     * View food item details
     * GET /api/customer/food/items/{id}
     */
    @GetMapping("/items/{id}")
    public ResponseEntity<FoodItemResponse> getFoodItemById(@PathVariable Long id) {
        FoodItemResponse item = customerFoodService.getFoodItemById(id);
        return ResponseEntity.ok(item);
    }
}