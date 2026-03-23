package net.breezeware.Spring_Boot_Cafeteria.food.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import net.breezeware.Spring_Boot_Cafeteria.food.dto.*;
import net.breezeware.Spring_Boot_Cafeteria.food.enumeration.MenuDay;
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

    @Operation(summary = "Get all food items", description = "Returns all food items for staff order preparation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Food items retrieved",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = FoodItemResponse.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/items")
    public ResponseEntity<List<FoodItemResponse>> getAllFoodItems() {
        List<FoodItemResponse> items = staffFoodService.getAllFoodItems();
        return ResponseEntity.ok(items);
    }

    @Operation(summary = "Get food item by ID", description = "Returns a specific food item by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Food item retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FoodItemResponse.class))),
            @ApiResponse(responseCode = "404", description = "Food item not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/items/{id}")
    public ResponseEntity<FoodItemResponse> getFoodItemById(@PathVariable Long id) {
        FoodItemResponse item = staffFoodService.getFoodItemById(id);
        return ResponseEntity.ok(item);
    }

    @Operation(summary = "Get available food items", description = "Returns food items with quantity greater than 0")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Available food items retrieved",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = FoodItemResponse.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/items/available")
    public ResponseEntity<List<FoodItemResponse>> getAvailableFoodItems() {
        List<FoodItemResponse> items = staffFoodService.getAvailableFoodItems();
        return ResponseEntity.ok(items);
    }

    @Operation(summary = "Get low stock food items", description = "Returns food items at or below the stock threshold")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Low stock items retrieved",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = FoodItemResponse.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/items/low-stock")
    public ResponseEntity<List<FoodItemResponse>> getLowStockItems(
            @RequestParam(defaultValue = "10") int threshold) {
        List<FoodItemResponse> items = staffFoodService.getLowStockItems(threshold);
        return ResponseEntity.ok(items);
    }

    @Operation(summary = "Get food items by category", description = "Returns food items for a given category")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Food items retrieved",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = FoodItemResponse.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/items/category/{category}")
    public ResponseEntity<List<FoodItemResponse>> getFoodItemsByCategory(@PathVariable String category) {
        List<FoodItemResponse> items = staffFoodService.getFoodItemsByCategory(category);
        return ResponseEntity.ok(items);
    }

    // ═══════════════════════════════════════════════════════
    // Inventory Management
    // ═══════════════════════════════════════════════════════

    @Operation(summary = "Update stock quantity", description = "Sets the stock quantity of a food item to a specific value")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FoodItemResponse.class))),
            @ApiResponse(responseCode = "404", description = "Food item not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PatchMapping("/items/{id}/stock")
    public ResponseEntity<FoodItemResponse> updateStock(
            @PathVariable Long id, @RequestParam Integer quantity) {
        FoodItemResponse updated = staffFoodService.updateStock(id, quantity);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Reduce stock", description = "Reduces the stock of a food item by the given amount (used when preparing an order)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock reduced",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FoodItemResponse.class))),
            @ApiResponse(responseCode = "400", description = "Insufficient stock", content = @Content),
            @ApiResponse(responseCode = "404", description = "Food item not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PatchMapping("/items/{id}/reduce-stock")
    public ResponseEntity<FoodItemResponse> reduceStock(
            @PathVariable Long id, @RequestParam Integer amount) {
        FoodItemResponse updated = staffFoodService.reduceStock(id, amount);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Restore stock", description = "Restores the stock of a food item by the given amount (used when an order is cancelled)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock restored",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FoodItemResponse.class))),
            @ApiResponse(responseCode = "404", description = "Food item not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PatchMapping("/items/{id}/restore-stock")
    public ResponseEntity<FoodItemResponse> restoreStock(
            @PathVariable Long id, @RequestParam Integer amount) {
        FoodItemResponse updated = staffFoodService.restoreStock(id, amount);
        return ResponseEntity.ok(updated);
    }

    // ═══════════════════════════════════════════════════════
    // Menu Operations
    // ═══════════════════════════════════════════════════════

    @Operation(summary = "Get menus for a specific day", description = "Returns menus available on the given day")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Menus retrieved",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = FoodMenuResponse.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/menus/day/{day}")
    public ResponseEntity<List<FoodMenuResponse>> getMenusForDay(@PathVariable MenuDay day) {
        List<FoodMenuResponse> menus = staffFoodService.getMenusForDay(day);
        return ResponseEntity.ok(menus);
    }

    @Operation(summary = "Get all menus", description = "Returns all food menus")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Menus retrieved",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = FoodMenuResponse.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/menus")
    public ResponseEntity<List<FoodMenuResponse>> getAllMenus() {
        List<FoodMenuResponse> menus = staffFoodService.getAllMenus();
        return ResponseEntity.ok(menus);
    }

    @Operation(summary = "Get menu by ID", description = "Returns a food menu by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Menu retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FoodMenuResponse.class))),
            @ApiResponse(responseCode = "404", description = "Menu not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/menus/{id}")
    public ResponseEntity<FoodMenuResponse> getMenuById(@PathVariable Long id) {
        FoodMenuResponse menu = staffFoodService.getMenuById(id);
        return ResponseEntity.ok(menu);
    }

    // ═══════════════════════════════════════════════════════
    // Menu Item Availability Management
    // ═══════════════════════════════════════════════════════

    @Operation(summary = "Toggle menu item availability", description = "Switches a menu item between available and unavailable")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Availability toggled", content = @Content),
            @ApiResponse(responseCode = "404", description = "Menu item mapping not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PatchMapping("/menu-items/{menuItemMapId}/toggle")
    public ResponseEntity<Void> toggleMenuItemAvailability(@PathVariable Long menuItemMapId) {
        staffFoodService.toggleMenuItemAvailability(menuItemMapId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Mark menu item as available", description = "Sets a menu item to available")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Menu item marked available", content = @Content),
            @ApiResponse(responseCode = "404", description = "Menu item mapping not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PatchMapping("/menu-items/{menuItemMapId}/available")
    public ResponseEntity<Void> makeMenuItemAvailable(@PathVariable Long menuItemMapId) {
        staffFoodService.makeMenuItemAvailable(menuItemMapId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Mark menu item as unavailable", description = "Sets a menu item to unavailable (out of stock)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Menu item marked unavailable", content = @Content),
            @ApiResponse(responseCode = "404", description = "Menu item mapping not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PatchMapping("/menu-items/{menuItemMapId}/unavailable")
    public ResponseEntity<Void> makeMenuItemUnavailable(@PathVariable Long menuItemMapId) {
        staffFoodService.makeMenuItemUnavailable(menuItemMapId);
        return ResponseEntity.ok().build();
    }
}