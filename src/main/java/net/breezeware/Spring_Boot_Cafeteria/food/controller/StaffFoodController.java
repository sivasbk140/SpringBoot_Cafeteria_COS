package net.breezeware.Spring_Boot_Cafeteria.food.controller;

import lombok.RequiredArgsConstructor;
import net.breezeware.Spring_Boot_Cafeteria.food.dto.*;
import net.breezeware.Spring_Boot_Cafeteria.food.entity.MenuDay;
import net.breezeware.Spring_Boot_Cafeteria.food.service.StaffFoodService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/staff/food")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StaffFoodController {

    private final StaffFoodService staffFoodService;

    // ═══════════════════════════════════════════════════════
    // View Operations
    // ═══════════════════════════════════════════════════════

    /**
     * View all food items (for order preparation)
     * GET /api/staff/food/items
     */
    @GetMapping("/items")
    public ResponseEntity<List<FoodItemResponse>> getAllFoodItems() {
        List<FoodItemResponse> items = staffFoodService.getAllFoodItems();
        return ResponseEntity.ok(items);
    }

    /**
     * View food item by ID
     * GET /api/staff/food/items/{id}
     */
    @GetMapping("/items/{id}")
    public ResponseEntity<FoodItemResponse> getFoodItemById(@PathVariable Long id) {
        FoodItemResponse item = staffFoodService.getFoodItemById(id);
        return ResponseEntity.ok(item);
    }

    /**
     * View available items
     * GET /api/staff/food/items/available
     */
    @GetMapping("/items/available")
    public ResponseEntity<List<FoodItemResponse>> getAvailableFoodItems() {
        List<FoodItemResponse> items = staffFoodService.getAvailableFoodItems();
        return ResponseEntity.ok(items);
    }

    /**
     * View low stock items
     * GET /api/staff/food/items/low-stock?threshold=10
     */
    @GetMapping("/items/low-stock")
    public ResponseEntity<List<FoodItemResponse>> getLowStockItems(
            @RequestParam(defaultValue = "10") int threshold) {
        List<FoodItemResponse> items = staffFoodService.getLowStockItems(threshold);
        return ResponseEntity.ok(items);
    }

    /**
     * View items by category
     * GET /api/staff/food/items/category/{category}
     */
    @GetMapping("/items/category/{category}")
    public ResponseEntity<List<FoodItemResponse>> getFoodItemsByCategory(
            @PathVariable String category) {
        List<FoodItemResponse> items = staffFoodService.getFoodItemsByCategory(category);
        return ResponseEntity.ok(items);
    }

    // ═══════════════════════════════════════════════════════
    // Inventory Management
    // ═══════════════════════════════════════════════════════

    /**
     * Update stock quantity (set to specific value)
     * PATCH /api/staff/food/items/{id}/stock?quantity=50
     */
    @PatchMapping("/items/{id}/stock")
    public ResponseEntity<FoodItemResponse> updateStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        FoodItemResponse updated = staffFoodService.updateStock(id, quantity);
        return ResponseEntity.ok(updated);
    }

    /**
     * Reduce stock (when preparing order)
     * PATCH /api/staff/food/items/{id}/reduce-stock?amount=5
     */
    @PatchMapping("/items/{id}/reduce-stock")
    public ResponseEntity<FoodItemResponse> reduceStock(
            @PathVariable Long id,
            @RequestParam Integer amount) {
        FoodItemResponse updated = staffFoodService.reduceStock(id, amount);
        return ResponseEntity.ok(updated);
    }

    /**
     * Restore stock (when order is cancelled)
     * PATCH /api/staff/food/items/{id}/restore-stock?amount=5
     */
    @PatchMapping("/items/{id}/restore-stock")
    public ResponseEntity<FoodItemResponse> restoreStock(
            @PathVariable Long id,
            @RequestParam Integer amount) {
        FoodItemResponse updated = staffFoodService.restoreStock(id, amount);
        return ResponseEntity.ok(updated);
    }

    // ═══════════════════════════════════════════════════════
    // Menu Operations
    // ═══════════════════════════════════════════════════════

    /**
     * View menus for specific day
     * GET /api/staff/food/menus/day/{day}
     */
    @GetMapping("/menus/day/{day}")
    public ResponseEntity<List<FoodMenuResponse>> getMenusForDay(@PathVariable MenuDay day) {
        List<FoodMenuResponse> menus = staffFoodService.getMenusForDay(day);
        return ResponseEntity.ok(menus);
    }

    /**
     * View all menus
     * GET /api/staff/food/menus
     */
    @GetMapping("/menus")
    public ResponseEntity<List<FoodMenuResponse>> getAllMenus() {
        List<FoodMenuResponse> menus = staffFoodService.getAllMenus();
        return ResponseEntity.ok(menus);
    }

    /**
     * View menu by ID
     * GET /api/staff/food/menus/{id}
     */
    @GetMapping("/menus/{id}")
    public ResponseEntity<FoodMenuResponse> getMenuById(@PathVariable Long id) {
        FoodMenuResponse menu = staffFoodService.getMenuById(id);
        return ResponseEntity.ok(menu);
    }

    // ═══════════════════════════════════════════════════════
    // Menu Item Availability Management
    // ═══════════════════════════════════════════════════════

    /**
     * Toggle menu item availability (available ↔ unavailable)
     * PATCH /api/staff/food/menu-items/{menuItemMapId}/toggle
     */
    @PatchMapping("/menu-items/{menuItemMapId}/toggle")
    public ResponseEntity<Void> toggleMenuItemAvailability(@PathVariable Long menuItemMapId) {
        staffFoodService.toggleMenuItemAvailability(menuItemMapId);
        return ResponseEntity.ok().build();
    }

   // making items available

    @PatchMapping("/menu-items/{menuItemMapId}/available")
    public ResponseEntity<Void> makeMenuItemAvailable(@PathVariable Long menuItemMapId) {
        staffFoodService.makeMenuItemAvailable(menuItemMapId);
        return ResponseEntity.ok().build();
    }

   // making items out of stock

    @PatchMapping("/menu-items/{menuItemMapId}/unavailable")
    public ResponseEntity<Void> makeMenuItemUnavailable(@PathVariable Long menuItemMapId) {
        staffFoodService.makeMenuItemUnavailable(menuItemMapId);
        return ResponseEntity.ok().build();
    }
}