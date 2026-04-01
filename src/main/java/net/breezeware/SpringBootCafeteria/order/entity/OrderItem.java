package net.breezeware.SpringBootCafeteria.order.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import net.breezeware.SpringBootCafeteria.food.entity.FoodItem;

import java.util.Date;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@ToString(exclude = {"order", "foodItem"})
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @NotNull(message = "Order is required")
    @NonNull
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_item_id", nullable = false)
    @NotNull(message = "Food item is required")
    @NonNull
    private FoodItem foodItem;

    @Positive(message = "Price must be positive")
    @NotNull(message = "Price is required")
    @NonNull
    @Column(nullable = false)
    private Double price;

    @Min(value = 1, message = "Quantity must be at least 1")
    @NotNull(message = "Quantity is required")
    @NonNull
    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "created_on", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    @Column(name = "updated_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn;

    @PrePersist
    protected void onCreate() {
        createdOn = new Date();
        updatedOn = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedOn = new Date();
    }


    public Double getTotalPrice() {
        return price * quantity;
    }

    public void increaseQuantity(int amount) {
        this.quantity += amount;
    }

    public void decreaseQuantity(int amount) {
        if (this.quantity - amount < 1) {
            throw new IllegalArgumentException("Quantity cannot be less than 1");
        }
        this.quantity -= amount;
    }
}