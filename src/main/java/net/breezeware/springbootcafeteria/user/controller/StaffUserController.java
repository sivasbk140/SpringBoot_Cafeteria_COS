package net.breezeware.springbootcafeteria.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.breezeware.springbootcafeteria.user.dto.UserLoginRequest;
import net.breezeware.springbootcafeteria.user.dto.UserRequest;
import net.breezeware.springbootcafeteria.user.dto.UserResponse;
import net.breezeware.springbootcafeteria.user.service.StaffUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Staff User APIs", description = "APIs for cafeteria staff registration and login")
@Slf4j
@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StaffUserController {

    private final StaffUserService staffUserService;

    @Operation(summary = "Register staff", description = "Registers a new staff account")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Staff registration details",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UserRequest.class),
                    examples = {
                            @ExampleObject(
                                    name = "valid staff",
                                    value = """
                                        {
                                          "name": "kumar",
                                          "email": "kumar@gmail.com",
                                          "password": "kumar@123",
                                          "role": "STAFF"
                                        }
                                        """
                            ),
                            @ExampleObject(
                                    name = "Another Staff",
                                    value = """
                                        {
                                          "name": "arun",
                                          "email": "arun@example.com",
                                          "password": "arun@456",
                                          "role": "STAFF"
                                        }
                                        """
                            )
                    }
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Registration successful",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "statusCode": 201,
                          "message": "Registration Successful"
                        }
                    """))),
            @ApiResponse(responseCode = "400", description = "Email Already Exists",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "statusCode": 400,
                          "message": "Email Already Exists"
                        }
                    """)))
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody @Valid UserRequest request) {
        log.info("Registering the staff user");
        UserResponse response = staffUserService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Staff login", description = "Authenticates a staff member with email and password")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Staff login details",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UserLoginRequest.class),
                    examples = {
                            @ExampleObject(
                                    name = "valid staff",
                                    value = """
                                        {
                                          "email": "kumar@gmail.com",
                                          "password": "kumar@123"
                                        }
                                        """
                            ),
                            @ExampleObject(
                                    name = "Another Staff",
                                    value = """
                                        {
                                          "email": "arun@example.com",
                                          "password": "arun@456"
                                        }
                                        """
                            )
                    }
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "statusCode": 200,
                          "message": "Login Successful"
                        }
                    """)))
    })
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody @Valid UserLoginRequest request) {
        log.info("Logging in with mail and password");
        UserResponse response = staffUserService.login(request);
        return ResponseEntity.ok(response);
    }
}
