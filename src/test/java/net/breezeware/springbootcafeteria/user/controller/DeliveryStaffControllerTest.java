package net.breezeware.springbootcafeteria.user.controller;

import net.breezeware.springbootcafeteria.user.dto.UserLoginRequestDto;
import net.breezeware.springbootcafeteria.user.dto.UserRequestDto;
import net.breezeware.springbootcafeteria.user.dto.UserResponseDto;
import net.breezeware.springbootcafeteria.user.enumeration.Role;
import net.breezeware.springbootcafeteria.user.service.DeliveryStaffService;
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
    public void register_failed() {
        UserRequestDto requestDto =
                new UserRequestDto("Sikar", "sikar@gmail.com", "sikar@123", Role.DELIVERY_STAFF);
        when(deliveryStaffService.registerUser(requestDto))
                .thenThrow(new RuntimeException("Email already exist"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            deliveryStaffUserController.registerUser(requestDto);
        });

        assertEquals("Email already exist", exception.getMessage());
        verify(deliveryStaffService, times(1)).registerUser(requestDto);
    }

    @Test
    public void login_successful() {
        UserLoginRequestDto loginDto =
                new UserLoginRequestDto("sikar@gmail.com", "sikar@123");

        UserResponseDto responseDto =
                new UserResponseDto(1L, "Sikar", "sikar@gmail.com", Role.DELIVERY_STAFF);

        when(deliveryStaffService.login(loginDto))
                .thenReturn(responseDto);

        ResponseEntity<UserResponseDto> response =
                deliveryStaffUserController.login(loginDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Sikar", response.getBody().getName());
        assertEquals("sikar@gmail.com", response.getBody().getEmail());
        assertEquals(Role.DELIVERY_STAFF, response.getBody().getRole());

        verify(deliveryStaffService, times(1)).login(loginDto);
    }

    @Test
    public void login_failed() {
        UserLoginRequestDto loginDto =
                new UserLoginRequestDto("sikar@gmail.com", "sikar@123");

        when(deliveryStaffService.login(loginDto))
                .thenThrow(new RuntimeException("Login Failed password or mail is wrong"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            deliveryStaffUserController.login(loginDto);
        });

        assertEquals("Login Failed password or mail is wrong", exception.getMessage());

        verify(deliveryStaffService, times(1)).login(loginDto);
    }
}
