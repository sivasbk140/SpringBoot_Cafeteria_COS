package net.breezeware.SpringBootCafeteria.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.breezeware.SpringBootCafeteria.exception.AppCustomException;
import net.breezeware.SpringBootCafeteria.user.dto.UserLoginRequestDto;
import net.breezeware.SpringBootCafeteria.user.dto.UserRequestDto;
import net.breezeware.SpringBootCafeteria.user.dto.UserResponseDto;
import net.breezeware.SpringBootCafeteria.user.entity.User;
import net.breezeware.SpringBootCafeteria.user.enumeration.Role;
import net.breezeware.SpringBootCafeteria.user.repo.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class responsible for managing cafeteria staff user authentication
 * and registration in the cafeteria system.
 *
 * <p>This service handles staff login and registration.
 * New users are automatically assigned the STAFF role upon registration.</p>
 *
 * @author Siva
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StaffUserService {

    private final UserRepository userRepository;

    /**
     * Authenticates a cafeteria staff user using email and password.
     *
     * @param request the login request containing email and password
     * @return UserResponseDto containing authenticated user details
     *
     * @throws AppCustomException if email is not found or password does not match
     *
     * @apiNote Returns HTTP 401 for invalid credentials.
     */
    public UserResponseDto login(UserLoginRequestDto request) {
        log.info("staff logging In service layer");
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppCustomException("Invalid email or password", HttpStatus.UNAUTHORIZED));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new AppCustomException("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }

        return mapToResponse(user);
    }

    /**
     * Registers a new cafeteria staff user in the system.
     *
     * @param request the registration request containing name, email, and password
     * @return UserResponseDto containing the saved staff user details
     *
     * @throws AppCustomException if the email is already registered
     *
     * @implSpec New users are persisted with the STAFF role automatically assigned.
     */
    public UserResponseDto registerUser(UserRequestDto request) {
        log.info("Registering staff service layer");
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppCustomException("Email already registered", HttpStatus.CONFLICT);
        }

        User user = new User(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                Role.STAFF
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
