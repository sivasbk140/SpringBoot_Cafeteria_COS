package net.breezeware.SpringBootCafeteria.user.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import net.breezeware.SpringBootCafeteria.user.enumeration.Role;

/**
 * DTO representing the request payload for user registration.
 *
 * <p>Carries the input data required to register any type of user
 * (ADMIN, STAFF, CUSTOMER, or DELIVERY_STAFF) in the cafeteria system.</p>
 *
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {

    /** Full name of the user. */
    @Schema(description = "name of the user ",example = "siva")
    String name;

    /** Email address of the user (used as login credential). */
    @Schema (description = "emailId",example = "siva@gmail.com")
    String email;

    /** Password for the user account. */
    @Schema(description = "type the password " , example = "siva@123")
    String password;

    /** Role to assign to the user (CUSTOMER, ADMIN, STAFF, or DELIVERY_STAFF). */
    @Schema(description = "the role of the user",example = "CUSTOMER // ADMIN // STAFF // DELIVERY_STAFF")
    Role role;
}