package com.ridebooking.rideservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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