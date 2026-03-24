package net.breezeware.Spring_Boot_Cafeteria.order.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDeliveryRequest {
    private String name;
    private String phone;
    private String address;
}
