package net.breezeware.Spring_Boot_Cafeteria.food.service;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import net.breezeware.Spring_Boot_Cafeteria.food.dto.FoodItemRequestDto;
import net.breezeware.Spring_Boot_Cafeteria.food.dto.FoodItemResponseDto;
import net.breezeware.Spring_Boot_Cafeteria.food.entity.FoodItem;
import net.breezeware.Spring_Boot_Cafeteria.food.repo.FoodItemRepository;

import net.breezeware.Spring_Boot_Cafeteria.user.dto.UserResponseDto;
import net.breezeware.Spring_Boot_Cafeteria.user.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FoodItemService {

    private final FoodItemRepository foodItemRepository;

    // add food items

    private FoodItemResponseDto mapToResponse(FoodItem item) {
        return new FoodItemResponseDto(
                item.getId(),
                item.getName(),
                item.getPrice(),
                item.getDescription(),
                item.getQuantity(),
                item.getCategory()
        );
    }

    public FoodItemResponseDto addItems(FoodItemRequestDto request) {
        log.info("Adding food item service layer");
        if (foodItemRepository.existsById(request.getId())) {
            throw new RuntimeException("Item already available");

        }
        FoodItem item = new FoodItem(
                request.getName(),
                request.getPrice(),
                request.getQuantity(),
                request.getCategory(),
                request.getDescription());

        FoodItem savedItem = foodItemRepository.save(item);
        return mapToResponse(savedItem);

    }



    // view all food items

    public List<FoodItemResponseDto> getAllItems() {
        log.info("View all items in service layer");
        return foodItemRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // find food items by id

    public FoodItemResponseDto viewItemById(Long id) {
        log.info("view user by id in service layer");
        FoodItem item = foodItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("item not found"));
        return mapToResponse(item);
    }

    // Delete food item by id


    public FoodItemResponseDto removeItemById(Long id) {
        FoodItem item = foodItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Food item not found"));

        foodItemRepository.delete(item);

        return mapToResponse(item);
    }
}


