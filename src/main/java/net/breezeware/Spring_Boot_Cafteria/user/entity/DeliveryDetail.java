package net.breezeware.Spring_Boot_Cafteria.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Entity
@Table(name = "Delivery_Details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Column(nullable = false)
    private String email;


    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    @NotBlank
    @Column(name = "location", nullable = false)
    private String location;

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