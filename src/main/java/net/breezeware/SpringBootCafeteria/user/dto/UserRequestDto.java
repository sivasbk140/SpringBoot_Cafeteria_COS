package net.breezeware.SpringBootCafeteria.user.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import net.breezeware.SpringBootCafeteria.user.enumeration.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {
    @Schema(description = "name of the user ",example = "siva")
    String name;
    @Schema (description = "emailId",example = "siva@gmail.com")
    String email;
    @Schema(description = "type the password " , example = "siva@123")
    String password;
    @Schema(description = "the role of the user",example = "CUSTOMER")
    Role role;
}