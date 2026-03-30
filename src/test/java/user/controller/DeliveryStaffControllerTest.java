package user.controller;

import net.breezeware.Spring_Boot_Cafeteria.user.controller.CustomerUserController;
import net.breezeware.Spring_Boot_Cafeteria.user.controller.DeliveryStaffUserController;
import net.breezeware.Spring_Boot_Cafeteria.user.dto.UserRequestDto;
import net.breezeware.Spring_Boot_Cafeteria.user.dto.UserResponseDto;
import net.breezeware.Spring_Boot_Cafeteria.user.enumeration.Role;

import net.breezeware.Spring_Boot_Cafeteria.user.service.DeliveryStaffService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class DeliveryStaffControllerTest {


    @Mock
    private DeliveryStaffService deliveryStaffService;

    @InjectMocks
    private DeliveryStaffUserController deliveryStaffUserController;
    @Test
    void register_success() {

        // Arrange
        UserRequestDto requestDto =
                new UserRequestDto("Sikar", "sikar@gmail.com", "sikar@123", Role.DELIVERY_STAFF);

        UserResponseDto responseDto =
                new UserResponseDto(1L, "Sikar", "sikar@gmail.com", Role.DELIVERY_STAFF);

        when(deliveryStaffService.registerUser(requestDto))
                .thenReturn(responseDto);

        // Act
        ResponseEntity<UserResponseDto> response =
                deliveryStaffUserController.registerUser(requestDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Sikar", response.getBody().getName());
    }

    @Test

    public void register_failed()
    {
        UserRequestDto requestDto =
                new UserRequestDto("Sikar", "sikar@gmail.com", "sikar@123", Role.DELIVERY_STAFF);
        when(deliveryStaffService.registerUser(requestDto))
                .thenThrow(new RuntimeException("Email already exist"));


        RuntimeException exception = assertThrows(RuntimeException.class, () -> {  deliveryStaffUserController.registerUser(requestDto);
        });



        assertEquals("Email already exist",exception.getMessage());
        verify(deliveryStaffService, times(1)).registerUser(requestDto);

    }

    @Test
    public void login_successful()
    {
        UserRequestDto requestDto =
                new UserRequestDto("Sikar", "sikar@gmail.com", "sikar@123", Role.DELIVERY_STAFF);

        UserResponseDto responseDto =
                new UserResponseDto(1L, "Sikar", "sikar@gmail.com", Role.DELIVERY_STAFF);


        when(deliveryStaffService.login(requestDto))
                .thenReturn(responseDto);

        ResponseEntity<UserResponseDto> response =
                deliveryStaffUserController.login(requestDto);

        // Assert

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(1L, response.getBody().getId());
        assertEquals("Sikar", response.getBody().getName());
        assertEquals("sikar@gmail.com", response.getBody().getEmail());
        assertEquals(Role.DELIVERY_STAFF, response.getBody().getRole());

        verify(deliveryStaffService, times(1)).login(requestDto);
    }

    @Test
    public  void  login_failed()
    {
        UserRequestDto requestDto =
                new UserRequestDto("Sikar", "sikar@gmail.com", "sikar@123", Role.DELIVERY_STAFF);

        when(deliveryStaffService.login(requestDto))
                .thenThrow(new RuntimeException("Login Failed password or mail is wrong"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            deliveryStaffUserController.login(requestDto);
        });


        assertEquals("Login Failed password or mail is wrong", exception.getMessage());


        verify(deliveryStaffService, times(1)).login(requestDto);


    }
}
