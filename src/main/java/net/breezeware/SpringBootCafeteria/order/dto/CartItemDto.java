package net.breezeware.SpringBootCafeteria.order.dto;

 import io.swagger.v3.oas.annotations.media.Schema;
 import lombok.*;

 import java.util.List;

@Data
 @NoArgsConstructor
 @AllArgsConstructor

public class CartItemDto {
 @Schema(description = "food item id",example = "101")
 private Long foodItemId;
 @Schema(description = "food item name",example = "Biriyani")
 private String foodItemName;
 @Schema(description = "food item price",example = "200.0")
 private  Double totalPrice;
 @Schema(description = "food item quantity",example = "50")
 private  Long quantity;
}
