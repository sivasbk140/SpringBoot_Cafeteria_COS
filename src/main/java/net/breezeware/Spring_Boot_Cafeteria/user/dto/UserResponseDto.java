package net.breezeware.Spring_Boot_Cafeteria.user.dto;


import lombok.*;
import net.breezeware.Spring_Boot_Cafeteria.user.enumeration.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto  {
    Long id;
    String name;
    String email;
    Role role;

}