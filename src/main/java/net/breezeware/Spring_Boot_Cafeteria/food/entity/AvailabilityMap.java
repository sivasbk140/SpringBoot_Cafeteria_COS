package net.breezeware.Spring_Boot_Cafeteria.food.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import net.breezeware.Spring_Boot_Cafeteria.food.enumeration.MenuDay;
import java.util.Date;

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
    @NonNull
    private FoodMenu menu;

    @Enumerated(EnumType.STRING)
    @Column(name = "menu_day", nullable = false)
    @NotNull(message = "Menu day is required")
    @NonNull
    private MenuDay menuDay;

    @Column(name = "created_on", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    @Column(name = "updated_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn;

    @PrePersist
    protected void onCreate() {
        createdOn = new Date();
        updatedOn = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedOn = new Date();
    }
}