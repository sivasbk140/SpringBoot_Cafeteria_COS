package net.breezeware.SpringBootCafeteria.food.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.breezeware.SpringBootCafeteria.food.dto.AdminFoodMenuResponse;
import net.breezeware.SpringBootCafeteria.food.dto.FoodItemRequest;
import net.breezeware.SpringBootCafeteria.food.dto.FoodItemResponse;
import net.breezeware.SpringBootCafeteria.food.dto.FoodMenuRequest;
import net.breezeware.SpringBootCafeteria.food.enumeration.MenuDay;
import net.breezeware.SpringBootCafeteria.food.service.AdminFoodService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/admin/food")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Admin Food APIs", description = "APIs for Admin to view menus and food items and make changes over them")
public class AdminFoodController {

    private final AdminFoodService adminFoodService;


    @Operation(summary = "Create food item", description = "Adds a new food item to the system")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Food item created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FoodItemResponse.class),
                            examples = @ExampleObject(
                                    value = "{ \"id\":1, \"name\":\"Dosa\", \"price\":50, \"category\":\"BREAKFAST\" }"
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/items")
    public ResponseEntity<FoodItemResponse> createFoodItem(
            @Valid @RequestBody FoodItemRequest request) {
        FoodItemResponse response = adminFoodService.createFoodItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @Operation(summary = "Get all food items", description = "Fetches all available food items")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Food items retrieved",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = FoodItemResponse.class)),
                            examples = @ExampleObject(
                                    value = "[{\"id\":1,\"name\":\"Dosa\",\"price\":50}]"
                            )
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/items")
    public ResponseEntity<List<FoodItemResponse>> getAllFoodItems() {
        List<FoodItemResponse> items = adminFoodService.getAllFoodItems();
        return ResponseEntity.ok(items);
    }


    @Operation(summary = "Get food item by ID", description = "Fetches a food item for the given ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Food item retrieved",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FoodItemResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"id\":1,\"name\":\"Dosa\",\"price\":50}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Food item not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/items/{id}")
    public ResponseEntity<FoodItemResponse> getFoodItemById(@PathVariable Long id) {
        FoodItemResponse item = adminFoodService.getFoodItemById(id);
        return ResponseEntity.ok(item);
    }



    @Operation(summary = "Update food item", description = "Updates an existing food item by ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Food item updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FoodItemResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"id\":1,\"name\":\"Dosa\",\"price\":60}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "404", description = "Food item not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PutMapping("/items/{id}")
    public ResponseEntity<FoodItemResponse> updateFoodItem(
            @PathVariable Long id,
            @Valid @RequestBody FoodItemRequest request) {
        FoodItemResponse updated = adminFoodService.updateFoodItem(id, request);
        return ResponseEntity.ok(updated);
    }



    @Operation(summary = "Delete food item", description = "Deletes a food item by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Food item deleted successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Food item not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteFoodItem(@PathVariable Long id) {
        adminFoodService.deleteFoodItem(id);
        return ResponseEntity.noContent().build();
    }



    @Operation(summary = "Get food items by category", description = "Fetches all food items for a given category")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Food items fetched",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = FoodItemResponse.class)),
                            examples = @ExampleObject(
                                    value = "[{\"id\":1,\"name\":\"Dosa\",\"price\":50}]"
                            )
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/items/category/{category}")
    public ResponseEntity<List<FoodItemResponse>> getFoodItemsByCategory(
            @PathVariable String category) {
        List<FoodItemResponse> items = adminFoodService.getFoodItemsByCategory(category);
        return ResponseEntity.ok(items);
    }



    @Operation(summary = "Get low stock food items", description = "Fetches food items with quantity at or below the threshold")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Low stock food items fetched",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = FoodItemResponse.class)),
                            examples = @ExampleObject(
                                    value = "[{\"id\":1,\"name\":\"Dosa\",\"price\":50,\"quantity\":3}]"
                            )
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/items/low-stock")
    public ResponseEntity<List<FoodItemResponse>> getLowStockItems(
            @RequestParam(defaultValue = "10") int threshold) {
        List<FoodItemResponse> items = adminFoodService.getLowStockItems(threshold);
        return ResponseEntity.ok(items);
    }



    @Operation(summary = "Search food items", description = "Searches food items by name keyword")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Search results returned",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = FoodItemResponse.class)),
                            examples = @ExampleObject(
                                    value = "[{\"id\":2,\"name\":\"Burger\",\"price\":80}]"
                            )
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/items/search")
    public ResponseEntity<List<FoodItemResponse>> searchFoodItems(
            @RequestParam String keyword) {
        List<FoodItemResponse> items = adminFoodService.searchFoodItems(keyword);
        return ResponseEntity.ok(items);
    }




    @Operation(summary = "Create food menu", description = "Creates a new food menu with items and availability days")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Food menu created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdminFoodMenuResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/menus")
    public ResponseEntity<AdminFoodMenuResponse> createFoodMenu(
            @Valid @RequestBody FoodMenuRequest request) {
        AdminFoodMenuResponse response = adminFoodService.createFoodMenu(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }



    @Operation(summary = "Get all food menus", description = "Fetches all food menus")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Food menus retrieved",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = AdminFoodMenuResponse.class))
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/menus")
    public ResponseEntity<List<AdminFoodMenuResponse>> getAllFoodMenus() {
        List<AdminFoodMenuResponse> menus = adminFoodService.getAllFoodMenus();
        return ResponseEntity.ok(menus);
    }


    @Operation(summary = "Get food menu by ID", description = "Fetches a food menu for the given ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Food menu retrieved",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdminFoodMenuResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Menu not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/menus/{id}")
    public ResponseEntity<AdminFoodMenuResponse> getFoodMenuById(@PathVariable Long id) {
        AdminFoodMenuResponse menu = adminFoodService.getFoodMenuById(id);
        return ResponseEntity.ok(menu);
    }



    @Operation(summary = "Get food menus by day", description = "Fetches all menus (BREAKFAST, LUNCH, DINNER) for the given day")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Food menus retrieved",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = AdminFoodMenuResponse.class))
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/menus/day/{day}")
    public ResponseEntity<List<AdminFoodMenuResponse>> getMenusForDay(@PathVariable MenuDay day) {
        List<AdminFoodMenuResponse> menus = adminFoodService.getMenusForDay(day);
        return ResponseEntity.ok(menus);
    }



    @Operation(summary = "Update food menu", description = "Updates an existing food menu by ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Food menu updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdminFoodMenuResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Menu not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PutMapping("/menus/{id}")
    public ResponseEntity<AdminFoodMenuResponse> updateFoodMenu(
            @PathVariable Long id,
            @Valid @RequestBody FoodMenuRequest request) {
        AdminFoodMenuResponse updated = adminFoodService.updateFoodMenu(id, request);
        return ResponseEntity.ok(updated);
    }



    @Operation(summary = "Delete food menu", description = "Deletes a food menu by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Food menu deleted successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Menu not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @DeleteMapping("/menus/{id}")
    public ResponseEntity<Void> deleteFoodMenu(@PathVariable Long id) {
        adminFoodService.deleteFoodMenu(id);
        return ResponseEntity.noContent().build();
    }



    @Operation(summary = "Add food item to menu", description = "Adds an existing food item to a menu")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Food item added to menu",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdminFoodMenuResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Menu or food item not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/menus/{menuId}/items/{foodItemId}")
    public ResponseEntity<AdminFoodMenuResponse> addFoodItemToMenu(
            @PathVariable Long menuId,
            @PathVariable Long foodItemId) {
        AdminFoodMenuResponse updated = adminFoodService.addFoodItemToMenu(menuId, foodItemId);
        return ResponseEntity.ok(updated);
    }



    @Operation(summary = "Remove food item from menu", description = "Removes a food item from a menu")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Food item removed from menu",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdminFoodMenuResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Menu not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @DeleteMapping("/menus/{menuId}/items/{foodItemId}")
    public ResponseEntity<AdminFoodMenuResponse> removeFoodItemFromMenu(
            @PathVariable Long menuId,
            @PathVariable Long foodItemId) {
        AdminFoodMenuResponse updated = adminFoodService.removeFoodItemFromMenu(menuId, foodItemId);
        return ResponseEntity.ok(updated);
    }



}