package net.breezeware.Spring_Boot_Cafeteria.order.dto;
import lombok.*;
@Data
@NoArgsConstructor
@AllArgsConstructor

public class OrderItemRequestDto {

 private  Long id;
 private Long quantity;
 private  Double price;
}
