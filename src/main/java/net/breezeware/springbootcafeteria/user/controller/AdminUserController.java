package net.breezeware.springbootcafeteria.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import lombok.val;
import net.breezeware.springbootcafeteria.user.dto.UserLoginRequestDto;
import net.breezeware.springbootcafeteria.user.dto.UserRequestDto;
import net.breezeware.springbootcafeteria.user.dto.UserResponseDto;
import net.breezeware.springbootcafeteria.user.service.AdminUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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

    })

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody UserRequestDto request) {
        log.info("Registering the Admin user");
        UserResponseDto response = adminUserService.registerUser(request);
        val body = ResponseEntity.status(HttpStatus.CREATED).body(response);
        log.info("Leaving");
        return body;
    }

    @Operation(summary = "Admin login", description = "Authenticates an admin with email and password")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid email or password", content = @Content)

    })

    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> login(@RequestBody @Valid UserLoginRequestDto request) {
        log.info("Logging in with mail and password");
        UserResponseDto response = adminUserService.login(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get  users by id", description = "Returns a particular user by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User retrieved",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserResponseDto.class))))

    })

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

    })

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = adminUserService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
