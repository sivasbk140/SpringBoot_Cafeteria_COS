package net.breezeware.SpringBootCafeteria.food.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import net.breezeware.SpringBootCafeteria.food.enumeration.MenuDay;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a daily food menu in the cafeteria system.
 *
 * <p>Each menu is uniquely identified by its category (e.g. BREAKFAST, LUNCH, DINNER)
 * and the day of the week ({@link MenuDay}). A menu contains a list of
 * {@link FoodMenuItemMap} entries that link food items to this menu.
 * Timestamps are managed automatically via JPA lifecycle hooks.</p>
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
     * <p>Combined with {@code menuDay} as a unique constraint.</p>
     */
    @NotBlank(message = "Category is required")
    @NonNull
    @Column(nullable = false, length = 50)
    private String category;  // BREAKFAST, LUNCH, DINNER

    /**
     * The day of the week this menu is scheduled for.
     *
     * <p>Stored as a string representation of {@link MenuDay} enum.
     * Combined with {@code category} as a unique constraint.</p>
     */
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Menu day is required")
    @NonNull
    @Column(name = "menu_day", nullable = false)
    private MenuDay menuDay;

    /**
     * List of food item mappings associated with this menu.
     *
     * <p>One-to-many relationship with FoodMenuItemMap.
     * Cascade operations and orphan removal are enabled.</p>
     */
    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FoodMenuItemMap> menuItems = new ArrayList<>();

    /**
     * Timestamp when the menu was created.
     *
     * <p>Automatically set during persistence and not updatable.</p>
     */
    @Column(name = "created_on", updatable = false)
    private LocalDateTime createdOn;

    /**
     * Timestamp when the menu was last updated.
     */
    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    /**
     * Lifecycle callback executed before persisting the entity.
     *
     * <p>Initializes both createdOn and updatedOn timestamps.</p>
     */
    @PrePersist
    protected void onCreate() {
        createdOn = LocalDateTime.now();
        updatedOn = LocalDateTime.now();
    }

    /**
     * Lifecycle callback executed before updating the entity.
     *
     * <p>Refreshes the updatedOn timestamp.</p>
     */
    @PreUpdate
    protected void onUpdate() {
        updatedOn = LocalDateTime.now();
    }

    /**
     * Adds a food item mapping to this menu and sets the back-reference on the mapping.
     *
     * @param menuItem the {@link FoodMenuItemMap} to add to this menu
     */
    public void addMenuItem(FoodMenuItemMap menuItem) {
        menuItems.add(menuItem);
        menuItem.setMenu(this);
    }

    /**
     * Removes a food item mapping from this menu and clears the back-reference.
     *
     * @param menuItem the {@link FoodMenuItemMap} to remove from this menu
     */
    public void removeMenuItem(FoodMenuItemMap menuItem) {
        menuItems.remove(menuItem);
        menuItem.setMenu(null);
    }
}