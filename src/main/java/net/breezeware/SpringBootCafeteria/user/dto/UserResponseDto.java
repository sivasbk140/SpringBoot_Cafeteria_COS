package net.breezeware.SpringBootCafeteria.user.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import net.breezeware.SpringBootCafeteria.user.enumeration.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto  {
    @Schema(description = "id of the user",example = "1")
    Long id;
    @Schema(description = "name of the user",example = "siva")
    String name;
    @Schema(description = "email of the user",example = "siva@gmail.com")
    String email;
    @Schema(description = "role of the user",example = "CUSTOMER // ADMIN // STAFF // DELIVERY_STAFF")
    Role role;

}