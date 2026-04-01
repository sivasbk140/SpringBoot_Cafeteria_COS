package net.breezeware.SpringBootCafeteria.food.service;

import net.breezeware.SpringBootCafeteria.food.dto.CustomerFoodItemResponse;
import net.breezeware.SpringBootCafeteria.food.dto.CustomerFoodMenuResponse;
import net.breezeware.SpringBootCafeteria.food.entity.FoodItem;
import net.breezeware.SpringBootCafeteria.food.entity.FoodMenu;
import net.breezeware.SpringBootCafeteria.food.enumeration.MenuDay;
import net.breezeware.SpringBootCafeteria.food.repo.FoodItemRepository;
import net.breezeware.SpringBootCafeteria.food.repo.FoodMenuRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerFoodServiceTest {

    @Mock
    private FoodMenuRepository foodMenuRepository;

    @Mock
    private FoodItemRepository foodItemRepository;

    @InjectMocks
    private CustomerFoodService customerFoodService;

    private FoodItem foodItem;
    private FoodMenu foodMenu;

    @BeforeEach
    void setUp() {
        foodItem = new FoodItem("Dosa", 50.0, 100, "BREAKFAST");
        foodMenu = new FoodMenu("BREAKFAST", MenuDay.MONDAY);
    }

    // ═══════════════════════════════════════════════════════
    // getMenusForDay
    // ═══════════════════════════════════════════════════════

    @Test
    void getMenusForDay_success() {
        when(foodMenuRepository.findByMenuDay(MenuDay.MONDAY)).thenReturn(List.of(foodMenu));

        List<CustomerFoodMenuResponse> result = customerFoodService.getMenusForDay(MenuDay.MONDAY);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("BREAKFAST", result.getFirst().getCategory());
        assertEquals(MenuDay.MONDAY, result.getFirst().getMenuDay());
        verify(foodMenuRepository, times(1)).findByMenuDay(MenuDay.MONDAY);
    }

    @Test
    void getMenusForDay_empty() {
        when(foodMenuRepository.findByMenuDay(MenuDay.SUNDAY)).thenReturn(Collections.emptyList());

        List<CustomerFoodMenuResponse> result = customerFoodService.getMenusForDay(MenuDay.SUNDAY);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(foodMenuRepository, times(1)).findByMenuDay(MenuDay.SUNDAY);
    }

    // ═══════════════════════════════════════════════════════
    // getAllAvailableMenus
    // ═══════════════════════════════════════════════════════

    @Test
    void getAllAvailableMenus_success() {
        when(foodMenuRepository.findAll()).thenReturn(List.of(foodMenu));

        List<CustomerFoodMenuResponse> result = customerFoodService.getAllAvailableMenus();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("BREAKFAST", result.getFirst().getCategory());
        verify(foodMenuRepository, times(1)).findAll();
    }

    @Test
    void getAllAvailableMenus_empty() {
        when(foodMenuRepository.findAll()).thenReturn(Collections.emptyList());

        List<CustomerFoodMenuResponse> result = customerFoodService.getAllAvailableMenus();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(foodMenuRepository, times(1)).findAll();
    }

    // ═══════════════════════════════════════════════════════
    // getAvailableFoodItems
    // ═══════════════════════════════════════════════════════

    @Test
    void getAvailableFoodItems_success() {
        when(foodItemRepository.findAvailableItems()).thenReturn(List.of(foodItem));

        List<CustomerFoodItemResponse> result = customerFoodService.getAvailableFoodItems();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Dosa", result.getFirst().getName());
        assertTrue(result.getFirst().isAvailable());
        verify(foodItemRepository, times(1)).findAvailableItems();
    }

    @Test
    void getAvailableFoodItems_empty() {
        when(foodItemRepository.findAvailableItems()).thenReturn(Collections.emptyList());

        List<CustomerFoodItemResponse> result = customerFoodService.getAvailableFoodItems();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(foodItemRepository, times(1)).findAvailableItems();
    }

    // ═══════════════════════════════════════════════════════
    // getFoodItemsByCategory
    // ═══════════════════════════════════════════════════════

    @Test
    void getFoodItemsByCategory_success() {
        when(foodItemRepository.findByCategory("BREAKFAST")).thenReturn(List.of(foodItem));

        List<CustomerFoodItemResponse> result = customerFoodService.getFoodItemsByCategory("BREAKFAST");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("BREAKFAST", result.getFirst().getCategory());
        verify(foodItemRepository, times(1)).findByCategory("BREAKFAST");
    }

    @Test
    void getFoodItemsByCategory_empty() {
        when(foodItemRepository.findByCategory("DINNER")).thenReturn(Collections.emptyList());

        List<CustomerFoodItemResponse> result = customerFoodService.getFoodItemsByCategory("DINNER");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(foodItemRepository, times(1)).findByCategory("DINNER");
    }

    // ═══════════════════════════════════════════════════════
    // searchFoodItems
    // ═══════════════════════════════════════════════════════

    @Test
    void searchFoodItems_success() {
        when(foodItemRepository.findByNameContainingIgnoreCase("dosa")).thenReturn(List.of(foodItem));

        List<CustomerFoodItemResponse> result = customerFoodService.searchFoodItems("dosa");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Dosa", result.getFirst().getName());
        verify(foodItemRepository, times(1)).findByNameContainingIgnoreCase("dosa");
    }

    @Test
    void searchFoodItems_no_match() {
        when(foodItemRepository.findByNameContainingIgnoreCase("pizza")).thenReturn(Collections.emptyList());

        List<CustomerFoodItemResponse> result = customerFoodService.searchFoodItems("pizza");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(foodItemRepository, times(1)).findByNameContainingIgnoreCase("pizza");
    }
}
