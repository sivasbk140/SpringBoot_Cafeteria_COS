package net.breezeware.Spring_Boot_Cafeteria.order.dto;

 import lombok.*;

 import java.util.List;

@Data
 @NoArgsConstructor
 @AllArgsConstructor

public class CartItemDto {
 private Long foodItemId;
 private String foodItemName;
 private  Double totalPrice;
 private  Long quantity;
}
