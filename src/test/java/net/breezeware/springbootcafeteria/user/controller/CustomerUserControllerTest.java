package net.breezeware.springbootcafeteria.user.controller;

import net.breezeware.springbootcafeteria.user.dto.UserLoginRequest;
import net.breezeware.springbootcafeteria.user.dto.UserRequest;
import net.breezeware.springbootcafeteria.user.dto.UserResponse;
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
        UserRequest requestDto =
                new UserRequest("Sikar", "sikar@gmail.com", "sikar@123", Role.CUSTOMER);

        UserResponse responseDto =
                new UserResponse(1L, "Sikar", "sikar@gmail.com", Role.CUSTOMER);

        when(customerUserService.registerUser(requestDto))
                .thenReturn(responseDto);

        // Act
        ResponseEntity<UserResponse> response =
                customerUserController.registerUser(requestDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Sikar", response.getBody().getName());
    }

    @Test
    public void register_failed() {
        UserRequest requestDto =
                new UserRequest("Sikar", "sikar@gmail.com", "sikar@123", Role.CUSTOMER);
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
        UserLoginRequest loginDto =
                new UserLoginRequest("sikar@gmail.com", "sikar@123");

        UserResponse responseDto =
                new UserResponse(1L, "Sikar", "sikar@gmail.com", Role.CUSTOMER);

        when(customerUserService.login(loginDto))
                .thenReturn(responseDto);

        ResponseEntity<UserResponse> response =
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
        UserLoginRequest loginDto =
                new UserLoginRequest("sikar@gmail.com", "sikar@123");

        when(customerUserService.login(loginDto))
                .thenThrow(new RuntimeException("Login Failed password or mail is wrong"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            customerUserController.login(loginDto);
        });

        assertEquals("Login Failed password or mail is wrong", exception.getMessage());

        verify(customerUserService, times(1)).login(loginDto);
    }

}
