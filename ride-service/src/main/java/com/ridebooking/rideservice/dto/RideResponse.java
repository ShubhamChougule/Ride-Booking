package com.ridebooking.rideservice.dto;

import com.ridebooking.rideservice.model.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RideResponse {
    private String rideId;
    private String driverId;
    private String riderId;
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
