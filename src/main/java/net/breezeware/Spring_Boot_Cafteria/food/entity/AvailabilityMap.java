package net.breezeware.Spring_Boot_Cafteria.food.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.breezeware.Spring_Boot_Cafteria.food.enumeration.MenuDay;


import java.util.Date;



@Entity
@Table(name = "availability_map")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "menu_id", nullable = false)
    @OneToMany(fetch = FetchType.LAZY)
    private FoodMenu menuId;


    @Enumerated(EnumType.STRING)
    @Column(name = "menuDay",nullable = false)
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