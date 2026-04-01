package net.breezeware.SpringBootCafeteria.order.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
@Data
@NoArgsConstructor
@AllArgsConstructor

public class OrderItemRequestDto {

 @Schema(description =  "order id",example = "1")
 private  Long id;
 @Schema(description =  "order item quantity",example = "3")
 private Long quantity;
 @Schema(description = "price of the orderitem",example = "250.0")
 private  Double price;
}
