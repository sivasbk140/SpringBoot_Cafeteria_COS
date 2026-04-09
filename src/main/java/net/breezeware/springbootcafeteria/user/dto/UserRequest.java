package net.breezeware.springbootcafeteria.user.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import net.breezeware.springbootcafeteria.user.enumeration.Role;

/**
 * DTO representing the request payload for user registration.
 *
 * Carries the input data required to register any type of user
 * (ADMIN, STAFF, CUSTOMER, or DELIVERY_STAFF) in the cafeteria system.
 *
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    /** Full name of the user. */

    @NotBlank(message = "Name is required")
    @Column(nullable = false, length = 100)
    @Schema(description = "name of the user ",example = "siva")
    String name;

    /** Email address of the user (used as login credential). */

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Schema (description = "emailId",example = "siva@gmail.com")
    String email;

    /** Password for the user account. */

    @NotBlank(message = "Password is required")
    @Schema(description = "type the password " , example = "siva@123")
    String password;

    /** Role to assign to the user (CUSTOMER, ADMIN, STAFF, or DELIVERY_STAFF). */

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Role is required")
    @Schema(description = "the role of the user",example = "CUSTOMER // ADMIN // STAFF // DELIVERY_STAFF")
    Role role;

}