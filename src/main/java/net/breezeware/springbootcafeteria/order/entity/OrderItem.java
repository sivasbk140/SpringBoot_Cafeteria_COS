package net.breezeware.springbootcafeteria.order.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import net.breezeware.springbootcafeteria.food.entity.FoodItem;

import java.time.Instant;

/**
 * Entity representing a single line item within a customer order.
 *
 * Each OrderItem is linked to an {@link Order} and a {@link FoodItem},
 * capturing the unit price and quantity at the time the order was placed.
 * Timestamps are managed automatically via JPA lifecycle callbacks.
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_item_seq")
    @SequenceGenerator(name = "order_item_seq", sequenceName = "order_item_seq", allocationSize = 1)
    private Long id;

    /**
     * The parent order this item belongs to.
     *
     * Many-to-one relationship with Order entity.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @NotNull(message = "Order is required")
    @NonNull
    private Order order;

    /**
     * The food item associated with this order line.
     *
     * Many-to-one relationship with FoodItem entity.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_item_id", nullable = false)
    @NotNull(message = "Food item is required")
    @NonNull
    private FoodItem foodItem;

    /**
     * Unit price of the food item at the time the order was placed.
     *
     * Snapshotted at order creation to preserve historical pricing.
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
     * Automatically set during persistence and not updatable.
     */
    @Column(name = "created_on", updatable = false)

    private Instant createdOn;

    /**
     * Timestamp when the order item was last updated.
     */
    @Column(name = "updated_on")

    private Instant updatedOn;

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

}