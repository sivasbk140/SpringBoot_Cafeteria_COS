package net.breezeware.springbootcafeteria.food.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.breezeware.springbootcafeteria.food.dto.AdminFoodMenuResponse;
import net.breezeware.springbootcafeteria.food.dto.FoodItemRequest;
import net.breezeware.springbootcafeteria.food.dto.FoodItemResponse;
import net.breezeware.springbootcafeteria.food.dto.FoodMenuRequest;
import net.breezeware.springbootcafeteria.food.entity.FoodItem;
import net.breezeware.springbootcafeteria.food.enumeration.MenuDay;
import net.breezeware.springbootcafeteria.food.service.AdminFoodService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Food item creation details",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = FoodItemRequest.class),
                    examples = {
                            @ExampleObject(
                                    name = "Breakfast Item",
                                    value = """
                                        {
                                          "name": "Dosa",
                                          "price": 50.0,
                                          "description": "Crispy south indian dosa",
                                          "quantity": 100,
                                          "category": "BREAKFAST"
                                        }
                                        """
                            ),
                            @ExampleObject(
                                    name = "Snack Item",
                                    value = """
                                        {
                                          "name": "Chicken Burger",
                                          "price": 120.0,
                                          "description": "Grilled chicken with lettuce and sauce",
                                          "quantity": 50,
                                          "category": "SNACK"
                                        }
                                        """
                            )
                    }
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Food item created",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "id": 1,
                          "name": "Dosa",
                          "price": 50.0,
                          "quantity": 100,
                          "category": "BREAKFAST",
                          "description": "Crispy south indian dosa",
                          "isAvailable": true,
                          "createdOn": "2024-01-01T00:00:00.000Z",
                          "updatedOn": "2024-01-01T00:00:00.000Z"
                        }
                    """))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "statusCode": 400,
                          "message": "Invalid input"
                        }
                    """)))
    })
    @PostMapping("/item")
    public ResponseEntity<FoodItemResponse> createFoodItem(
            @Valid @RequestBody FoodItemRequest request) {
        FoodItemResponse response = adminFoodService.createFoodItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @Operation(summary = "Get all food items", description = "Fetches all available food items")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Food items retrieved",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        [
                          {
                            "id": 1,
                            "name": "Dosa",
                            "price": 50.0,
                            "quantity": 100,
                            "category": "BREAKFAST",
                            "description": "Crispy south indian dosa",
                            "isAvailable": true
                          }
                        ]
                    """))),
            @ApiResponse(responseCode = "200", description = "No Items Found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                     []
                        }
                    """)))
    })
    @GetMapping("/items")
    public ResponseEntity<List<FoodItemResponse>> getAllFoodItems() {
        List<FoodItemResponse> items = adminFoodService.getAllFoodItems();
        return ResponseEntity.ok(items);
    }


    @Operation(summary = "Get food item by ID", description = "Fetches a food item for the given ID",
            parameters = {@Parameter(name = "id", description = "ID of the food item to retrieve")})

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Food item retrieved",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "id": 1,
                          "name": "Dosa",
                          "price": 50.0,
                          "quantity": 100,
                          "category": "BREAKFAST",
                          "description": "Crispy south indian dosa",
                          "isAvailable": true
                        }
                    """))),
            @ApiResponse(responseCode = "404", description = "Food item not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "statusCode": 404,
                          "message": "NOT_FOUND",
                          "details": ["Food item with ID not found"]
                        }
                    """)))
    })
    @GetMapping("/item/{id}")
    public ResponseEntity<FoodItemResponse> getFoodItemById(@PathVariable Long id) {
        FoodItemResponse item = adminFoodService.getFoodItemById(id);
        return ResponseEntity.ok(item);
    }


    @Operation(summary = "Update food item", description = "Updates an existing food item by ID",
            parameters = {@Parameter(name = "id", description = "ID of the food item to update")})

    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Food item update details",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = FoodItemRequest.class),
                    examples = {
                            @ExampleObject(
                                    name = "Update price and quantity",
                                    value = """
                                        {
                                          "name": "Dosa",
                                          "price": 60.0,
                                          "description": "Crispy south indian dosa with chutney",
                                          "quantity": 80,
                                          "category": "BREAKFAST"
                                        }
                                        """
                            )
                    }
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Food item updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "id": 1,
                          "name": "Dosa",
                          "price": 60.0,
                          "quantity": 80,
                          "category": "BREAKFAST",
                          "description": "Crispy south indian dosa with chutney",
                          "isAvailable": true
                        }
                    """))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "statusCode": 400,
                          "message": "Invalid input"
                        }
                    """))),
            @ApiResponse(responseCode = "404", description = "Food item not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "statusCode": 404,
                          "message": "NOT_FOUND",
                          "details": ["Food item with ID not found"]
                        }
                    """)))
    })
    @PutMapping("/item/{id}")
    public ResponseEntity<FoodItemResponse> updateFoodItem(
            @PathVariable Long id,
            @Valid @RequestBody FoodItemRequest request) {
        FoodItemResponse updated = adminFoodService.updateFoodItem(id, request);
        return ResponseEntity.ok(updated);
    }





    @Operation(summary = "Delete food item", description = "Deletes a food item by ID",
            parameters = {@Parameter(name = "id", description = "ID of the food item to delete")})

    @ApiResponses({

            @ApiResponse(responseCode = "404", description = "Food item not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "statusCode": 404,
                          "message": "NOT_FOUND",
                          "details": ["Food item with ID not found"]
                        }
                    """)))
    })
    @DeleteMapping("/item/{id}")
    public ResponseEntity<Void> deleteFoodItem(@PathVariable Long id) {
        adminFoodService.deleteFoodItem(id);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Get food items by category", description = "Fetches all food items for a given category",
            parameters = {@Parameter(name = "category", description = "Category to filter food items (e.g. BREAKFAST, LUNCH, SNACK)")})

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Food items fetched",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        [
                          {
                            "id": 1,
                            "name": "Dosa",
                            "price": 50.0,
                            "quantity": 100,
                            "category": "BREAKFAST"
                          }
                        ]
                    """))),
            @ApiResponse(responseCode = "200", description = "No items found for category",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                       []
                    """)))
    })
    @GetMapping("/items/category/{category}")
    public ResponseEntity<List<FoodItemResponse>> getFoodItemsByCategory(
            @PathVariable String category) {
        List<FoodItemResponse> items = adminFoodService.getFoodItemsByCategory(category);
        return ResponseEntity.ok(items);
    }


    @Operation(summary = "Get low stock food items", description = "Fetches food items with quantity at or below the threshold",
            parameters = {@Parameter(name = "threshold", description = "Stock threshold (default 10)")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Low stock food items fetched",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        [
                          {
                            "id": 1,
                            "name": "Dosa",
                            "price": 50.0,
                            "quantity": 3,
                            "category": "BREAKFAST"
                          }
                        ]
                    """))),
            @ApiResponse(responseCode = "200", description = "No items found under the threshold",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(
                            oneOf = {FoodItem[].class},
                            example = """
                                           []
                                           """)))
    })
    @GetMapping("/items/low-stock")
    public ResponseEntity<List<FoodItemResponse>> getLowStockItems(
            @RequestParam(defaultValue = "10") int threshold) {
        List<FoodItemResponse> items = adminFoodService.getLowStockItems(threshold);
        return ResponseEntity.ok(items);
    }


    @Operation(summary = "Search food items", description = "Searches food items by name keyword",
            parameters = {@Parameter(name = "keyword", description = "Search keyword to match against food item names")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search results returned",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        [
                          {
                            "id": 2,
                            "name": "Chicken Burger",
                            "price": 120.0,
                            "quantity": 50,
                            "category": "SNACK"
                          }
                        ]
                    """))),
            @ApiResponse(responseCode = "200", description = "No Food Items Found For The Key",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        []
                    """)))
    })
    @GetMapping("/items/search")
    public ResponseEntity<List<FoodItemResponse>> searchFoodItems(
            @RequestParam String keyword) {
        List<FoodItemResponse> items = adminFoodService.searchFoodItems(keyword);
        return ResponseEntity.ok(items);
    }


    @Operation(summary = "Create food menu", description = "Creates a new food menu with items and availability days")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Food menu creation details",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = FoodMenuRequest.class),
                    examples = {
                            @ExampleObject(
                                    name = "Monday Breakfast Menu",
                                    value = """
                                        {
                                          "category": "BREAKFAST",
                                          "menuDay": "MONDAY",
                                          "foodItemIds": [1, 2, 3]
                                        }
                                        """
                            ),
                            @ExampleObject(
                                    name = "Tuesday Lunch Menu",
                                    value = """
                                        {
                                          "category": "LUNCH",
                                          "menuDay": "TUESDAY",
                                          "foodItemIds": [4, 5]
                                        }
                                        """
                            )
                    }
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Food menu created",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "id": 1,
                          "category": "BREAKFAST",
                          "menuDay": "MONDAY",
                          "items": [
                            {
                              "id": 1,
                              "foodItemId": 1,
                              "foodItemName": "Dosa",
                              "foodItemPrice": 50.0,
                              "isAvailable": true
                            }
                          ],
                          "createdOn": "2024-01-01T00:00:00.000Z"
                        }
                    """))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "statusCode": 400,
                          "message": "Invalid input"
                        }
                    """)))
    })
    @PostMapping("/menu")
    public ResponseEntity<AdminFoodMenuResponse> createFoodMenu(
            @Valid @RequestBody FoodMenuRequest request) {
        AdminFoodMenuResponse response = adminFoodService.createFoodMenu(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @Operation(summary = "Get all food menus", description = "Fetches all food menus")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Food menus retrieved",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        [
                          {
                            "id": 1,
                            "category": "BREAKFAST",
                            "menuDay": "MONDAY",
                            "items": [],
                            "createdOn": "2024-01-01T00:00:00.000Z"
                          }
                        ]
                    """))),
            @ApiResponse(responseCode = "200", description = "No menus found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          []
                        }
                    """)))
    })
    @GetMapping("/menu")
    public ResponseEntity<List<AdminFoodMenuResponse>> getAllFoodMenus() {
        List<AdminFoodMenuResponse> menus = adminFoodService.getAllFoodMenus();
        return ResponseEntity.ok(menus);
    }


    @Operation(summary = "Get food menu by ID", description = "Fetches a food menu for the given ID",
            parameters = {@Parameter(name = "id", description = "ID of the food menu to retrieve")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Food menu retrieved",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "id": 1,
                          "category": "BREAKFAST",
                          "menuDay": "MONDAY",
                          "items": [
                            {
                              "id": 1,
                              "foodItemId": 1,
                              "foodItemName": "Dosa",
                              "foodItemPrice": 50.0,
                              "isAvailable": true
                            }
                          ],
                          "createdOn": "2024-01-01T00:00:00.000Z"
                        }
                    """))),
            @ApiResponse(responseCode = "404", description = "Menu not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "statusCode": 404,
                          "message": "NOT_FOUND",
                          "details": ["Menu with ID not found"]
                        }
                    """)))
    })
    @GetMapping("/menu/{id}")
    public ResponseEntity<AdminFoodMenuResponse> getFoodMenuById(@PathVariable Long id) {
        AdminFoodMenuResponse menu = adminFoodService.getFoodMenuById(id);
        return ResponseEntity.ok(menu);
    }


    @Operation(summary = "Get food menus by day", description = "Fetches all menus (BREAKFAST, LUNCH, DINNER) for the given day",
            parameters = {@Parameter(name = "day", description = "Day of the week (e.g. MONDAY, TUESDAY)")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Food menus retrieved",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        [
                          {
                            "id": 1,
                            "category": "BREAKFAST",
                            "menuDay": "MONDAY",
                            "items": [],
                            "createdOn": "2024-01-01T00:00:00.000Z"
                          }
                        ]
                    """))),
            @ApiResponse(responseCode = "200", description = "No Menus Found For Day",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                      []
                        }
                    """)))
    })
    @GetMapping("/menu/day/{day}")
    public ResponseEntity<List<AdminFoodMenuResponse>> getMenusForDay(@PathVariable MenuDay day) {
        List<AdminFoodMenuResponse> menus = adminFoodService.getMenusForDay(day);
        return ResponseEntity.ok(menus);
    }


    @Operation(summary = "Update food menu", description = "Updates an existing food menu by ID",
            parameters = {@Parameter(name = "id", description = "ID of the food menu to update")})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Food menu update details",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = FoodMenuRequest.class),
                    examples = {
                            @ExampleObject(
                                    name = "Update menu",
                                    value = """
                                        {
                                          "category": "LUNCH",
                                          "menuDay": "WEDNESDAY",
                                          "foodItemIds": [2, 4, 5]
                                        }
                                        """
                            )
                    }
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Food menu updated",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "id": 1,
                          "category": "LUNCH",
                          "menuDay": "WEDNESDAY",
                          "items": [],
                          "createdOn": "2024-01-01T00:00:00.000Z"
                        }
                    """))),
            @ApiResponse(responseCode = "404", description = "Menu not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "statusCode": 404,
                          "message": "NOT_FOUND",
                          "details": ["Menu with ID not found"]
                        }
                    """)))
    })
    @PutMapping("/menu/{id}")
    public ResponseEntity<AdminFoodMenuResponse> updateFoodMenu(
            @PathVariable Long id,
            @Valid @RequestBody FoodMenuRequest request) {
        AdminFoodMenuResponse updated = adminFoodService.updateFoodMenu(id, request);
        return ResponseEntity.ok(updated);
    }


    @Operation(summary = "Delete food menu", description = "Deletes a food menu by ID",
            parameters = {@Parameter(name = "id", description = "ID of the food menu to delete")})
    @ApiResponses({

            @ApiResponse(responseCode = "404", description = "Menu not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "statusCode": 404,
                          "message": "NOT_FOUND",
                          "details": ["Menu with ID not found"]
                        }
                    """)))
    })
    @DeleteMapping("/menu/{id}")
    public ResponseEntity<Void> deleteFoodMenu(@PathVariable Long id) {
        adminFoodService.deleteFoodMenu(id);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Add food item to menu", description = "Adds an existing food item to a menu",
            parameters = {
                    @Parameter(name = "menuId", description = "ID of the menu"),
                    @Parameter(name = "foodItemId", description = "ID of the food item to add")
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Food item added to menu",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "id": 1,
                          "category": "BREAKFAST",
                          "menuDay": "MONDAY",
                          "items": [
                            {
                              "id": 1,
                              "foodItemId": 1,
                              "foodItemName": "Dosa",
                              "foodItemPrice": 50.0,
                              "isAvailable": true
                            }
                          ],
                          "createdOn": "2024-01-01T00:00:00.000Z"
                        }
                    """))),
            @ApiResponse(responseCode = "404", description = "Menu or food item not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "statusCode": 404,
                          "message": "NOT_FOUND",
                          "details": ["Menu or food item not found"]
                        }
                    """)))
    })
    @PostMapping("/menu/{menuId}/item/{foodItemId}")
    public ResponseEntity<AdminFoodMenuResponse> addFoodItemToMenu(
            @PathVariable Long menuId,
            @PathVariable Long foodItemId) {
        AdminFoodMenuResponse updated = adminFoodService.addFoodItemToMenu(menuId, foodItemId);
        return ResponseEntity.ok(updated);
    }


    @Operation(summary = "Remove food item from menu", description = "Removes a food item from a menu",
            parameters = {
                    @Parameter(name = "menuId", description = "ID of the menu"),
                    @Parameter(name = "foodItemId", description = "ID of the food item to remove")
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Food item removed from menu",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "id": 1,
                          "category": "BREAKFAST",
                          "menuDay": "MONDAY",
                          "items": [],
                          "createdOn": "2024-01-01T00:00:00.000Z"
                        }
                    """))),
            @ApiResponse(responseCode = "404", description = "Menu not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "statusCode": 404,
                          "message": "NOT_FOUND",
                          "details": ["Menu not found"]
                        }
                    """)))
    })
    @DeleteMapping("/menu/{menuId}/item/{foodItemId}")
    public ResponseEntity<AdminFoodMenuResponse> removeFoodItemFromMenu(
            @PathVariable Long menuId,
            @PathVariable Long foodItemId) {
        AdminFoodMenuResponse updated = adminFoodService.removeFoodItemFromMenu(menuId, foodItemId);
        return ResponseEntity.ok(updated);
    }
}
