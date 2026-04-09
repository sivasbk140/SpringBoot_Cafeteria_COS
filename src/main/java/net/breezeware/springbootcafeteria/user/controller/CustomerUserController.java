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
import net.breezeware.springbootcafeteria.user.service.CustomerUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Customer User APIs", description = "APIs for customer registration and login")
@Slf4j
@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CustomerUserController {

    private final CustomerUserService customerUserService;

    @Operation(summary = "Register customer", description = "Registers a new customer account")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Customer registration details",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UserRequest.class),
                    examples = {
                            @ExampleObject(
                                    name = "valid customer",
                                    value = """
                                        {
                                          "name": "ravi",
                                          "email": "ravi@gmail.com",
                                          "password": "ravi@123",
                                          "role": "CUSTOMER"
                                        }
                                        """
                            ),
                            @ExampleObject(
                                    name = "Another Customer",
                                    value = """
                                        {
                                          "name": "priya",
                                          "email": "priya@example.com",
                                          "password": "priya@456",
                                          "role": "CUSTOMER"
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
        log.info("Registering the customer user");
        UserResponse response = customerUserService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Customer login", description = "Authenticates a customer with email and password")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Customer login details",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UserLoginRequest.class),
                    examples = {
                            @ExampleObject(
                                    name = "valid customer",
                                    value = """
                                        {
                                          "email": "ravi@gmail.com",
                                          "password": "ravi@123"
                                        }
                                        """
                            ),
                            @ExampleObject(
                                    name = "Another Customer",
                                    value = """
                                        {
                                          "email": "priya@example.com",
                                          "password": "priya@456"
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
        UserResponse response = customerUserService.login(request);
        return ResponseEntity.ok(response);
    }
}
