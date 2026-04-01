package net.breezeware.SpringBootCafeteria.order.service;

import net.breezeware.SpringBootCafeteria.food.entity.FoodItem;
import net.breezeware.SpringBootCafeteria.food.repo.FoodItemRepository;
import net.breezeware.SpringBootCafeteria.order.dto.CartItemDto;
import net.breezeware.SpringBootCafeteria.order.dto.OrderDetailDto;
import net.breezeware.SpringBootCafeteria.order.entity.Order;
import net.breezeware.SpringBootCafeteria.order.enumeration.OrderStatus;
import net.breezeware.SpringBootCafeteria.order.repo.OrderDeliveryMapRepository;
import net.breezeware.SpringBootCafeteria.order.repo.OrderRepository;
import net.breezeware.SpringBootCafeteria.user.entity.User;
import net.breezeware.SpringBootCafeteria.user.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerOrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderDeliveryMapRepository orderDeliveryMapRepository;
    @Mock
    private UserRepository userRepository;

   @Mock
    private FoodItemRepository foodItemRepository;

    @InjectMocks
    private CustomerOrderService customerOrderService;



    @Test
    void  addToCart_shouldAddItemsToCart()
    {



        // Arrange
        Long userId = 1L;
        String foodName = "Pizza";

        FoodItem foodItem = new FoodItem( "Pizza",250.0 ,10, "Non_Veg");

        when(foodItemRepository.findByNameIgnoreCase(foodName))
                .thenReturn(Optional.of(foodItem));

        // Act
        List<CartItemDto> result = customerOrderService.addToCart(userId, foodName, 2);

        // Assert
        assertEquals(1, result.size());

        CartItemDto item = result.get(0);
        assertEquals("Pizza", item.getFoodItemName());
        assertEquals(2L, item.getQuantity());
        assertEquals(500.0, item.getTotalPrice());

        verify(foodItemRepository).findByNameIgnoreCase(foodName);
    }


    @Test
    void addToCart_NegativeCase_InsufficientStock()
    {
        Long userId = 1L;
        String foodName = "Pizza";

        FoodItem foodItem = new FoodItem( "Pizza",250.0 ,1, "Non_Veg");

        when(foodItemRepository.findByNameIgnoreCase(foodName))
                .thenReturn(Optional.of(foodItem));

        // Act


        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                customerOrderService.addToCart(1L,foodName,4));

    //Assert
      assertEquals("Insufficient stock for item: Pizza",exception.getMessage());

        verify(foodItemRepository).findByNameIgnoreCase(foodName);
    }



    @Test
    void viewCart_shouldReturnMultipleItems_whenMultipleItemsAdded() {

        // Arrange
        Long userId = 1L;

        FoodItem pizza = new FoodItem("Pizza", 200.0, 10, "Veg");
        FoodItem burger = new FoodItem("Burger", 150.0, 10, "Non_Veg");

        when(foodItemRepository.findByNameIgnoreCase("Pizza"))
                .thenReturn(Optional.of(pizza));

        when(foodItemRepository.findByNameIgnoreCase("Burger"))
                .thenReturn(Optional.of(burger));

        // Act
        customerOrderService.addToCart(userId, "Pizza", 2);   // total = 400
        customerOrderService.addToCart(userId, "Burger", 3);  // total = 450

        List<CartItemDto> result = customerOrderService.viewCart(userId);


        assertEquals(2, result.size());

        // Validate Pizza
        CartItemDto item1 = result.get(0);
        CartItemDto item2 = result.get(1);

        // Since order may vary, safer check:
        boolean pizzaFound = result.stream().anyMatch(item ->
                item.getFoodItemName().equalsIgnoreCase("Pizza") &&
                        item.getQuantity() == 2L &&
                        item.getTotalPrice() == 400.0
        );

        boolean burgerFound = result.stream().anyMatch(item ->
                item.getFoodItemName().equalsIgnoreCase("Burger") &&
                        item.getQuantity() == 3L &&
                        item.getTotalPrice() == 450.0
        );



        verify(foodItemRepository).findByNameIgnoreCase("Pizza");
        verify(foodItemRepository).findByNameIgnoreCase("Burger");
    }

    @Test
    void viewCartShould_notReturnIfCartIsEmpty()
    {
        List<CartItemDto> result = customerOrderService.viewCart(99L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void removeItemFromCart()
    {
        Long userId = 1L;

        FoodItem pizza = new FoodItem("Pizza", 200.0, 10, "Veg"); FoodItem burger = new FoodItem("Burger", 150.0, 10, "Non_Veg");

        when(foodItemRepository.findByNameIgnoreCase("Pizza"))
                .thenReturn(Optional.of(pizza));

        when(foodItemRepository.findByNameIgnoreCase("Burger"))
                .thenReturn(Optional.of(burger));

        // Act
        customerOrderService.addToCart(userId, "Pizza", 2);   // total = 400
        customerOrderService.addToCart(userId, "Burger", 3);  // total = 450

        List<CartItemDto> result = customerOrderService.removeFromCart(1L,"Pizza");


        assertEquals(1, result.size());

    }
    @Test
    void removeFromCartShould_notRemoveItemIfCartIsEmpty()
    {
        List<CartItemDto> result = customerOrderService.removeFromCart(99L,"Pizza");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }



    @Test
    void getOrderDetail_shouldReturnOrder_whenUserOwnsOrder() {

        // Arrange
        Long orderId = 1L;
        Long userId = 101L;

        User user = new User();
        user.setId(userId);

        Order order = new Order();
        order.setId(orderId);
        order.setUser(user);
        order.setStatus(OrderStatus.PLACED_ORDER);
        order.setCreatedOn(new Date());

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        // Act
        OrderDetailDto result =
                customerOrderService.getOrderDetail(orderId, userId);

        // Assert
        assertNotNull(result);
        assertEquals(orderId.intValue(), result.getOrderId());

        verify(orderRepository).findById(orderId);
    }

    @Test
    void getOrderDetail_shouldNotReturnOrdersIfEmpty()
    {
        Long orderId = 1L;
        Long userId = 101L;
        User user = new User();
        user.setId(userId);

        Order order = new Order();
        order.setId(orderId);
        order.setUser(user);
        order.setStatus(OrderStatus.PLACED_ORDER);
        order.setCreatedOn(new Date());

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

    }

}

