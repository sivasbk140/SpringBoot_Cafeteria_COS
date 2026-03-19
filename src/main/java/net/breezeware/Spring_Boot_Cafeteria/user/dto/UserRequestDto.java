package net.breezeware.Spring_Boot_Cafeteria.user.dto;
import lombok.*;
import net.breezeware.Spring_Boot_Cafeteria.user.enumeration.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {
    String name;
    String email;
    String password;
    Role role;
}