package net.breezeware.springbootcafeteria.user.service;

import net.breezeware.springbootcafeteria.user.dto.UserLoginRequest;
import net.breezeware.springbootcafeteria.user.dto.UserRequest;
import net.breezeware.springbootcafeteria.user.dto.UserResponse;
import net.breezeware.springbootcafeteria.user.entity.User;
import net.breezeware.springbootcafeteria.user.enumeration.Role;
import net.breezeware.springbootcafeteria.user.dao.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StaffUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private StaffUserService staffUserService;

    @Test
    void register_success() {

        // Arrange
        UserRequest requestDto =
                new UserRequest("Sikar", "sikar@gmail.com", "sikar@123", Role.STAFF);

        User userEntity =
                new User("Sikar", "sikar@gmail.com", "sikar@123", Role.STAFF);

        when(userRepository.save(any(User.class)))
                .thenReturn(userEntity);

        // Act
        UserResponse result = staffUserService.registerUser(requestDto);

        // Assert
        assertNotNull(result);
        assertEquals("Sikar", result.getName());
    }

    @Test
    void register_failed_emailAlreadyExists() {

        // Arrange
        UserRequest requestDto =
                new UserRequest("Sikar", "sikar@gmail.com", "sikar@123", Role.STAFF);

        when(userRepository.save(any(User.class)))
                .thenThrow(new RuntimeException("Email Already Registered"));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            staffUserService.registerUser(requestDto);
        });

        assertEquals("Email Already Registered", exception.getMessage());

        // Verify
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void login_success() {

        // Arrange
        UserLoginRequest request =
                new UserLoginRequest("sikar@gmail.com", "sikar@123");

        User user =
                new User("Sikar", "sikar@gmail.com", "sikar@123", Role.STAFF);

        when(userRepository.findByEmail("sikar@gmail.com"))
                .thenReturn(Optional.of(user));

        // Act
        UserResponse response = staffUserService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("Sikar", response.getName());
        assertEquals("sikar@gmail.com", response.getEmail());

        verify(userRepository).findByEmail("sikar@gmail.com");
    }

    @Test
    void login_failed_user_not_found() {

        // Arrange
        UserLoginRequest request =
                new UserLoginRequest("sikar@gmail.com", "sikar@123");

        when(userRepository.findByEmail("sikar@gmail.com"))
                .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            staffUserService.login(request);
        });

        assertEquals("Invalid email or password", exception.getMessage());

        verify(userRepository).findByEmail("sikar@gmail.com");
    }

}
