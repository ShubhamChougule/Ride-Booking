package com.ridebooking.matchingservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event published by topic : ride.matched
 * publisher matching service
 * consumed by ride service to update the ride with assigned driver
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RideMatchedEvent {
    private String rideId;
    private String riderId;
    private String driverId;
    private double driverLongitude;
    private double driverLatitude;
    private double distanceToPickup;
}
