package net.breezeware.SpringBootCafeteria.food.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
/**
 * JPA entity representing a food item available in the cafeteria.
 * <p>
 * Tracks stock via the {@code quantity} field and exposes helper methods for
 * availability checking, stock reduction (on order placement), and stock restoration
 * (on order cancellation). Timestamps are managed automatically via lifecycle hooks.
 * </p>
 */
@Slf4j
@Entity
@Table(name = "food_item")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class FoodItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @NonNull
    @Column(nullable = false, length = 100)
    private String name;

    @Positive(message = "Price must be positive")
    @NonNull
    @Column(nullable = false)
    private Double price;

    @Min(value = 0, message = "Quantity cannot be negative")
    @NonNull
    @Column(nullable = false)
    private Integer quantity;

    @NonNull
    @Column(length = 50)
    private String category;

    @Column(length = 500)
    private String description;

    @Column(name = "created_on", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    @Column(name = "updated_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn;

    public FoodItem(String name, Double price, Integer quantity, String category, String description) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.description = description;
    }

    @PrePersist
    protected void onCreate() {
        createdOn = new Date();
        updatedOn = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedOn = new Date();
    }

    // Business logic

    /**
     * Returns whether this food item is currently in stock.
     *
     * @return {@code true} if {@code quantity > 0}, {@code false} otherwise
     */
    public boolean isAvailable() {
        return quantity > 0;
    }

    /**
     * Checks whether the available stock can satisfy the requested quantity.
     *
     * @param requestedQuantity the quantity the customer wants to order
     * @return {@code true} if {@code quantity >= requestedQuantity}
     */
    public boolean hasStock(int requestedQuantity) {
        return quantity >= requestedQuantity;
    }

    /**
     * Reduces the stock of this food item by the given amount.
     * <p>Called when an order is placed or checked out.</p>
     *
     * @param amount the number of units to deduct from stock
     * @throws IllegalArgumentException if {@code amount} exceeds the current {@code quantity}
     */
    public void reduceStock(int amount) {
        if (amount > quantity) {
            throw new IllegalArgumentException("Insufficient stock");
        }
        this.quantity -= amount;
    }

    /**
     * Restores the stock of this food item by the given amount.
     * <p>Called when an order is cancelled to return units back to inventory.</p>
     *
     * @param amount the number of units to add back to stock
     */
    public void restoreStock(int amount) {
        this.quantity += amount;
    }

}