package net.breezeware.Spring_Boot_Cafeteria.order.dto;

import net.breezeware.Spring_Boot_Cafeteria.order.enumeration.OrderStatus;

import java.util.List;

import lombok.*;
@Data@NoArgsConstructor
@AllArgsConstructor

public class OrderDetailDto {

    private int orderId;
    private int userId;
    private String userName;
    private OrderStatus status;
    private List<OrderItemDetailDTO> items;
    private double totalPrice;
    private String deliveryEmail;
    private String deliveryPhone;
    private String deliveryLocation;
    private String createdOn;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDetailDTO {
        private String foodItemName;
        private int quantity;
        private double price;
        private double totalPrice;


    }

}
