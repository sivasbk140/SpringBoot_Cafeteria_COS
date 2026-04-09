package net.breezeware.springbootcafeteria.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing the request payload for user login.
 *
 * Carries the email and password credentials submitted by any type of user
 * (ADMIN, STAFF, CUSTOMER, or DELIVERY_STAFF) during the authentication flow.
 *
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequest {

    /** Email address used as the login identifier. */

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Schema(description = "emailId",example = "siva@gmail.com")
    String email;

    /** Password for authentication. */
    @NotBlank(message = "Password is required")
    @Schema(description = "type the password " , example = "siva@123")
    String password;

}
