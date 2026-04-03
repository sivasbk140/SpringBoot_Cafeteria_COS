package net.breezeware.SpringBootCafeteria.user.controller;

import net.breezeware.SpringBootCafeteria.user.dto.UserLoginRequestDto;
import net.breezeware.SpringBootCafeteria.user.dto.UserRequestDto;
import net.breezeware.SpringBootCafeteria.user.dto.UserResponseDto;
import net.breezeware.SpringBootCafeteria.user.enumeration.Role;
import net.breezeware.SpringBootCafeteria.user.service.AdminUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserControllerTest {

    @Mock
    private AdminUserService adminUserService;

    @InjectMocks
    private AdminUserController adminUserController;

    @Test
    void register_success() {

        // Arrange
        UserRequestDto requestDto =
                new UserRequestDto("Sikar", "sikar@gmail.com", "sikar@123", Role.ADMIN);

        UserResponseDto responseDto =
                new UserResponseDto(1L, "Sikar", "sikar@gmail.com", Role.ADMIN);

        when(adminUserService.registerUser(requestDto))
                .thenReturn(responseDto);

        // Act
        ResponseEntity<UserResponseDto> response =
                adminUserController.registerUser(requestDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Sikar", response.getBody().getName());
    }

    @Test
    public void register_failed() {
        UserRequestDto requestDto =
                new UserRequestDto("Sikar", "sikar@gmail.com", "sikar@123", Role.ADMIN);
        when(adminUserService.registerUser(requestDto))
                .thenThrow(new RuntimeException("Email already exist"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adminUserController.registerUser(requestDto);
        });

        assertEquals("Email already exist", exception.getMessage());
        verify(adminUserService, times(1)).registerUser(requestDto);
    }

    @Test
    public void login_successful() {
        UserLoginRequestDto loginDto =
                new UserLoginRequestDto("sikar@gmail.com", "sikar@123");

        UserResponseDto responseDto =
                new UserResponseDto(1L, "Sikar", "sikar@gmail.com", Role.ADMIN);

        when(adminUserService.login(loginDto))
                .thenReturn(responseDto);

        ResponseEntity<UserResponseDto> response =
                adminUserController.login(loginDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Sikar", response.getBody().getName());
        assertEquals("sikar@gmail.com", response.getBody().getEmail());
        assertEquals(Role.ADMIN, response.getBody().getRole());

        verify(adminUserService, times(1)).login(loginDto);
    }

    @Test
    public void login_failed() {
        UserLoginRequestDto loginDto =
                new UserLoginRequestDto("sikar@gmail.com", "sikar@123");

        when(adminUserService.login(loginDto))
                .thenThrow(new RuntimeException("Login Failed password or mail is wrong"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adminUserController.login(loginDto);
        });

        assertEquals("Login Failed password or mail is wrong", exception.getMessage());

        verify(adminUserService, times(1)).login(loginDto);
    }

    @Test
    public void get_all_users_successful() {

        // Arrange
        List<UserResponseDto> responseDtoList = new ArrayList<>();
        responseDtoList.add(new UserResponseDto(1L, "Sikar", "sikar@gmail.com", Role.ADMIN));
        responseDtoList.add(new UserResponseDto(2L, "Sika", "sika@gmail.com", Role.ADMIN));

        when(adminUserService.getAllUsers()).thenReturn(responseDtoList);

        // Act
        ResponseEntity<List<UserResponseDto>> response =
                adminUserController.getAllUsers();

        List<UserResponseDto> responseList = response.getBody();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, responseList.size());
        assertEquals("Sika", responseList.get(1).getName());

        verify(adminUserService, times(1)).getAllUsers();
    }

    @Test
    public void get_all_users_failure() {

        // Arrange
        when(adminUserService.getAllUsers())
                .thenThrow(new RuntimeException("Empty List"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adminUserController.getAllUsers();
        });

        assertEquals("Empty List", exception.getMessage());

        // Verify
        verify(adminUserService, times(1)).getAllUsers();
    }

    @Test
    public void get_user_by_id_success() {
        UserResponseDto responseDto =
                new UserResponseDto(1L, "Sikar", "sikar@gmail.com", Role.ADMIN);

        when(adminUserService.getUserById(1L))
                .thenReturn(responseDto);

        ResponseEntity<UserResponseDto> response =
                adminUserController.getUserById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Sikar", response.getBody().getName());

        verify(adminUserService, times(1)).getUserById(1L);
    }

    @Test
    public void get_by_id_failure() {

        when(adminUserService.getUserById(1L))
                .thenThrow(new RuntimeException("No user Found"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adminUserController.getUserById(1L);
        });

        assertEquals("No user Found", exception.getMessage());

        verify(adminUserService, times(1)).getUserById(1L);
    }
}
