package food.controller;

import net.breezeware.Spring_Boot_Cafeteria.food.controller.CustomerFoodController;
import net.breezeware.Spring_Boot_Cafeteria.food.dto.CustomerFoodItemResponse;
import net.breezeware.Spring_Boot_Cafeteria.food.dto.CustomerFoodMenuResponse;
import net.breezeware.Spring_Boot_Cafeteria.food.enumeration.MenuDay;
import net.breezeware.Spring_Boot_Cafeteria.food.service.CustomerFoodService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerFoodControllerTest {

    @Mock
    private CustomerFoodService customerFoodService;

    @InjectMocks
    private CustomerFoodController customerFoodController;

    private CustomerFoodItemResponse itemResponse;
    private CustomerFoodMenuResponse menuResponse;

    @BeforeEach
    void setUp() {
        itemResponse = new CustomerFoodItemResponse("Dosa", 50.0, 100, "BREAKFAST", "Crispy dosa", true);
        menuResponse = new CustomerFoodMenuResponse("BREAKFAST", MenuDay.MONDAY, List.of(itemResponse), LocalDateTime.now());
    }

    // ═══════════════════════════════════════════════════════
    // getMenusForDay
    // ═══════════════════════════════════════════════════════

    @Test
    void getMenusForDay_success() {
        when(customerFoodService.getMenusForDay(MenuDay.MONDAY)).thenReturn(List.of(menuResponse));

        ResponseEntity<List<CustomerFoodMenuResponse>> response = customerFoodController.getMenusForDay(MenuDay.MONDAY);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(MenuDay.MONDAY, response.getBody().getFirst().getMenuDay());
        verify(customerFoodService, times(1)).getMenusForDay(MenuDay.MONDAY);
    }

    @Test
    void getMenusForDay_empty() {
        when(customerFoodService.getMenusForDay(MenuDay.SUNDAY)).thenReturn(List.of());

        ResponseEntity<List<CustomerFoodMenuResponse>> response = customerFoodController.getMenusForDay(MenuDay.SUNDAY);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    // ═══════════════════════════════════════════════════════
    // getAllAvailableMenus
    // ═══════════════════════════════════════════════════════

    @Test
    void getAllAvailableMenus_success() {
        when(customerFoodService.getAllAvailableMenus()).thenReturn(List.of(menuResponse));

        ResponseEntity<List<CustomerFoodMenuResponse>> response = customerFoodController.getAllAvailableMenus();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("BREAKFAST", response.getBody().getFirst().getCategory());
        verify(customerFoodService, times(1)).getAllAvailableMenus();
    }

    @Test
    void getAllAvailableMenus_empty() {
        when(customerFoodService.getAllAvailableMenus()).thenReturn(List.of());

        ResponseEntity<List<CustomerFoodMenuResponse>> response = customerFoodController.getAllAvailableMenus();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    // ═══════════════════════════════════════════════════════
    // getAvailableFoodItems
    // ═══════════════════════════════════════════════════════

    @Test
    void getAvailableFoodItems_success() {
        when(customerFoodService.getAvailableFoodItems()).thenReturn(List.of(itemResponse));

        ResponseEntity<List<CustomerFoodItemResponse>> response = customerFoodController.getAvailableFoodItems();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Dosa", response.getBody().getFirst().getName());
        assertTrue(response.getBody().getFirst().isAvailable());
        verify(customerFoodService, times(1)).getAvailableFoodItems();
    }

    @Test
    void getAvailableFoodItems_empty() {
        when(customerFoodService.getAvailableFoodItems()).thenReturn(List.of());

        ResponseEntity<List<CustomerFoodItemResponse>> response = customerFoodController.getAvailableFoodItems();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    // ═══════════════════════════════════════════════════════
    // getFoodItemsByCategory
    // ═══════════════════════════════════════════════════════

    @Test
    void getFoodItemsByCategory_success() {
        when(customerFoodService.getFoodItemsByCategory("BREAKFAST")).thenReturn(List.of(itemResponse));

        ResponseEntity<List<CustomerFoodItemResponse>> response = customerFoodController.getFoodItemsByCategory("BREAKFAST");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("BREAKFAST", response.getBody().getFirst().getCategory());
        verify(customerFoodService, times(1)).getFoodItemsByCategory("BREAKFAST");
    }

    @Test
    void getFoodItemsByCategory_empty() {
        when(customerFoodService.getFoodItemsByCategory("DINNER")).thenReturn(List.of());

        ResponseEntity<List<CustomerFoodItemResponse>> response = customerFoodController.getFoodItemsByCategory("DINNER");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    // ═══════════════════════════════════════════════════════
    // searchFoodItems
    // ═══════════════════════════════════════════════════════

    @Test
    void searchFoodItems_success() {
        when(customerFoodService.searchFoodItems("dosa")).thenReturn(List.of(itemResponse));

        ResponseEntity<List<CustomerFoodItemResponse>> response = customerFoodController.searchFoodItems("dosa");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Dosa", response.getBody().getFirst().getName());
        verify(customerFoodService, times(1)).searchFoodItems("dosa");
    }

    @Test
    void searchFoodItems_no_match() {
        when(customerFoodService.searchFoodItems("pizza")).thenReturn(List.of());

        ResponseEntity<List<CustomerFoodItemResponse>> response = customerFoodController.searchFoodItems("pizza");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }
}
