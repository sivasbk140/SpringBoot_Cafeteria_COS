package net.breezeware.Spring_Boot_Cafeteria.food.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import net.breezeware.Spring_Boot_Cafeteria.food.enumeration.MenuDay;
import java.time.LocalDateTime;

@Entity
@Table(name = "availability_map")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@ToString(exclude = "menu")
public class AvailabilityMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    @NotNull(message = "Menu is required")
    private FoodMenu menu;

    @Enumerated(EnumType.STRING)
    @Column(name = "menu_day", nullable = false)
    @NotNull(message = "Menu day is required")
    @NonNull
    private MenuDay menuDay;

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
}