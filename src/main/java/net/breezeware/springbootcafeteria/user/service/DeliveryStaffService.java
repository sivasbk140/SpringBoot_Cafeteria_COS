package net.breezeware.springbootcafeteria.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.breezeware.springbootcafeteria.exception.AppCustomException;
import net.breezeware.springbootcafeteria.user.dto.UserLoginRequestDto;
import net.breezeware.springbootcafeteria.user.dto.UserRequestDto;
import net.breezeware.springbootcafeteria.user.dto.UserResponseDto;
import net.breezeware.springbootcafeteria.user.entity.User;
import net.breezeware.springbootcafeteria.user.enumeration.Role;
import net.breezeware.springbootcafeteria.user.dao.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class responsible for managing delivery staff user authentication
 * and registration in the cafeteria system.
 *
 * <p>This service handles delivery staff login and registration.
 * New users are automatically assigned the DELIVERY_STAFF role upon registration.</p>
 *
 * @author Siva
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryStaffService {

    private final UserRepository userRepository;

    /**
     * Authenticates a delivery staff user using email and password.
     *
     * @param request the login request containing email and password
     * @return UserResponseDto containing authenticated user details
     *
     * @throws AppCustomException if email is not found or password does not match
     *
     * @apiNote Returns HTTP 401 for invalid credentials.
     */
    public UserResponseDto login(UserLoginRequestDto request) {
        log.info("Delivery Staff logging In service layer");
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppCustomException("Invalid email or password", HttpStatus.UNAUTHORIZED));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new AppCustomException("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }

        return mapToResponse(user);
    }

    /**
     * Registers a new delivery staff user in the system.
     *
     * @param request the registration request containing name, email, and password
     * @return UserResponseDto containing the saved delivery staff user details
     *
     * @throws AppCustomException if the email is already registered
     *
     * @implSpec New users are persisted with the DELIVERY_STAFF role automatically assigned.
     */
    public UserResponseDto registerUser(UserRequestDto request) {
        log.info("Registering new Delivery Staff service layer");
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppCustomException("Email already registered", HttpStatus.CONFLICT);
        }

        User user = new User(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                Role.DELIVERY_STAFF
        );

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    /**
     * Maps a User entity to a UserResponseDto.
     *
     * @param user the User entity to map
     * @return UserResponseDto containing id, name, email, and role
     *
     * @implNote Internal helper method for DTO conversion.
     */
    private UserResponseDto mapToResponse(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
