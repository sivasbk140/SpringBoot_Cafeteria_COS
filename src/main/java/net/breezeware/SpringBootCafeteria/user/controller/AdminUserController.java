package net.breezeware.SpringBootCafeteria.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.breezeware.SpringBootCafeteria.user.dto.UserLoginRequestDto;
import net.breezeware.SpringBootCafeteria.user.dto.UserRequestDto;
import net.breezeware.SpringBootCafeteria.user.dto.UserResponseDto;
import net.breezeware.SpringBootCafeteria.user.service.AdminUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for admin user management.
 * <p>
 * Exposes endpoints for admin registration, login, and viewing all users.
 * Base path: {@code /api/Admin}
 * </p>
 */
@Tag(name = "Admin User APIs", description = "APIs for admin to register, login and manage all users")
@Slf4j
@RestController
@RequestMapping("/api/Admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @Operation(summary = "Register admin", description = "Registers a new admin account")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Admin registered successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Email already registered", content = @Content)
          //  @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    /**
     * Registers a new admin account.
     *
     * @param request the registration payload with name, email, password, and role
     * @return HTTP 201 with the created admin's profile in the response body
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody UserRequestDto request) {
        log.info("Registering the Admin user");
        UserResponseDto response = adminUserService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Admin login", description = "Authenticates an admin with email and password")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid email or password", content = @Content)
          //  @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    /**
     * Authenticates an admin with email and password.
     *
     * @param request the login payload with email and password
     * @return HTTP 200 with the authenticated admin's profile in the response body
     */
    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> login(@RequestBody UserLoginRequestDto request) {
        log.info("Logging in with mail and password");
        UserResponseDto response = adminUserService.login(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get  users by id", description = "Returns a particular user by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User retrieved",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserResponseDto.class))))
         //   @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    /**
     * Retrieves a user by their unique ID.
     *
     * @param id the ID of the user to retrieve
     * @return HTTP 200 with the user's profile in the response body
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        log.info("view user by user_id");
        UserResponseDto response = adminUserService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all users", description = "Returns a list of all registered users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users retrieved",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserResponseDto.class))))
           // @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    /**
     * Retrieves all registered users in the system.
     *
     * @return HTTP 200 with a list of all user profiles in the response body
     */
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = adminUserService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
