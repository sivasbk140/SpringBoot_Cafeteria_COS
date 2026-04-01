package net.breezeware.SpringBootCafeteria.food.controller;

import net.breezeware.SpringBootCafeteria.food.dto.*;
import net.breezeware.SpringBootCafeteria.food.enumeration.MenuDay;
import net.breezeware.SpringBootCafeteria.food.service.AdminFoodService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminFoodControllerTest {

    @Mock
    private AdminFoodService adminFoodService;

    @InjectMocks
    private AdminFoodController adminFoodController;

    private FoodItemRequest foodItemRequest;
    private FoodItemResponse foodItemResponse;
    private FoodMenuRequest foodMenuRequest;
    private AdminFoodMenuResponse menuResponse;

    @BeforeEach
    void setUp() {
        foodItemRequest = new FoodItemRequest("Dosa", 50.0, "Crispy dosa", 100, "BREAKFAST");
        foodItemResponse = new FoodItemResponse(1L, "Dosa", 50.0, 100, "BREAKFAST", "Crispy dosa", true, new Date(), new Date());
        foodMenuRequest = new FoodMenuRequest("BREAKFAST", MenuDay.MONDAY, List.of(1L));
        menuResponse = new AdminFoodMenuResponse(1L, "BREAKFAST", MenuDay.MONDAY,
                List.of(new FoodMenuItemMapResponse(1L, 1L, "Dosa", 50.0, true)),
                LocalDateTime.now());
    }

    // Test for the food items

    @Test
    void createFoodItem_success() {
        when(adminFoodService.createFoodItem(foodItemRequest)).thenReturn(foodItemResponse);

        ResponseEntity<FoodItemResponse> response = adminFoodController.createFoodItem(foodItemRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Dosa", response.getBody().getName());
        assertEquals(50.0, response.getBody().getPrice());
        verify(adminFoodService, times(1)).createFoodItem(foodItemRequest);
    }

    @Test
    void createFoodItem_failed() {
        when(adminFoodService.createFoodItem(foodItemRequest))
                .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                adminFoodController.createFoodItem(foodItemRequest));

        assertEquals("Database error", exception.getMessage());
        verify(adminFoodService, times(1)).createFoodItem(foodItemRequest);
    }

    @Test
    void getAllFoodItems_success() {
        when(adminFoodService.getAllFoodItems()).thenReturn(List.of(foodItemResponse));

        ResponseEntity<List<FoodItemResponse>> response = adminFoodController.getAllFoodItems();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Dosa", response.getBody().getFirst().getName());
        verify(adminFoodService, times(1)).getAllFoodItems();
    }

    @Test
    void getAllFoodItems_empty_list() {
        when(adminFoodService.getAllFoodItems()).thenReturn(List.of());

        ResponseEntity<List<FoodItemResponse>> response = adminFoodController.getAllFoodItems();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getFoodItemById_success() {
        when(adminFoodService.getFoodItemById(1L)).thenReturn(foodItemResponse);

        ResponseEntity<FoodItemResponse> response = adminFoodController.getFoodItemById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Dosa", response.getBody().getName());
        verify(adminFoodService, times(1)).getFoodItemById(1L);
    }

    @Test
    void getFoodItemById_failed() {
        when(adminFoodService.getFoodItemById(99L))
                .thenThrow(new RuntimeException("Food item not found with id: 99"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                adminFoodController.getFoodItemById(99L));

        assertEquals("Food item not found with id: 99", exception.getMessage());
        verify(adminFoodService, times(1)).getFoodItemById(99L);
    }

    @Test
    void updateFoodItem_success() {
        FoodItemResponse updated = new FoodItemResponse(1L, "Dosa Updated", 60.0, 90, "BREAKFAST", "Updated", true, new Date(), new Date());
        when(adminFoodService.updateFoodItem(eq(1L), any(FoodItemRequest.class))).thenReturn(updated);

        ResponseEntity<FoodItemResponse> response = adminFoodController.updateFoodItem(1L, foodItemRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Dosa Updated", response.getBody().getName());
        assertEquals(60.0, response.getBody().getPrice());
        verify(adminFoodService, times(1)).updateFoodItem(eq(1L), any(FoodItemRequest.class));
    }

    @Test
    void updateFoodItem_failed() {
        when(adminFoodService.updateFoodItem(eq(99L), any(FoodItemRequest.class)))
                .thenThrow(new RuntimeException("Food item not found with id: 99"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                adminFoodController.updateFoodItem(99L, foodItemRequest));

        assertEquals("Food item not found with id: 99", exception.getMessage());
        verify(adminFoodService, times(1)).updateFoodItem(eq(99L), any(FoodItemRequest.class));
    }

    @Test
    void deleteFoodItem_success() {
        doNothing().when(adminFoodService).deleteFoodItem(1L);

        ResponseEntity<Void> response = adminFoodController.deleteFoodItem(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(adminFoodService, times(1)).deleteFoodItem(1L);
    }

    @Test
    void deleteFoodItem_failed() {
        doThrow(new RuntimeException("Food item not found with id: 99"))
                .when(adminFoodService).deleteFoodItem(99L);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                adminFoodController.deleteFoodItem(99L));

        assertEquals("Food item not found with id: 99", exception.getMessage());
        verify(adminFoodService, times(1)).deleteFoodItem(99L);
    }

    @Test
    void getFoodItemsByCategory_success() {
        when(adminFoodService.getFoodItemsByCategory("BREAKFAST")).thenReturn(List.of(foodItemResponse));

        ResponseEntity<List<FoodItemResponse>> response = adminFoodController.getFoodItemsByCategory("BREAKFAST");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("BREAKFAST", response.getBody().getFirst().getCategory());
        verify(adminFoodService, times(1)).getFoodItemsByCategory("BREAKFAST");
    }

    @Test
    void getFoodItemsByCategory_empty() {
        when(adminFoodService.getFoodItemsByCategory("DINNER")).thenReturn(List.of());

        ResponseEntity<List<FoodItemResponse>> response = adminFoodController.getFoodItemsByCategory("DINNER");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getLowStockItems_success() {
        FoodItemResponse lowStock = new FoodItemResponse(2L, "Idli", 30.0, 3, "BREAKFAST", null, true, new Date(), new Date());
        when(adminFoodService.getLowStockItems(10)).thenReturn(List.of(lowStock));

        ResponseEntity<List<FoodItemResponse>> response = adminFoodController.getLowStockItems(10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(3, response.getBody().getFirst().getQuantity());
        verify(adminFoodService, times(1)).getLowStockItems(10);
    }

    @Test
    void getLowStockItems_empty() {
        when(adminFoodService.getLowStockItems(5)).thenReturn(List.of());

        ResponseEntity<List<FoodItemResponse>> response = adminFoodController.getLowStockItems(5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void searchFoodItems_success() {
        when(adminFoodService.searchFoodItems("dosa")).thenReturn(List.of(foodItemResponse));

        ResponseEntity<List<FoodItemResponse>> response = adminFoodController.searchFoodItems("dosa");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Dosa", response.getBody().getFirst().getName());
        verify(adminFoodService, times(1)).searchFoodItems("dosa");
    }

    @Test
    void searchFoodItems_no_match() {
        when(adminFoodService.searchFoodItems("pizza")).thenReturn(List.of());

        ResponseEntity<List<FoodItemResponse>> response = adminFoodController.searchFoodItems("pizza");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }



    // Test for the menu details


    @Test
    void createFoodMenu_success() {
        when(adminFoodService.createFoodMenu(foodMenuRequest)).thenReturn(menuResponse);

        ResponseEntity<AdminFoodMenuResponse> response = adminFoodController.createFoodMenu(foodMenuRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("BREAKFAST", response.getBody().getCategory());
        assertEquals(MenuDay.MONDAY, response.getBody().getMenuDay());
        verify(adminFoodService, times(1)).createFoodMenu(foodMenuRequest);
    }

    @Test
    void createFoodMenu_failed() {
        when(adminFoodService.createFoodMenu(foodMenuRequest))
                .thenThrow(new RuntimeException("Food item not found: 1"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                adminFoodController.createFoodMenu(foodMenuRequest));

        assertEquals("Food item not found: 1", exception.getMessage());
        verify(adminFoodService, times(1)).createFoodMenu(foodMenuRequest);
    }

    @Test
    void getAllFoodMenus_success() {
        when(adminFoodService.getAllFoodMenus()).thenReturn(List.of(menuResponse));

        ResponseEntity<List<AdminFoodMenuResponse>> response = adminFoodController.getAllFoodMenus();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("BREAKFAST", response.getBody().getFirst().getCategory());
        verify(adminFoodService, times(1)).getAllFoodMenus();
    }

    @Test
    void getAllFoodMenus_empty() {
        when(adminFoodService.getAllFoodMenus()).thenReturn(List.of());

        ResponseEntity<List<AdminFoodMenuResponse>> response = adminFoodController.getAllFoodMenus();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getFoodMenuById_success() {
        when(adminFoodService.getFoodMenuById(1L)).thenReturn(menuResponse);

        ResponseEntity<AdminFoodMenuResponse> response = adminFoodController.getFoodMenuById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        assertEquals(MenuDay.MONDAY, response.getBody().getMenuDay());
        verify(adminFoodService, times(1)).getFoodMenuById(1L);
    }

    @Test
    void getFoodMenuById_failed() {
        when(adminFoodService.getFoodMenuById(99L))
                .thenThrow(new RuntimeException("Menu not found with id: 99"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                adminFoodController.getFoodMenuById(99L));

        assertEquals("Menu not found with id: 99", exception.getMessage());
        verify(adminFoodService, times(1)).getFoodMenuById(99L);
    }

    @Test
    void getMenusForDay_success() {
        when(adminFoodService.getMenusForDay(MenuDay.MONDAY)).thenReturn(List.of(menuResponse));

        ResponseEntity<List<AdminFoodMenuResponse>> response = adminFoodController.getMenusForDay(MenuDay.MONDAY);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(MenuDay.MONDAY, response.getBody().getFirst().getMenuDay());
        verify(adminFoodService, times(1)).getMenusForDay(MenuDay.MONDAY);
    }

    @Test
    void getMenusForDay_empty() {
        when(adminFoodService.getMenusForDay(MenuDay.SUNDAY)).thenReturn(List.of());

        ResponseEntity<List<AdminFoodMenuResponse>> response = adminFoodController.getMenusForDay(MenuDay.SUNDAY);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void updateFoodMenu_success() {
        AdminFoodMenuResponse updated = new AdminFoodMenuResponse(1L, "LUNCH", MenuDay.TUESDAY, List.of(), LocalDateTime.now());
        when(adminFoodService.updateFoodMenu(eq(1L), any(FoodMenuRequest.class))).thenReturn(updated);

        ResponseEntity<AdminFoodMenuResponse> response = adminFoodController.updateFoodMenu(1L, foodMenuRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("LUNCH", response.getBody().getCategory());
        assertEquals(MenuDay.TUESDAY, response.getBody().getMenuDay());
        verify(adminFoodService, times(1)).updateFoodMenu(eq(1L), any(FoodMenuRequest.class));
    }

    @Test
    void updateFoodMenu_failed() {
        when(adminFoodService.updateFoodMenu(eq(99L), any(FoodMenuRequest.class)))
                .thenThrow(new RuntimeException("Menu not found with id: 99"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                adminFoodController.updateFoodMenu(99L, foodMenuRequest));

        assertEquals("Menu not found with id: 99", exception.getMessage());
        verify(adminFoodService, times(1)).updateFoodMenu(eq(99L), any(FoodMenuRequest.class));
    }

    @Test
    void deleteFoodMenu_success() {
        doNothing().when(adminFoodService).deleteFoodMenu(1L);

        ResponseEntity<Void> response = adminFoodController.deleteFoodMenu(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(adminFoodService, times(1)).deleteFoodMenu(1L);
    }

    @Test
    void deleteFoodMenu_failed() {
        doThrow(new RuntimeException("Menu not found with id: 99"))
                .when(adminFoodService).deleteFoodMenu(99L);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                adminFoodController.deleteFoodMenu(99L));

        assertEquals("Menu not found with id: 99", exception.getMessage());
        verify(adminFoodService, times(1)).deleteFoodMenu(99L);
    }

    @Test
    void addFoodItemToMenu_success() {
        when(adminFoodService.addFoodItemToMenu(1L, 1L)).thenReturn(menuResponse);

        ResponseEntity<AdminFoodMenuResponse> response = adminFoodController.addFoodItemToMenu(1L, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getItems().size());
        assertEquals("Dosa", response.getBody().getItems().getFirst().getFoodItemName());
        verify(adminFoodService, times(1)).addFoodItemToMenu(1L, 1L);
    }

    @Test
    void addFoodItemToMenu_failed_menuNotFound() {
        when(adminFoodService.addFoodItemToMenu(99L, 1L))
                .thenThrow(new RuntimeException("Menu not found"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                adminFoodController.addFoodItemToMenu(99L, 1L));

        assertEquals("Menu not found", exception.getMessage());
        verify(adminFoodService, times(1)).addFoodItemToMenu(99L, 1L);
    }

    @Test
    void removeFoodItemFromMenu_success() {
        AdminFoodMenuResponse emptyMenu = new AdminFoodMenuResponse(1L, "BREAKFAST", MenuDay.MONDAY, List.of(), LocalDateTime.now());
        when(adminFoodService.removeFoodItemFromMenu(1L, 1L)).thenReturn(emptyMenu);

        ResponseEntity<AdminFoodMenuResponse> response = adminFoodController.removeFoodItemFromMenu(1L, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getItems().isEmpty());
        verify(adminFoodService, times(1)).removeFoodItemFromMenu(1L, 1L);
    }

    @Test
    void removeFoodItemFromMenu_failed_menuNotFound() {
        when(adminFoodService.removeFoodItemFromMenu(99L, 1L))
                .thenThrow(new RuntimeException("Menu not found"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                adminFoodController.removeFoodItemFromMenu(99L, 1L));

        assertEquals("Menu not found", exception.getMessage());
        verify(adminFoodService, times(1)).removeFoodItemFromMenu(99L, 1L);
    }
}
