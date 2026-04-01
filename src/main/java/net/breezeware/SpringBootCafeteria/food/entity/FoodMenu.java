package net.breezeware.SpringBootCafeteria.food.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import net.breezeware.SpringBootCafeteria.food.enumeration.MenuDay;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "food_menu", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"category", "menu_day"})
})
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@ToString(exclude = "menuItems")
public class FoodMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Category is required")
    @NonNull
    @Column(nullable = false, length = 50)
    private String category;  // BREAKFAST, LUNCH, DINNER

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Menu day is required")
    @NonNull
    @Column(name = "menu_day", nullable = false)
    private MenuDay menuDay;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FoodMenuItemMap> menuItems = new ArrayList<>();

    @Column(name = "created_on", updatable = false)
    private LocalDateTime createdOn;

    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    @PrePersist
    protected void onCreate() {
        createdOn = LocalDateTime.now();
        updatedOn = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedOn = LocalDateTime.now();
    }

    public void addMenuItem(FoodMenuItemMap menuItem) {
        menuItems.add(menuItem);
        menuItem.setMenu(this);
    }

    public void removeMenuItem(FoodMenuItemMap menuItem) {
        menuItems.remove(menuItem);
        menuItem.setMenu(null);
    }
}