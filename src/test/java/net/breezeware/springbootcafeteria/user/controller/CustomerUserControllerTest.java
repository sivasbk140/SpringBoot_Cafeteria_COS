package net.breezeware.springbootcafeteria.user.controller;

import net.breezeware.springbootcafeteria.user.dto.UserLoginRequestDto;
import net.breezeware.springbootcafeteria.user.dto.UserRequestDto;
import net.breezeware.springbootcafeteria.user.dto.UserResponseDto;
import net.breezeware.springbootcafeteria.user.enumeration.Role;
import net.breezeware.springbootcafeteria.user.service.CustomerUserService;
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
public class CustomerUserControllerTest {

    @Mock
    private CustomerUserService customerUserService;

    @InjectMocks
    private CustomerUserController customerUserController;

    @Test
    void register_success() {

        // Arrange
        UserRequestDto requestDto =
                new UserRequestDto("Sikar", "sikar@gmail.com", "sikar@123", Role.CUSTOMER);

        UserResponseDto responseDto =
                new UserResponseDto(1L, "Sikar", "sikar@gmail.com", Role.CUSTOMER);

        when(customerUserService.registerUser(requestDto))
                .thenReturn(responseDto);

        // Act
        ResponseEntity<UserResponseDto> response =
                customerUserController.registerUser(requestDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Sikar", response.getBody().getName());
    }

    @Test
    public void register_failed() {
        UserRequestDto requestDto =
                new UserRequestDto("Sikar", "sikar@gmail.com", "sikar@123", Role.CUSTOMER);
        when(customerUserService.registerUser(requestDto))
                .thenThrow(new RuntimeException("Email already exist"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            customerUserController.registerUser(requestDto);
        });

        assertEquals("Email already exist", exception.getMessage());
        verify(customerUserService, times(1)).registerUser(requestDto);
    }

    @Test
    public void login_successful() {
        UserLoginRequestDto loginDto =
                new UserLoginRequestDto("sikar@gmail.com", "sikar@123");

        UserResponseDto responseDto =
                new UserResponseDto(1L, "Sikar", "sikar@gmail.com", Role.CUSTOMER);

        when(customerUserService.login(loginDto))
                .thenReturn(responseDto);

        ResponseEntity<UserResponseDto> response =
                customerUserController.login(loginDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Sikar", response.getBody().getName());
        assertEquals("sikar@gmail.com", response.getBody().getEmail());
        assertEquals(Role.CUSTOMER, response.getBody().getRole());

        verify(customerUserService, times(1)).login(loginDto);
    }

    @Test
    public void login_failed() {
        UserLoginRequestDto loginDto =
                new UserLoginRequestDto("sikar@gmail.com", "sikar@123");

        when(customerUserService.login(loginDto))
                .thenThrow(new RuntimeException("Login Failed password or mail is wrong"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            customerUserController.login(loginDto);
        });

        assertEquals("Login Failed password or mail is wrong", exception.getMessage());

        verify(customerUserService, times(1)).login(loginDto);
    }

}
