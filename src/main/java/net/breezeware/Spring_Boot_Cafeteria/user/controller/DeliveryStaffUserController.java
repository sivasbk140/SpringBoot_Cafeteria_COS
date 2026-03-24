package net.breezeware.Spring_Boot_Cafeteria.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import net.breezeware.Spring_Boot_Cafeteria.user.dto.UserRequestDto;
import net.breezeware.Spring_Boot_Cafeteria.user.dto.UserResponseDto;
import net.breezeware.Spring_Boot_Cafeteria.user.service.DeliveryStaffService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.*;

@Slf4j

@RestController
@RequestMapping("/api/deliveryStaff")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")

public class DeliveryStaffUserController {

    private final DeliveryStaffService deliveryStaffService;

    @Operation(summary = "Register delivery staff", description = "Registers a new delivery staff account")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Delivery Staff registered successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Email already registered", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody UserRequestDto request) {
        log.info("Registering the staff user");
        UserResponseDto response = deliveryStaffService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Delivery Staff login", description = "Authenticates a delivery staff member with email and password")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid email or password", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> login(@RequestBody UserRequestDto request) {
        log.info("Logging in with mail and password");
        UserResponseDto response = deliveryStaffService.login(request);
        return ResponseEntity.ok(response);
    }
}

