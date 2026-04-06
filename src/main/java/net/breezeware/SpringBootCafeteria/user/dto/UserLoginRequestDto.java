package net.breezeware.SpringBootCafeteria.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing the request payload for user login.
 *
 * <p>Carries the email and password credentials submitted by any type of user
 * (ADMIN, STAFF, CUSTOMER, or DELIVERY_STAFF) during the authentication flow.</p>
 *
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequestDto {

    /** Email address used as the login identifier. */
    @Schema(description = "emailId",example = "siva@gmail.com")
    String email;

    /** Password for authentication. */
    @Schema(description = "type the password " , example = "siva@123")
    String password;

}
