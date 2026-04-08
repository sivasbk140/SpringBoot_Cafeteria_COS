package net.breezeware.springbootcafeteria.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO representing delivery details provided by the customer at checkout.
 *
 * This object captures the recipient's name, phone, and delivery address
 * and is used to create an {@link net.breezeware.springbootcafeteria.order.entity.OrderDeliveryMap} record.
 *
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDeliveryRequest {

    /** Full name of the delivery recipient. */
    @Schema(description = "user name",example = "siva")
    private String name;

    /** Contact phone number of the delivery recipient. */
    @Schema(description = "user phone number",example = "12345678901")
    private String phone;

    /** Delivery address for the order. */
    @Schema(description = "user address",example = "coimbatore DiamondCrustApartment")
    private String address;
}
