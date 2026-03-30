package user.controller;

import net.breezeware.Spring_Boot_Cafeteria.user.controller.CustomerUserController;
import net.breezeware.Spring_Boot_Cafeteria.user.dto.UserRequestDto;
import net.breezeware.Spring_Boot_Cafeteria.user.dto.UserResponseDto;
import net.breezeware.Spring_Boot_Cafeteria.user.enumeration.Role;
import net.breezeware.Spring_Boot_Cafeteria.user.service.CustomerUserService;
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

    public void register_failed()
    {
        UserRequestDto requestDto =
                new UserRequestDto("Sikar", "sikar@gmail.com", "sikar@123", Role.CUSTOMER);
        when(customerUserService.registerUser(requestDto))
                .thenThrow(new RuntimeException("Email already exist"));


        RuntimeException exception = assertThrows(RuntimeException.class, () -> {   customerUserController.registerUser(requestDto);
        });



        assertEquals("Email already exist",exception.getMessage());
        verify(customerUserService, times(1)).registerUser(requestDto);

    }

    @Test
    public void login_successful()
    {
        UserRequestDto requestDto =
                new UserRequestDto("Sikar", "sikar@gmail.com", "sikar@123", Role.CUSTOMER);

        UserResponseDto responseDto =
                new UserResponseDto(1L, "Sikar", "sikar@gmail.com", Role.CUSTOMER);


        when(customerUserService.login(requestDto))
                .thenReturn(responseDto);

        ResponseEntity<UserResponseDto> response =
                customerUserController.login(requestDto);

        // Assert

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(1L, response.getBody().getId());
        assertEquals("Sikar", response.getBody().getName());
        assertEquals("sikar@gmail.com", response.getBody().getEmail());
        assertEquals(Role.CUSTOMER, response.getBody().getRole());

        verify(customerUserService, times(1)).login(requestDto);
    }

    @Test
    public  void  login_failed()
    {
        UserRequestDto requestDto =
                new UserRequestDto("Sikar", "sikar@gmail.com", "sikar@123", Role.CUSTOMER);

        when(customerUserService.login(requestDto))
                .thenThrow(new RuntimeException("Login Failed password or mail is wrong"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            customerUserController.login(requestDto);
        });


        assertEquals("Login Failed password or mail is wrong", exception.getMessage());


        verify(customerUserService, times(1)).login(requestDto);


    }

}
