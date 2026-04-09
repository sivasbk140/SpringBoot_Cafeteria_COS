package net.breezeware.springbootcafeteria.food.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

/**
 * Entity representing the mapping between a {@link FoodMenu} and a {@link FoodItem}.
 *
 * Acts as a junction table ({@code food_menu_items_map}) linking food items to menus.
 * Tracks an individual availability flag for each item within its menu context.
 * When a new mapping is persisted, {@code isAvailable} defaults to {@code true}.
 *
 * @author Siva
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "food_menu_items_map")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@ToString(exclude = {"menu", "foodItem"})
public class FoodMenuItemMap {

    /**
     * Unique identifier for this menu-item mapping.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The food menu this mapping belongs to.
     *
     * Many-to-one relationship with FoodMenu entity.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    @NotNull(message = "Menu is required")
    @NonNull
    private FoodMenu menu;

    /**
     * The food item linked to the menu via this mapping.
     *
     * Many-to-one relationship with FoodItem entity.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_item_id", nullable = false)
    @NotNull(message = "Food item is required")
    @NonNull
    private FoodItem foodItem;

    /**
     * Indicates whether this food item is available within this menu.
     *
     * Defaults to {@code true} on creation. Can be toggled independently
     * per menu without affecting the global food item record.
     */
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    /**
     * Timestamp when this mapping was created.
     *
     * Automatically set during persistence and not updatable.
     */
    @Column(name = "created_on", updatable = false)
    private Instant createdOn;

    /**
     * Timestamp when this mapping was last updated.
     */
    @Column(name = "updated_on")
    private Instant updatedOn;

    /**
     * Lifecycle callback executed before persisting the entity.
     *
     * Initializes createdOn and updatedOn timestamps, and defaults
     * {@code isAvailable} to {@code true} if not explicitly set.
     */
    @PrePersist
    protected void onCreate() {
        createdOn = Instant.now();
        updatedOn = Instant.now();
        if (isAvailable == null) {
            isAvailable = true;
        }
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