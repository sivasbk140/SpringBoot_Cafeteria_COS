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

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class responsible for managing admin user authentication
 * and user management operations in the cafeteria system.
 *
 * <p>This service handles admin login, admin registration, and
 * retrieval of user records across all roles in the system.</p>
 *
 * @author Siva
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserService {

    private final UserRepository userRepository;

    /**
     * Authenticates an admin user using email and password.
     *
     * @param request the login request containing email and password
     * @return UserResponseDto containing authenticated user details
     *
     * @throws AppCustomException if email is not found or password does not match
     *
     * @apiNote Returns HTTP 401 for invalid credentials.
     */
    public UserResponseDto login(UserLoginRequestDto request) {
        log.info("Admin logging In service layer");
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppCustomException("Invalid email or password", HttpStatus.UNAUTHORIZED));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new AppCustomException("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }

        return mapToResponse(user);
    }

    /**
     * Registers a new admin user in the system.
     *
     * @param request the registration request containing name, email, and password
     * @return UserResponseDto containing the saved admin user details
     *
     * @throws AppCustomException if the email is already registered
     *
     * @implSpec New users are persisted with the ADMIN role automatically assigned.
     */
    public UserResponseDto registerUser(UserRequestDto request) {
        log.info("Registering new Admin service layer");
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppCustomException("Email already registered", HttpStatus.CONFLICT);
        }

        User user = new User(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                Role.ADMIN
        );

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    /**
     * Retrieves a user by their unique ID.
     *
     * @param id the unique identifier of the user
     * @return UserResponseDto containing the user's details
     *
     * @throws AppCustomException if no user is found with the given ID
     *
     * @apiNote Returns HTTP 404 if the user does not exist.
     */
    public UserResponseDto getUserById(Long id) {
        log.info("view user by id in service layer");
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppCustomException("User not found", HttpStatus.NOT_FOUND));
        return mapToResponse(user);
    }

    /**
     * Retrieves all registered users across all roles in the system.
     *
     * @return list of UserResponseDto for all users
     *
     * @throws AppCustomException if no users exist in the system
     *
     * @apiNote Only admin users are permitted to call this operation.
     */
    public List<UserResponseDto> getAllUsers() {
        log.info("get all users in service layer");

        List<UserResponseDto> users = userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        if (users.isEmpty()) {
            throw new AppCustomException("No users found in the system", HttpStatus.NOT_FOUND);
        }

        return users;
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
