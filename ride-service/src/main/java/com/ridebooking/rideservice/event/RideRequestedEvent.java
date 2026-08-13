package com.ridebooking.rideservice.event;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RideRequestedEvent {

    private String rideId;
    private String riderId;
    private double pickupLongitude;
    private double pickupLatitude;
    private String pickUpAddress;

    private double dropLongitude;
    private double dropLatitude;
    private String dropAddress;


}
