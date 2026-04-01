package net.breezeware.SpringBootCafeteria.order.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {
  @Schema(description = "user id of the one who made an order ",example = "101")
    private Long user_id;
    @Schema(description = "List of items included in the order")
    private List<OrderItemRequestDto> items;
    @Schema(description = "user's name  the one who made an order ",example = "siva")
    private String deliveryName;
    @Schema(description = "user's phone number  the one who made an order ",example = "siva")
    private String deliveryPhone;
    @Schema(description = "user's address  the one who made an order ",example = "siva")
    private String deliveryAddress;

}
