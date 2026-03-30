package food.service;

import net.breezeware.Spring_Boot_Cafeteria.food.dto.FoodItemRequest;
import net.breezeware.Spring_Boot_Cafeteria.food.dto.FoodItemResponse;
import net.breezeware.Spring_Boot_Cafeteria.food.dto.FoodMenuRequest;
import net.breezeware.Spring_Boot_Cafeteria.food.entity.FoodItem;
import net.breezeware.Spring_Boot_Cafeteria.food.repo.FoodItemRepository;
import net.breezeware.Spring_Boot_Cafeteria.food.repo.FoodMenuRepository;
import net.breezeware.Spring_Boot_Cafeteria.food.service.AdminFoodService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminFoodServiceTest {

    @Mock
    private FoodItemRepository foodItemRepository;

    @InjectMocks
    private AdminFoodService adminFoodService;

    private FoodItem foodItem;

    @BeforeEach
    void setUp() {
        foodItem = new FoodItem("Dosa", 50.0, 100, "BREAKFAST");
        foodItem.setDescription("Crispy dosa");
    }


    @Test
    void getFoodItemById_positive_returnsResponse() {
        when(foodItemRepository.findById(1L)).thenReturn(Optional.of(foodItem));

        FoodItemResponse response = adminFoodService.getFoodItemById(1L);

        assertNotNull(response);
        assertEquals("Dosa", response.getName());
        assertEquals(50.0, response.getPrice());
        assertEquals("BREAKFAST", response.getCategory());

        verify(foodItemRepository, times(1)).findById(1L);
    }

    @Test
    void getFoodItemById_negative_notFound_throwsException() {
        when(foodItemRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                adminFoodService.getFoodItemById(99L));

        assertEquals("Food item not found with id: 99", exception.getMessage());
        verify(foodItemRepository, times(1)).findById(99L);
    }


    @Test
    void getAllFoodItems_positive_returnsAllItems() {
        FoodItem second = new FoodItem("Idli", 30.0, 50, "BREAKFAST");
        when(foodItemRepository.findAll()).thenReturn(List.of(foodItem, second));

        List<FoodItemResponse> result = adminFoodService.getAllFoodItems();

        assertEquals(2, result.size());
        assertEquals("Dosa", result.get(0).getName());
        assertEquals("Idli", result.get(1).getName());

        verify(foodItemRepository, times(1)).findAll();
    }

    @Test
    void getAllFoodItems_negative_emptyList_returnsEmpty() {
        when(foodItemRepository.findAll()).thenReturn(List.of());

        List<FoodItemResponse> result = adminFoodService.getAllFoodItems();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(foodItemRepository, times(1)).findAll();
    }


    @Test
    void getFoodItemsByCategory_positive_returnsMatchingItems() {
        when(foodItemRepository.findByCategory("BREAKFAST")).thenReturn(List.of(foodItem));

        List<FoodItemResponse> result = adminFoodService.getFoodItemsByCategory("BREAKFAST");

        assertEquals(1, result.size());
        assertEquals("BREAKFAST", result.getFirst().getCategory());

        verify(foodItemRepository, times(1)).findByCategory("BREAKFAST");
    }

    @Test
    void getFoodItemsByCategory_negative_noMatch_returnsEmpty() {
        when(foodItemRepository.findByCategory("DINNER")).thenReturn(List.of());

        List<FoodItemResponse> result = adminFoodService.getFoodItemsByCategory("DINNER");

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(foodItemRepository, times(1)).findByCategory("DINNER");
    }
    @Test

    void deleteFoodItem_shouldDeleteSuccessfully() {
        when(foodItemRepository.existsById(1L)).thenReturn(true);
        doNothing().when(foodItemRepository).deleteById(1L);

        adminFoodService.deleteFoodItem(1L);

        verify(foodItemRepository, times(1)).existsById(1L);
        verify(foodItemRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteFoodItem_negative_notFound_throwsException() {
        when(foodItemRepository.existsById(99L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                adminFoodService.deleteFoodItem(99L));

        assertEquals("Food item not found with id: 99", exception.getMessage());
        verify(foodItemRepository, times(1)).existsById(99L);
        verify(foodItemRepository, never()).deleteById(any());
    }


    @Test
    void createFoodItem_positive_returnsSavedResponse() {
        FoodItemRequest request = new FoodItemRequest("Dosa", 50.0, "Crispy dosa", 100, "BREAKFAST");
        when(foodItemRepository.save(any(FoodItem.class))).thenReturn(foodItem);

        FoodItemResponse response = adminFoodService.createFoodItem(request);

        assertNotNull(response);
        assertEquals("Dosa", response.getName());
        assertEquals(50.0, response.getPrice());
        assertEquals("BREAKFAST", response.getCategory());
        verify(foodItemRepository, times(1)).save(any(FoodItem.class));
    }

    @Test
    void createFoodItem_negative_repositoryThrows_propagatesException() {
        FoodItemRequest request = new FoodItemRequest("Dosa", 50.0, "Crispy dosa", 100, "BREAKFAST");
        when(foodItemRepository.save(any(FoodItem.class))).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                adminFoodService.createFoodItem(request));

        assertEquals("Database error", exception.getMessage());
        verify(foodItemRepository, times(1)).save(any(FoodItem.class));
    }


    @Test
    void updateFoodItem_positive_returnsUpdatedResponse() {
        FoodItemRequest request = new FoodItemRequest("Dosa Updated", 60.0, "Updated desc", 80, "BREAKFAST");
        FoodItem updated = new FoodItem("Dosa Updated", 60.0, 80, "BREAKFAST");
        when(foodItemRepository.findById(1L)).thenReturn(Optional.of(foodItem));
        when(foodItemRepository.save(any(FoodItem.class))).thenReturn(updated);

        FoodItemResponse response = adminFoodService.updateFoodItem(1L, request);

        assertNotNull(response);
        assertEquals("Dosa Updated", response.getName());
        assertEquals(60.0, response.getPrice());
        verify(foodItemRepository, times(1)).findById(1L);
        verify(foodItemRepository, times(1)).save(any(FoodItem.class));
    }

    @Test
    void updateFoodItem_negative_notFound_throwsException() {
        FoodItemRequest request = new FoodItemRequest("Dosa Updated", 60.0, "Updated desc", 80, "BREAKFAST");
        when(foodItemRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                adminFoodService.updateFoodItem(99L, request));

        assertEquals("Food item not found with id: 99", exception.getMessage());
        verify(foodItemRepository, times(1)).findById(99L);
        verify(foodItemRepository, never()).save(any(FoodItem.class));
    }


}
