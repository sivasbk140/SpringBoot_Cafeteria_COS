package net.breezeware.Spring_Boot_Cafeteria.food.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.breezeware.Spring_Boot_Cafeteria.food.dto.FoodItemRequestDto;
import net.breezeware.Spring_Boot_Cafeteria.food.dto.FoodItemResponseDto;
import net.breezeware.Spring_Boot_Cafeteria.food.service.FoodItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/food-items")
@RequiredArgsConstructor
public class FoodItemController {

    private final FoodItemService foodItemService;

    @PostMapping
    public ResponseEntity<FoodItemResponseDto> addFoodItem(@RequestBody FoodItemRequestDto request) {
        log.info("Adding food item");
        FoodItemResponseDto response = foodItemService.addItems(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<FoodItemResponseDto>> getAllFoodItems() {
        log.info("Fetching all food items");
        List<FoodItemResponseDto> response = foodItemService.getAllItems();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodItemResponseDto> getFoodItemById(@PathVariable Long id) {
        log.info("Fetching food item by id: {}", id);
        FoodItemResponseDto response = foodItemService.viewItemById(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<FoodItemResponseDto> removeFoodItem(@PathVariable Long id) {
        log.info("Removing food item by id: {}", id);
        FoodItemResponseDto response = foodItemService.removeItemById(id);
        return ResponseEntity.ok(response);
    }
}