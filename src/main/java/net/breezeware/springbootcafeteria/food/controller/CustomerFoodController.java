package net.breezeware.springbootcafeteria.food.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.breezeware.springbootcafeteria.food.dto.CustomerFoodItemResponse;
import net.breezeware.springbootcafeteria.food.dto.CustomerFoodMenuResponse;
import net.breezeware.springbootcafeteria.food.enumeration.MenuDay;
import net.breezeware.springbootcafeteria.food.service.CustomerFoodService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Customer Food APIs", description = "APIs for customers to browse food items and menus")
@RestController
@RequestMapping("/api/customer/food")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CustomerFoodController {

    private final CustomerFoodService customerFoodService;


    @Operation(summary = "Get menus for a specific day", description = "Returns all menus available on the given day (e.g. MONDAY)",
            parameters = {@Parameter(name = "day", description = "Day of the week (e.g. MONDAY, TUESDAY)")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Menus retrieved",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        [
                          {
                            "category": "BREAKFAST",
                            "menuDay": "MONDAY",
                            "items": [
                              {
                                "name": "Dosa",
                                "price": 50.0,
                                "quantity": 100,
                                "category": "BREAKFAST",
                                "description": "Crispy south indian dosa",
                                "isAvailable": true
                              }
                            ],
                            "createdOn": "2024-01-01T00:00:00.000Z"
                          }
                        ]
                    """))),
            @ApiResponse(responseCode = "200", description = "No Menus Found For The Day",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          []
                        }
                    """)))
    })
    @GetMapping("/menus/day/{day}")
    public ResponseEntity<List<CustomerFoodMenuResponse>> getMenusForDay(@PathVariable MenuDay day) {
        List<CustomerFoodMenuResponse> menus = customerFoodService.getMenusForDay(day);
        return ResponseEntity.ok(menus);
    }


    @Operation(summary = "Get all available menus", description = "Returns all food menus visible to the customer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Menus retrieved",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        [
                          {
                            "category": "BREAKFAST",
                            "menuDay": "MONDAY",
                            "items": [],
                            "createdOn": "2024-01-01T00:00:00.000Z"
                          }
                        ]
                    """)))
    })
    @GetMapping("/menus")
    public ResponseEntity<List<CustomerFoodMenuResponse>> getAllAvailableMenus() {
        List<CustomerFoodMenuResponse> menus = customerFoodService.getAllAvailableMenus();
        return ResponseEntity.ok(menus);
    }


    @Operation(summary = "Get available food items", description = "Returns only food items with quantity greater than 0")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Available food items retrieved",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        [
                          {
                            "name": "Chicken Burger",
                            "price": 120.0,
                            "quantity": 50,
                            "category": "SNACK",
                            "description": "Grilled chicken with lettuce and sauce",
                            "isAvailable": true
                          }
                        ]
                    """))),
            @ApiResponse(responseCode = "200", description = "No Available Food Items Found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          []
                        }
                    """)))
    })
    @GetMapping("/items/available")
    public ResponseEntity<List<CustomerFoodItemResponse>> getAvailableFoodItems() {
        List<CustomerFoodItemResponse> items = customerFoodService.getAvailableFoodItems();
        return ResponseEntity.ok(items);
    }


    @Operation(summary = "Get food items by category", description = "Returns available food items for the given category",
            parameters = {@Parameter(name = "category", description = "Category to filter food items (e.g. BREAKFAST, SNACK)")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Food items retrieved",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        [
                          {
                            "name": "Dosa",
                            "price": 50.0,
                            "quantity": 100,
                            "category": "BREAKFAST",
                            "description": "Crispy south indian dosa",
                            "isAvailable": true
                          }
                        ]
                    """))),
            @ApiResponse(responseCode = "200", description = "No Food Item Found For The Category",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                        []
                        }
                    """)))
    })
    @GetMapping("/items/category/{category}")
    public ResponseEntity<List<CustomerFoodItemResponse>> getFoodItemsByCategory(@PathVariable String category) {
        List<CustomerFoodItemResponse> items = customerFoodService.getFoodItemsByCategory(category);
        return ResponseEntity.ok(items);
    }


    @Operation(summary = "Search food items", description = "Searches available food items by name keyword",
            parameters = {@Parameter(name = "keyword", description = "Search keyword to match against food item names")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search results returned",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        [
                          {
                            "name": "Chicken Burger",
                            "price": 120.0,
                            "quantity": 50,
                            "category": "SNACK",
                            "description": "Grilled chicken with lettuce and sauce",
                            "isAvailable": true
                          }
                        ]
                    """))),
            @ApiResponse(responseCode = "200", description = "No Items Found For The Key",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          []
                        }
                    """)))
    })
    @GetMapping("/items/search")
    public ResponseEntity<List<CustomerFoodItemResponse>> searchFoodItems(@RequestParam String keyword) {
        List<CustomerFoodItemResponse> items = customerFoodService.searchFoodItems(keyword);
        return ResponseEntity.ok(items);
    }
}
