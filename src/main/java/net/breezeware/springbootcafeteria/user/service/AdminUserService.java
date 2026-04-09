package net.breezeware.springbootcafeteria.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.breezeware.springbootcafeteria.exception.AppCustomException;
import net.breezeware.springbootcafeteria.user.dto.UserLoginRequest;
import net.breezeware.springbootcafeteria.user.dto.UserRequest;
import net.breezeware.springbootcafeteria.user.dto.UserResponse;
import net.breezeware.springbootcafeteria.user.entity.User;
import net.breezeware.springbootcafeteria.user.enumeration.Role;
import net.breezeware.springbootcafeteria.user.dao.UserRepository;
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
    public UserResponse login(UserLoginRequest request) {
        log.info("Admin logging In service layer");
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> { log.error("Login failed: email not found :{}" ,request.getEmail());
                    return new AppCustomException("Invalid email or password", HttpStatus.UNAUTHORIZED); });

        if (!user.getPassword().equals(request.getPassword())) {
            log.error("Login Failed : incorrect password for Email :{}", request.getEmail());
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
    public UserResponse registerUser(UserRequest request) {
        log.info("Registering new Admin service layer");
        if (userRepository.existsByEmail(request.getEmail())) {
              log.error("Email already exist: {}" ,request.getEmail());
            throw new AppCustomException("Email already registered", HttpStatus.CONFLICT);
        }

        User user = new User(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                Role.ADMIN
        );
        log.info("Admin registered successfully with email: {}", request.getEmail());
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
    public UserResponse getUserById(Long id) {
        log.info("view user by id in service layer");
        User user = userRepository.findById(id)
                .orElseThrow(() -> { log.error("user not found for id :{}",id);
                    return new AppCustomException("User not found", HttpStatus.NOT_FOUND);
                });
        log.info("User found with id: {}", id);
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
    public List<UserResponse> getAllUsers() {
        log.info("get all users in service layer");

        List<UserResponse> users = userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        if (users.isEmpty()) {
            log.error("no  users found in the system");
            throw new AppCustomException("No users found in the system", HttpStatus.NOT_FOUND);
        }
      log.info("returning users");
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
    private UserResponse mapToResponse(User user) {
        log.debug("Mapping user to response: {}", user.getId());
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
