package net.breezeware.SpringBootCafeteria.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.breezeware.SpringBootCafeteria.user.dto.UserLoginRequestDto;
import net.breezeware.SpringBootCafeteria.user.dto.UserRequestDto;
import net.breezeware.SpringBootCafeteria.user.dto.UserResponseDto;
import net.breezeware.SpringBootCafeteria.user.service.StaffUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for cafeteria staff user management.
 * <p>
 * Exposes endpoints for staff registration and login.
 * Base path: {@code /api/staff}
 * </p>
 */
@Tag(name = "Staff User APIs", description = "APIs for cafeteria staff registration and login")
@Slf4j
@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StaffUserController {

    private final StaffUserService staffUserService;

    @Operation(summary = "Register staff", description = "Registers a new staff account")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Staff registered successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Email already registered", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    /**
     * Registers a new cafeteria staff account.
     *
     * @param request the registration payload with name, email, and password
     * @return HTTP 201 with the created staff member's profile in the response body
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody UserRequestDto request) {
        log.info("Registering the staff user");
        UserResponseDto response = staffUserService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Staff login", description = "Authenticates a staff member with email and password")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid email or password", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    /**
     * Authenticates a cafeteria staff member with email and password.
     *
     * @param request the login payload with email and password
     * @return HTTP 200 with the authenticated staff member's profile in the response body
     */
    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> login(@RequestBody UserLoginRequestDto request) {
        log.info("Logging in with mail and password");
        UserResponseDto response = staffUserService.login(request);
        return ResponseEntity.ok(response);
    }
}
