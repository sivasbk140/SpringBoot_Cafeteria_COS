package net.breezeware.Spring_Boot_Cafeteria.user.dto;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
class UserResponseDto {
    Long id;
    String name;
    String email;
}