package net.breezeware.SpringBootCafeteria.order.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import net.breezeware.SpringBootCafeteria.food.entity.FoodItem;

import java.util.Date;

/**
 * Entity representing a single line item within a customer order.
 *
 * <p>Each OrderItem is linked to an {@link Order} and a {@link FoodItem},
 * capturing the unit price and quantity at the time the order was placed.
 * Timestamps are managed automatically via JPA lifecycle callbacks.</p>
 *
 * @author Siva
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@ToString(exclude = {"order", "foodItem"})
public class OrderItem {

    /**
     * Unique identifier for the order item.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The parent order this item belongs to.
     *
     * <p>Many-to-one relationship with Order entity.</p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @NotNull(message = "Order is required")
    @NonNull
    private Order order;

    /**
     * The food item associated with this order line.
     *
     * <p>Many-to-one relationship with FoodItem entity.</p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_item_id", nullable = false)
    @NotNull(message = "Food item is required")
    @NonNull
    private FoodItem foodItem;

    /**
     * Unit price of the food item at the time the order was placed.
     *
     * <p>Snapshotted at order creation to preserve historical pricing.</p>
     */
    @Positive(message = "Price must be positive")
    @NotNull(message = "Price is required")
    @NonNull
    @Column(nullable = false)
    private Double price;

    /**
     * Number of units of the food item ordered.
     */
    @Min(value = 1, message = "Quantity must be at least 1")
    @NotNull(message = "Quantity is required")
    @NonNull
    @Column(nullable = false)
    private Integer quantity;

    /**
     * Timestamp when the order item was created.
     *
     * <p>Automatically set during persistence and not updatable.</p>
     */
    @Column(name = "created_on", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    /**
     * Timestamp when the order item was last updated.
     */
    @Column(name = "updated_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn;

    /**
     * Lifecycle callback executed before persisting the entity.
     *
     * <p>Initializes both createdOn and updatedOn timestamps.</p>
     */
    @PrePersist
    protected void onCreate() {
        createdOn = new Date();
        updatedOn = new Date();
    }

    /**
     * Lifecycle callback executed before updating the entity.
     *
     * <p>Refreshes the updatedOn timestamp.</p>
     */
    @PreUpdate
    protected void onUpdate() {
        updatedOn = new Date();
    }

    /**
     * Calculates the total price for this line item.
     *
     * @return price multiplied by quantity
     *
     * @implSpec Computed dynamically from the unit price and quantity fields.
     */
    public Double getTotalPrice() {
        return price * quantity;
    }

    /**
     * Increases the quantity of this order item by the given amount.
     *
     * @param amount the number of units to add
     */
    public void increaseQuantity(int amount) {
        this.quantity += amount;
    }

    /**
     * Decreases the quantity of this order item by the given amount.
     *
     * @param amount the number of units to remove
     *
     * @throws IllegalArgumentException if the resulting quantity would be less than 1
     */
    public void decreaseQuantity(int amount) {
        if (this.quantity - amount < 1) {
            throw new IllegalArgumentException("Quantity cannot be less than 1");
        }
        this.quantity -= amount;
    }
}