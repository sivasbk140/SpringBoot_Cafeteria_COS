package net.breezeware.SpringBootCafeteria.user.service;

import net.breezeware.SpringBootCafeteria.user.dto.UserRequestDto;
import net.breezeware.SpringBootCafeteria.user.dto.UserResponseDto;
import net.breezeware.SpringBootCafeteria.user.entity.User;
import net.breezeware.SpringBootCafeteria.user.enumeration.Role;
import net.breezeware.SpringBootCafeteria.user.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomerUserService customerUserService;

    @Test
    void register_success() {

        // Arrange
        UserRequestDto requestDto =
                new UserRequestDto("Sikar", "sikar@gmail.com", "sikar@123", Role.CUSTOMER);

        User userEntity =
                new User("Sikar", "sikar@gmail.com", "sikar@123", Role.CUSTOMER);

        when(userRepository.save(any(User.class)))
                .thenReturn(userEntity);

        // Act
        UserResponseDto result = customerUserService.registerUser(requestDto);

        // Assert
        assertNotNull(result);
        assertEquals("Sikar", result.getName());
    }

    @Test
    void register_failed_emailAlreadyExists() {

        // Arrange
        UserRequestDto requestDto =
                new UserRequestDto("Sikar", "sikar@gmail.com", "sikar@123", Role.CUSTOMER);

        when(userRepository.save(any(User.class)))
                .thenThrow(new RuntimeException("Email Already Registered"));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            customerUserService.registerUser(requestDto);
        });

        assertEquals("Email Already Registered", exception.getMessage());

        // Verify
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void login_success() {

        // Arrange
        UserRequestDto request =
                new UserRequestDto("Sikar", "sikar@gmail.com", "sikar@123", Role.CUSTOMER);

        User user =
                new User("Sikar", "sikar@gmail.com", "sikar@123", Role.CUSTOMER);

        when(userRepository.findByEmail("sikar@gmail.com"))
                .thenReturn(Optional.of(user));

        // Act
        UserResponseDto response = customerUserService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("Sikar", response.getName());
        assertEquals("sikar@gmail.com", response.getEmail());

        verify(userRepository).findByEmail("sikar@gmail.com");
    }



    @Test
    void login_failed_user_not_found() {

        // Arrange
        UserRequestDto request =
                new UserRequestDto("Sikar", "sikar@gmail.com", "sikar@123", Role.CUSTOMER);

        when(userRepository.findByEmail("sikar@gmail.com"))
                .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            customerUserService.login(request);
        });

        assertEquals("Invalid email or password", exception.getMessage());

        verify(userRepository).findByEmail("sikar@gmail.com");
    }
}
