package net.breezeware.Spring_Boot_Cafeteria.user.dto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

class UserRequestDto {
    String name;
    String email;
    String password;
}