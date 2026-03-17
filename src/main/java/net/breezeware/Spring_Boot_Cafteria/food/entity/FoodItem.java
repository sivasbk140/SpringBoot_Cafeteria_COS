package net.breezeware.Spring_Boot_Cafteria.food.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Date;


@Entity
@Table(name="food_item")
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor

public class FoodItem
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "name",nullable = false)
    private String name;

    @NotBlank
    @Column(name = "price",nullable = false)
    private Double price;


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