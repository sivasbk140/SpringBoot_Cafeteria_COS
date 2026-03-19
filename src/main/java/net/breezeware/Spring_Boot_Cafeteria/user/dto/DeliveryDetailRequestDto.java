package net.breezeware.Spring_Boot_Cafeteria.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDetailRequestDto {
    private String location;
    private String email;
    private String phoneNumber;
}
