package net.breezeware.springbootcafeteria.order.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import net.breezeware.springbootcafeteria.user.entity.User;

import net.breezeware.springbootcafeteria.order.enumeration.OrderStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Entity representing a customer order in the cafeteria system.
 *
 * An Order is associated with a {@link User} and contains multiple
 * {@link OrderItem} entries. It maintains order status, timestamps,
 * and provides utility methods for order operations.
 *
 * This entity uses JPA annotations for ORM mapping and lifecycle
 * callbacks to manage creation and update timestamps.
 *
 * @author Siva
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "order_table")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@ToString(exclude = {"user", "items"})
public class Order {

    /**
     * Unique identifier for the order.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user who placed the order.
     *
     * Many-to-one relationship with User entity.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User is required")
    @NonNull
    private User user;

    /**
     * Current status of the order.
     *
     * Stored as a string representation of {@link OrderStatus} enum.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Status is required")
    @NonNull
    private OrderStatus status;

    /**
     * List of items associated with this order.
     *
     * One-to-many relationship with OrderItem.
     * Cascade operations are enabled and orphan removal is supported.
     */
    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<OrderItem> items = new ArrayList<>();

    /**
     * Timestamp when the order was created.
     *
     * This field is automatically set during persistence and not updatable.
     */
    @Column(name = "created_on", updatable = false)

    private Instant createdOn;

    /**
     * Timestamp when the order was last updated.
     */
    @Column(name = "updated_on")

    private Instant updatedOn;

    /**
     * Lifecycle callback executed before persisting the entity.
     *
     * Initializes createdOn and updatedOn timestamps.
     */
    @PrePersist
    protected void onCreate() {
        createdOn = Instant.now();
        updatedOn = Instant.now();
    }

    /**
     * Lifecycle callback executed before updating the entity.
     *
     * Updates the updatedOn timestamp.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedOn = Instant.now();
    }

    /**
     * Adds an item to the order and sets the bidirectional relationship.
     *
     * @param item the OrderItem to add
     *
     * @implNote Ensures both sides of the relationship are synchronized.
     */
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    /**
     * Calculates the total price of the order.
     *
     * @return total price as the sum of all item prices
     *
     * @implSpec Computed dynamically from OrderItem list.
     */
    public Double getTotalPrice() {
        return items.stream()
                .mapToDouble(OrderItem::getTotalPrice)
                .sum();
    }

    /**
     * Checks whether the order contains any items.
     *
     * @return true if no items are present, false otherwise
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Determines whether the order can be cancelled by the customer.
     *
     * @return true if the current status allows cancellation
     *
     * @see OrderStatus#isCancellable()
     */
    public boolean canBeCancelled() {
        return status.isCancellable();
    }

}
