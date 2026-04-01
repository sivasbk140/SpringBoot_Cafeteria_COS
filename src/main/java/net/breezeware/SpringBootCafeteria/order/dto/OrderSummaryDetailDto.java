package net.breezeware.SpringBootCafeteria.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import net.breezeware.SpringBootCafeteria.order.enumeration.OrderStatus;

import  lombok .*;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class  OrderSummaryDetailDto {
    @Schema(description = "order id ", example = "101")
    private int orderId;
    @Schema(description = "user id who purchased food items", example = "1")
    private int userId;
    @Schema(description = "status of the order ", example = "ORDER_PLACED")
    private OrderStatus status;
    @Schema(description = "grandtotal of the order items in an order ", example = "1000.0")
    private double totalPrice;
    @Schema(description = "Order creation timestamp", example = "2026-04-01T10:30:00")
    private String createdOn;

}