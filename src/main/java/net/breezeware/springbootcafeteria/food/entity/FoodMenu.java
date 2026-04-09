package net.breezeware.springbootcafeteria.food.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import net.breezeware.springbootcafeteria.food.enumeration.MenuDay;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a daily food menu in the cafeteria system.
 *
 * Each menu is uniquely identified by its category (e.g. BREAKFAST, LUNCH, DINNER)
 * and the day of the week ({@link MenuDay}). A menu contains a list of
 * {@link FoodMenuItemMap} entries that link food items to this menu.
 * Timestamps are managed automatically via JPA lifecycle hooks.
 *
 * @author Siva
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "food_menu", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"category", "menu_day"})
})
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@ToString(exclude = "menuItems")
public class FoodMenu {

    /**
     * Unique identifier for the food menu.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Category of the menu (e.g. BREAKFAST, LUNCH, DINNER).
     *
     * Combined with {@code menuDay} as a unique constraint.
     */
    @NotBlank(message = "Category is required")
    @NonNull
    @Column(nullable = false, length = 50)
    private String category;  // BREAKFAST, LUNCH, DINNER

    /**
     * The day of the week this menu is scheduled for.
     *
     * Stored as a string representation of {@link MenuDay} enum.
     * Combined with {@code category} as a unique constraint.
     */
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Menu day is required")
    @NonNull
    @Column(name = "menu_day", nullable = false)
    private MenuDay menuDay;

    /**
     * List of food item mappings associated with this menu.
     *
     * One-to-many relationship with FoodMenuItemMap.
     * Cascade operations and orphan removal are enabled.
     */
    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FoodMenuItemMap> menuItems = new ArrayList<>();

    /**
     * Timestamp when the menu was created.
     *
     * Automatically set during persistence and not updatable.
     */
    @Column(name = "created_on", updatable = false)
    private Instant createdOn;

    /**
     * Timestamp when the menu was last updated.
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