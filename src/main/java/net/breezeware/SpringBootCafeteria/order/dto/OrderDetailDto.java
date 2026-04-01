package net.breezeware.SpringBootCafeteria.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import net.breezeware.SpringBootCafeteria.order.enumeration.OrderStatus;

import java.util.List;

import lombok.*;
@Data@NoArgsConstructor
@AllArgsConstructor

public class OrderDetailDto {


    @Schema(description = "Unique identifier of the order", example = "1")
    private Long orderId;

    @Schema(description = "ID of the user who placed the order", example = "101")
    private int userId;

    @Schema(description = "Name of the user who placed the order", example = "Siva ")
    private String userName;

    @Schema(description = "Current status of the order", example = "ORDER_CONFIRMED")
    private OrderStatus status;

    @Schema(description = "List of items included in the order")
    private List<OrderItemDetailDTO> items;

    @Schema(description = "Total price of the order", example = "250.75")
    private double totalPrice;

    @Schema(description = "Name of the delivery recipient", example = "Siva")
    private String deliveryName;

    @Schema(description = "Contact phone number for delivery", example = "9876543210")
    private String deliveryPhone;

    @Schema(description = "Delivery address of the order", example = "123, Gandhi Street, Chennai")
    private String deliveryAddress;

    @Schema(description = "Order creation timestamp", example = "2026-04-01T10:30:00")
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
