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

    /**
     * STORY 9: View menus available for specific day
     * GET /api/customer/food/menus/day/{day}
     */
    @Operation(summary = "Get menus for a specific day", description = "Returns all menus available on the given day (e.g. MONDAY)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Menus retrieved",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = FoodMenuResponse.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/menus/day/{day}")
    public ResponseEntity<List<FoodMenuResponse>> getMenusForDay(@PathVariable MenuDay day) {
        List<FoodMenuResponse> menus = customerFoodService.getMenusForDay(day);
        return ResponseEntity.ok(menus);
    }

    /**
     * View menu by category
     * GET /api/customer/food/menus/category/{category}
     */
    @Operation(summary = "Get menu by category", description = "Returns the menu for the given category")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Menu retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FoodMenuResponse.class))),
            @ApiResponse(responseCode = "404", description = "Menu not found for category", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/menus/category/{category}")
    public ResponseEntity<FoodMenuResponse> getMenuByCategory(@PathVariable String category) {
        FoodMenuResponse menu = customerFoodService.getMenuByCategory(category);
        return ResponseEntity.ok(menu);
    }

    /**
     * View all available menus
     * GET /api/customer/food/menus
     */
    @Operation(summary = "Get all available menus", description = "Returns all food menus visible to the customer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Menus retrieved",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = FoodMenuResponse.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/menus")
    public ResponseEntity<List<FoodMenuResponse>> getAllAvailableMenus() {
        List<FoodMenuResponse> menus = customerFoodService.getAllAvailableMenus();
        return ResponseEntity.ok(menus);
    }

    /**
     * View available food items only (quantity > 0)
     * GET /api/customer/food/items/available
     */
    @Operation(summary = "Get available food items", description = "Returns only food items with quantity greater than 0")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Available food items retrieved",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = FoodItemResponse.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/items/available")
    public ResponseEntity<List<FoodItemResponse>> getAvailableFoodItems() {
        List<FoodItemResponse> items = customerFoodService.getAvailableFoodItems();
        return ResponseEntity.ok(items);
    }

    /**
     * View food items by category
     * GET /api/customer/food/items/category/{category}
     */
    @Operation(summary = "Get food items by category", description = "Returns available food items for the given category")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Food items retrieved",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = FoodItemResponse.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/items/category/{category}")
    public ResponseEntity<List<FoodItemResponse>> getFoodItemsByCategory(@PathVariable String category) {
        List<FoodItemResponse> items = customerFoodService.getFoodItemsByCategory(category);
        return ResponseEntity.ok(items);
    }

    /**
     * Search food items by name
     * GET /api/customer/food/items/search?keyword=burger
     */
    @Operation(summary = "Search food items", description = "Searches available food items by name keyword")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search results returned",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = FoodItemResponse.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/items/search")
    public ResponseEntity<List<FoodItemResponse>> searchFoodItems(@RequestParam String keyword) {
        List<FoodItemResponse> items = customerFoodService.searchFoodItems(keyword);
        return ResponseEntity.ok(items);
    }

    /**
     * View food item details
     * GET /api/customer/food/items/{id}
     */
    @Operation(summary = "Get food item by ID", description = "Returns details of a specific food item")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Food item retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FoodItemResponse.class))),
            @ApiResponse(responseCode = "404", description = "Food item not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/items/{id}")
    public ResponseEntity<FoodItemResponse> getFoodItemById(@PathVariable Long id) {
        FoodItemResponse item = customerFoodService.getFoodItemById(id);
        return ResponseEntity.ok(item);
    }
}