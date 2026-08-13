package com.ridebooking.rideservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "rides")
@AllArgsConstructor
@NoArgsConstructor
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private String driverId;

    @Column(nullable = false)
    private String rideId;

    @Column(nullable = false)
    private double pickupLongitude;

    @Column(nullable = false)
    private double pickupLatitude;


    private String pickUpAddress;

    @Column(nullable = false)
    private double dropLongitude;

    @Column(nullable = false)
    private double dropLatitude;


    private String dropAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideStatus rideStatus;

    private double estimatedFare;
    private double actualFare;


    private LocalDateTime rideStartedAt;
    private LocalDateTime rideCompletedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
