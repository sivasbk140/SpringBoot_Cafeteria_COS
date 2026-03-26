package user.service;

import net.breezeware.Spring_Boot_Cafeteria.user.dto.UserRequestDto;
import net.breezeware.Spring_Boot_Cafeteria.user.dto.UserResponseDto;
import net.breezeware.Spring_Boot_Cafeteria.user.entity.User;
import net.breezeware.Spring_Boot_Cafeteria.user.enumeration.Role;
import net.breezeware.Spring_Boot_Cafeteria.user.repo.UserRepository;
import net.breezeware.Spring_Boot_Cafeteria.user.service.AdminUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminUserService adminUserService;

    @Test
    void register_success() {

        // Arrange
        UserRequestDto requestDto =
                new UserRequestDto("Sikar", "sikar@gmail.com", "sikar@123", Role.ADMIN);

        User userEntity =
                new User("Sikar", "sikar@gmail.com", "sikar@123", Role.ADMIN);

        when(userRepository.save(any(User.class)))
                .thenReturn(userEntity);

        // Act
        UserResponseDto result = adminUserService.registerUser(requestDto);

        // Assert
        assertNotNull(result);
        assertEquals("Sikar", result.getName());
    }

    @Test
    void register_failed_emailAlreadyExists() {

        // Arrange
        UserRequestDto requestDto =
                new UserRequestDto("Sikar", "sikar@gmail.com", "sikar@123", Role.ADMIN);

        when(userRepository.save(any(User.class)))
                .thenThrow(new RuntimeException("Email Already Registered"));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adminUserService.registerUser(requestDto);
        });

        assertEquals("Email Already Registered", exception.getMessage());

        // Verify
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void get_all_users() {

        // Arrange
        List<User> userList = new ArrayList<>();
//
        userList.add(new User("Sikar", "sikar@gmail.com", "sikar@123", Role.ADMIN));
        userList.add(new User("Sik", "sik@gmail.com", "sik@123", Role.ADMIN));

        when(userRepository.findAll()).thenReturn(userList);

        // Act
        List<UserResponseDto> responseList = adminUserService.getAllUsers();

        // Assert
        assertEquals(2, responseList.size());
        assertEquals("Sik", responseList.get(1).getName());
    }

    @Test
    void get_all_users_empty_list() {

        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // Act

        List<UserResponseDto> responseList = adminUserService.getAllUsers();

        // Assert
        assertNotNull(responseList);
        assertTrue(responseList.isEmpty());

        verify(userRepository, times(1)).findAll();
    }


    @Test
    void get_by_id_success() {


        //  User user = new User(null,null,null,null);

        User user = new User("Sikar", "sikar@gmail.com", "sikar@123", Role.ADMIN);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));


        UserResponseDto response = adminUserService.getUserById(1L);


        assertNotNull(response);
        assertEquals("Sikar", response.getName());
        assertEquals("sikar@gmail.com", response.getEmail());

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void get_by_id_failed() {

        // Arrange
        when(userRepository.findById(1L))
                .thenThrow(new RuntimeException("User Not Found for id"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            adminUserService.getUserById(1L);
        });

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void login_success() {

        // Arrange
        UserRequestDto request =
                new UserRequestDto("Sikar", "sikar@gmail.com", "sikar@123", Role.ADMIN);

        User user =
                new User("Sikar", "sikar@gmail.com", "sikar@123", Role.ADMIN);

        when(userRepository.findByEmail("sikar@gmail.com"))
                .thenReturn(Optional.of(user));

        // Act
        UserResponseDto response = adminUserService.login(request);

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
                new UserRequestDto("Sikar", "sikar@gmail.com", "sikar@123", Role.ADMIN);

        when(userRepository.findByEmail("sikar@gmail.com"))
                .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adminUserService.login(request);
        });

        assertEquals("Invalid email or password", exception.getMessage());

        verify(userRepository).findByEmail("sikar@gmail.com");
    }
}