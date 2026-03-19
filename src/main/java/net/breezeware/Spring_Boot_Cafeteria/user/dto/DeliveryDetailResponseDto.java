package net.breezeware.Spring_Boot_Cafeteria.user.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class DeliveryDetailResponseDto {

    private Long id;
    private Long userId;
    private String email;
    private String phoneNumber;
    private String location;

}
