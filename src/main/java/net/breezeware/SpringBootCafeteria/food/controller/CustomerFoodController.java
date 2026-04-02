package net.breezeware.SpringBootCafeteria.food.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import net.breezeware.SpringBootCafeteria.food.dto.CustomerFoodItemResponse;
import net.breezeware.SpringBootCafeteria.food.dto.CustomerFoodMenuResponse;
import net.breezeware.SpringBootCafeteria.food.enumeration.MenuDay;
import net.breezeware.SpringBootCafeteria.food.service.CustomerFoodService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/food")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CustomerFoodController {

    private final CustomerFoodService customerFoodService;


    @Operation(summary = "Get menus for a specific day", description = "Returns all menus available on the given day (e.g. MONDAY)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Menus retrieved",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CustomerFoodMenuResponse.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/menus/day/{day}")
    public ResponseEntity<List<CustomerFoodMenuResponse>> getMenusForDay(@PathVariable MenuDay day) {
        List<CustomerFoodMenuResponse> menus = customerFoodService.getMenusForDay(day);
        return ResponseEntity.ok(menus);
    }


    @Operation(summary = "Get all available menus", description = "Returns all food menus visible to the customer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Menus retrieved",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CustomerFoodMenuResponse.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/menus")
    public ResponseEntity<List<CustomerFoodMenuResponse>> getAllAvailableMenus() {
        List<CustomerFoodMenuResponse> menus = customerFoodService.getAllAvailableMenus();
        return ResponseEntity.ok(menus);
    }


    @Operation(summary = "Get available food items", description = "Returns only food items with quantity greater than 0")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Available food items retrieved",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CustomerFoodItemResponse.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/items/available")
    public ResponseEntity<List<CustomerFoodItemResponse>> getAvailableFoodItems() {
        List<CustomerFoodItemResponse> items = customerFoodService.getAvailableFoodItems();
        return ResponseEntity.ok(items);
    }


    @Operation(summary = "Get food items by category", description = "Returns available food items for the given category")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Food items retrieved",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CustomerFoodItemResponse.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/items/category/{category}")
    public ResponseEntity<List<CustomerFoodItemResponse>> getFoodItemsByCategory(@PathVariable String category) {
        List<CustomerFoodItemResponse> items = customerFoodService.getFoodItemsByCategory(category);
        return ResponseEntity.ok(items);
    }


    @Operation(summary = "Search food items", description = "Searches available food items by name keyword")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search results returned",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CustomerFoodItemResponse.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/items/search")
    public ResponseEntity<List<CustomerFoodItemResponse>> searchFoodItems(@RequestParam String keyword) {
        List<CustomerFoodItemResponse> items = customerFoodService.searchFoodItems(keyword);
        return ResponseEntity.ok(items);
    }
}