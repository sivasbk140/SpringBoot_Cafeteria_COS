package net.breezeware.springbootcafeteria.food.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Date;
/**
 * Entity representing a food item available in the cafeteria system.
 *
 * Tracks stock via the {@code quantity} field and exposes helper methods for
 * availability checking, stock reduction (on order placement), and stock restoration
 * (on order cancellation). Timestamps are managed automatically via JPA lifecycle hooks.
 *
 * @author Siva
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@Entity
@Table(name = "food_item")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class FoodItem {

    /**
     * Unique identifier for the food item.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the food item.
     */
    @NotBlank(message = "Name is required")
    @NonNull
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Unit price of the food item.
     */
    @Positive(message = "Price must be positive")
    @NonNull
    @Column(nullable = false)
    private Double price;

    /**
     * Available stock quantity of the food item.
     *
     * Reduced when orders are placed; restored when orders are cancelled.
     */
    @Min(value = 0, message = "Quantity cannot be negative")
    @NonNull
    @Column(nullable = false)
    private Integer quantity;

    /**
     * Category of the food item (e.g. BREAKFAST, LUNCH, SNACK).
     */
    @NonNull
    @Column(length = 50)
    private String category;

    /**
     * Optional description providing additional details about the food item.
     */
    @Column(length = 500)
    private String description;

    /**
     * Timestamp when the food item was created.
     *
     * Automatically set during persistence and not updatable.
     */
    @Column(name = "created_on", updatable = false)

    private Instant createdOn;

    /**
     * Timestamp when the food item was last updated.
     */
    @Column(name = "updated_on")

    private Instant updatedOn;

    /**
     * Constructs a FoodItem with the given details (without ID or timestamps).
     *
     * @param name        the name of the food item
     * @param price       the unit price
     * @param quantity    the initial stock quantity
     * @param category    the food category
     * @param description optional description
     */
    public FoodItem(String name, Double price, Integer quantity, String category, String description) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.description = description;
    }

    /**
     * Lifecycle callback executed before persisting the entity.
     *
     * Initializes both createdOn and updatedOn timestamps.
     */
    @PrePersist
    protected void onCreate() {
        createdOn = Instant.now();
        updatedOn = Instant.now();
    }

    /**
     * Lifecycle callback executed before updating the entity.
     *
     * Refreshes the updatedOn timestamp.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedOn = Instant.now();
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
     * Called when an order is placed to deduct units from inventory.
     *
     * @param amount the number of units to deduct from stock
     */
    public void reduceStock(int amount) {
        if (amount > quantity) {
            throw new IllegalArgumentException("Insufficient stock for item: " + name);
        }
        this.quantity -= amount;
    }

    /**
     * Restores the stock of this food item by the given amount.
     * Called when an order is cancelled to return units back to inventory.
     *
     * @param amount the number of units to add back to stock
     */
    public void restoreStock(int amount) {
        this.quantity += amount;
    }

}