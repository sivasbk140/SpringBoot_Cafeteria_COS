package net.breezeware.SpringBootCafeteria.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequestDto {
    @Schema(description = "emailId",example = "siva@gmail.com")
    String email;
    @Schema(description = "type the password " , example = "siva@123")
    String password;

}
