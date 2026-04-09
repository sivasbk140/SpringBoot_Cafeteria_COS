package net.breezeware.springbootcafeteria.user.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import net.breezeware.springbootcafeteria.user.enumeration.Role;

/**
 * DTO representing user details returned in responses after login or registration.
 *
 * Contains the user's public profile information. Sensitive fields
 * such as password are intentionally excluded from this response.
 *
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    /** Unique identifier of the user. */
    @Schema(description = "id of the user",example = "1")
    Long id;

    /** Full name of the user. */
    @Schema(description = "name of the user",example = "siva")
    String name;

    /** Email address of the user. */
    @Schema(description = "email of the user",example = "siva@gmail.com")
    String email;

    /** Role assigned to the user (CUSTOMER, ADMIN, STAFF, or DELIVERY_STAFF). */
    @Schema(description = "role of the user",example = "CUSTOMER // ADMIN // STAFF // DELIVERY_STAFF")
    Role role;

}