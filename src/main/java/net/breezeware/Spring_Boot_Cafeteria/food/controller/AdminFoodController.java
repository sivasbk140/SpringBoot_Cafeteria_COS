package net.breezeware.Spring_Boot_Cafeteria.food.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.breezeware.Spring_Boot_Cafeteria.food.dto.*;
import net.breezeware.Spring_Boot_Cafeteria.food.enumeration.MenuDay;
import net.breezeware.Spring_Boot_Cafeteria.food.service.AdminFoodService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ADMIN FOOD CONTROLLER
 * Endpoints for admin to manage food items and menus
 * User Stories: 1, 2, 3, 4, 5, 6, 7, 8
 */
@RestController
@RequestMapping("/api/admin/food")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminFoodController {

    private final AdminFoodService adminFoodService;

    // ═══════════════════════════════════════════════════════
    // FOOD ITEMS - Stories 1, 2, 3, 4
    // ═══════════════════════════════════════════════════════

    /**
     * STORY 1: Create Food Item
     * POST /api/admin/food/items
     */
    @PostMapping("/items")
    public ResponseEntity<FoodItemResponse> createFoodItem(
            @Valid @RequestBody FoodItemRequest request) {
        FoodItemResponse response = adminFoodService.createFoodItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * STORY 2: View All Food Items
     * GET /api/admin/food/items
     */
    @GetMapping("/items")
    public ResponseEntity<List<FoodItemResponse>> getAllFoodItems() {
        List<FoodItemResponse> items = adminFoodService.getAllFoodItems();
        return ResponseEntity.ok(items);
    }

    /**
     * STORY 2: View Food Item by ID
     * GET /api/admin/food/items/{id}
     */
    @GetMapping("/items/{id}")
    public ResponseEntity<FoodItemResponse> getFoodItemById(@PathVariable Long id) {
        FoodItemResponse item = adminFoodService.getFoodItemById(id);
        return ResponseEntity.ok(item);
    }

    /**
     * STORY 3: Update Food Item
     * PUT /api/admin/food/items/{id}
     */
    @PutMapping("/items/{id}")
    public ResponseEntity<FoodItemResponse> updateFoodItem(
            @PathVariable Long id,
            @Valid @RequestBody FoodItemRequest request) {
        FoodItemResponse updated = adminFoodService.updateFoodItem(id, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * STORY 4: Delete Food Item
     * DELETE /api/admin/food/items/{id}
     */
    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteFoodItem(@PathVariable Long id) {
        adminFoodService.deleteFoodItem(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get food items by category
     * GET /api/admin/food/items/category/{category}
     */
    @GetMapping("/items/category/{category}")
    public ResponseEntity<List<FoodItemResponse>> getFoodItemsByCategory(
            @PathVariable String category) {
        List<FoodItemResponse> items = adminFoodService.getFoodItemsByCategory(category);
        return ResponseEntity.ok(items);
    }

    /**
     * Get low stock items
     * GET /api/admin/food/items/low-stock?threshold=10
     */
    @GetMapping("/items/low-stock")
    public ResponseEntity<List<FoodItemResponse>> getLowStockItems(
            @RequestParam(defaultValue = "10") int threshold) {
        List<FoodItemResponse> items = adminFoodService.getLowStockItems(threshold);
        return ResponseEntity.ok(items);
    }

    /**
     * Search food items
     * GET /api/admin/food/items/search?keyword=burger
     */
    @GetMapping("/items/search")
    public ResponseEntity<List<FoodItemResponse>> searchFoodItems(
            @RequestParam String keyword) {
        List<FoodItemResponse> items = adminFoodService.searchFoodItems(keyword);
        return ResponseEntity.ok(items);
    }

    // ═══════════════════════════════════════════════════════
    // MENUS - Stories 5, 6, 7, 8
    // ═══════════════════════════════════════════════════════

    /**
     * STORY 5: Create Food Menu
     * POST /api/admin/food/menus
     */
    @PostMapping("/menus")
    public ResponseEntity<FoodMenuResponse> createFoodMenu(
            @Valid @RequestBody FoodMenuRequest request) {
        FoodMenuResponse response = adminFoodService.createFoodMenu(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * STORY 6: View All Food Menus
     * GET /api/admin/food/menus
     */
    @GetMapping("/menus")
    public ResponseEntity<List<FoodMenuResponse>> getAllFoodMenus() {
        List<FoodMenuResponse> menus = adminFoodService.getAllFoodMenus();
        return ResponseEntity.ok(menus);
    }

    /**
     * STORY 6: View Food Menu by ID
     * GET /api/admin/food/menus/{id}
     */
    @GetMapping("/menus/{id}")
    public ResponseEntity<FoodMenuResponse> getFoodMenuById(@PathVariable Long id) {
        FoodMenuResponse menu = adminFoodService.getFoodMenuById(id);
        return ResponseEntity.ok(menu);
    }

    /**
     * Get menu by category
     * GET /api/admin/food/menus/category/{category}
     */
    @GetMapping("/menus/category/{category}")
    public ResponseEntity<FoodMenuResponse> getMenuByCategory(@PathVariable String category) {
        FoodMenuResponse menu = adminFoodService.getMenuByCategory(category);
        return ResponseEntity.ok(menu);
    }

    /**
     * STORY 7: Update Food Menu
     * PUT /api/admin/food/menus/{id}
     */
    @PutMapping("/menus/{id}")
    public ResponseEntity<FoodMenuResponse> updateFoodMenu(
            @PathVariable Long id,
            @Valid @RequestBody FoodMenuRequest request) {
        FoodMenuResponse updated = adminFoodService.updateFoodMenu(id, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * STORY 8: Delete Food Menu
     * DELETE /api/admin/food/menus/{id}
     */
    @DeleteMapping("/menus/{id}")
    public ResponseEntity<Void> deleteFoodMenu(@PathVariable Long id) {
        adminFoodService.deleteFoodMenu(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Add food item to menu
     * POST /api/admin/food/menus/{menuId}/items/{foodItemId}
     */
    @PostMapping("/menus/{menuId}/items/{foodItemId}")
    public ResponseEntity<FoodMenuResponse> addFoodItemToMenu(
            @PathVariable Long menuId,
            @PathVariable Long foodItemId) {
        FoodMenuResponse updated = adminFoodService.addFoodItemToMenu(menuId, foodItemId);
        return ResponseEntity.ok(updated);
    }

    /**
     * Remove food item from menu
     * DELETE /api/admin/food/menus/{menuId}/items/{foodItemId}
     */
    @DeleteMapping("/menus/{menuId}/items/{foodItemId}")
    public ResponseEntity<FoodMenuResponse> removeFoodItemFromMenu(
            @PathVariable Long menuId,
            @PathVariable Long foodItemId) {
        FoodMenuResponse updated = adminFoodService.removeFoodItemFromMenu(menuId, foodItemId);
        return ResponseEntity.ok(updated);
    }

    /**
     * Set menu availability days
     * PUT /api/admin/food/menus/{menuId}/availability
     * Body: ["MONDAY", "WEDNESDAY", "FRIDAY"]
     */
    @PutMapping("/menus/{menuId}/availability")
    public ResponseEntity<FoodMenuResponse> setMenuAvailability(
            @PathVariable Long menuId,
            @RequestBody List<MenuDay> days) {
        FoodMenuResponse updated = adminFoodService.setMenuAvailability(menuId, days);
        return ResponseEntity.ok(updated);
    }
}