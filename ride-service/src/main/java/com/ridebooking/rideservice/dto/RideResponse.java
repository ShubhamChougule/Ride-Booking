package com.ridebooking.rideservice.dto;

import com.ridebooking.rideservice.model.RideStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideResponse {
    private String id;
    private String driverId;
    private String rideId;
    private double pickupLongitude;
    private double pickupLatitude;
    private String pickUpAddress;
    private double dropLongitude;
    private double dropLatitude;
    private String dropAddress;
    private RideStatus rideStatus;
    private double estimatedFare;
    private double actualFare;
    private LocalDateTime rideStartedAt;
    private LocalDateTime rideCompletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
