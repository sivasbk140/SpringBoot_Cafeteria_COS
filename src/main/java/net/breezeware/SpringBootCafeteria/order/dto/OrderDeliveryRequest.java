package net.breezeware.SpringBootCafeteria.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDeliveryRequest {
    @Schema(description = "user name",example = "siva")
    private String name;
    @Schema(description = "user phone number",example = "12345678901")
    private String phone;
    @Schema(description = "user address",example = "coimbatore DiamondCrustApartment")
    private String address;
}
