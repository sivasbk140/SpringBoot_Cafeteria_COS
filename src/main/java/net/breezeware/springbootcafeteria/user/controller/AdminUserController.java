package net.breezeware.springbootcafeteria.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import lombok.val;
import net.breezeware.springbootcafeteria.user.dto.UserLoginRequest;
import net.breezeware.springbootcafeteria.user.dto.UserRequest;
import net.breezeware.springbootcafeteria.user.dto.UserResponse;
import net.breezeware.springbootcafeteria.user.service.AdminUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User registration details",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UserRequest.class),
                    examples = {
                            @ExampleObject(
                                    name = "valid admin",
                                    value = """
                                        {
                                          "name": "siva",
                                          "email": "siva@gmail.com",
                                          "password": "siva@123",
                                          "role": "ADMIN"
                                        }
                                        """
                            ),
                            @ExampleObject(
                                    name = "Another Admin",
                                    value = """
                                        {
                                          "name": "john",
                                          "email": "john@example.com",
                                          "password": "john@456",
                                          "role": "ADMIN"
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
        log.info("Registering the Admin user");
        UserResponse response = adminUserService.registerUser(request);
        val body = ResponseEntity.status(HttpStatus.CREATED).body(response);
        log.info("Leaving");
        return body;
    }



    @Operation(summary = "Admin login", description = "Authenticates an admin with email and password")
       @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User login details",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UserLoginRequest.class),
                    examples = {
                            @ExampleObject(
                                    name = "valid admin",
                                    value = """
                                        {
                                       
                                          "email": "siva@gmail.com",
                                          "password": "siva@123"
                                       
                                        }
                                        """
                            ),
                            @ExampleObject(
                                    name = "Another Admin",
                                    value = """
                                        {
               
                                          "email": "john@example.com",
                                          "password": "john@456"
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
    public ResponseEntity<UserResponse> login(  @RequestBody @Valid UserLoginRequest request) {
        log.info("Logging in with mail and password");
        UserResponse response = adminUserService.login(request);
        return ResponseEntity.ok(response);
    }



    @Operation(summary = "Get  users by id", description = "Returns a particular user by id",
    parameters = {@Parameter(name ="userId",description = "ID of the user the admin need to check.")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User retrieved",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "statusCode": 200,
                          "message": "successfully found the user ",
                          "details": ["User found for ID"]
                        }
                    """))),
            @ApiResponse(responseCode = "404", description = "User retrieved",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "statusCode": 404,
                          "message": "NOT_FOUND",
                          "details": ["User with ID not found"]
                        }
                    """)))


    })

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        log.info("view user by user_id");
        UserResponse response = adminUserService.getUserById(id);
        return ResponseEntity.ok(response);
    }




    @Operation(summary = "Get all users", description = "Returns a list of all registered users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users retrieved",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                          "statusCode": 200,
                          "message": "successfully returned the list of users"
                   
                        }
                    """))),
            @ApiResponse(responseCode = "200", description = "No users Found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = """
                        {
                    []
                        }
                    """)))

    })

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = adminUserService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
