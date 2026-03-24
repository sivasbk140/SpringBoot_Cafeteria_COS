package net.breezeware.Spring_Boot_Cafeteria.order.dto;
import lombok.*;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {

    private Long user_id;
    private List<OrderItemRequestDto> items;
    private String deliveryName;
    private String deliveryPhone;
    private String deliveryAddress;

}
