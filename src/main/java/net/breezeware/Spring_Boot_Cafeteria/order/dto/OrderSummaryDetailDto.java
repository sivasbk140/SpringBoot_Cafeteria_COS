package net.breezeware.Spring_Boot_Cafeteria.order.dto;

import net.breezeware.Spring_Boot_Cafeteria.order.enumeration.OrderStatus;

import  lombok .*;
@Data
@NoArgsConstructor
@AllArgsConstructor

public class  OrderSummaryDetailDto {
    private int orderId;
    private int userId;
    private OrderStatus status;
    private double totalPrice;
    private String createdOn;

}