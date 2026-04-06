package net.breezeware.SpringBootCafeteria.order.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import net.breezeware.SpringBootCafeteria.user.entity.User;

import net.breezeware.SpringBootCafeteria.order.enumeration.OrderStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Entity representing a customer order in the cafeteria system.
 *
 * <p>An Order is associated with a {@link User} and contains multiple
 * {@link OrderItem} entries. It maintains order status, timestamps,
 * and provides utility methods for order operations.</p>
 *
 * <p>This entity uses JPA annotations for ORM mapping and lifecycle
 * callbacks to manage creation and update timestamps.</p>
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
     * <p>Many-to-one relationship with User entity.</p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User is required")
    @NonNull
    private User user;

    /**
     * Current status of the order.
     *
     * <p>Stored as a string representation of {@link OrderStatus} enum.</p>
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Status is required")
    @NonNull
    private OrderStatus status;

    /**
     * List of items associated with this order.
     *
     * <p>One-to-many relationship with OrderItem.
     * Cascade operations are enabled and orphan removal is supported.</p>
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
     * <p>This field is automatically set during persistence and not updatable.</p>
     */
    @Column(name = "created_on", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    /**
     * Timestamp when the order was last updated.
     */
    @Column(name = "updated_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn;

    /**
     * Lifecycle callback executed before persisting the entity.
     *
     * <p>Initializes createdOn and updatedOn timestamps.</p>
     */
    @PrePersist
    protected void onCreate() {
        createdOn = new Date();
        updatedOn = new Date();
    }

    /**
     * Lifecycle callback executed before updating the entity.
     *
     * <p>Updates the updatedOn timestamp.</p>
     */
    @PreUpdate
    protected void onUpdate() {
        updatedOn = new Date();
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
