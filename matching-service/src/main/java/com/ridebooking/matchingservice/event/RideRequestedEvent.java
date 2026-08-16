package com.ridebooking.matchingservice.event;


import lombok.Data;

/**
 *  Event consumed from kafka topic ride.requested
 *  published by ride service when rider request a ride
 */

@Data
public class RideRequestedEvent {

    private Long rideId;
    private String riderId;
    private double pickupLongitude;
    private double pickupLatitude;
    private String pickUpAddress;

    private double dropLongitude;
    private double dropLatitude;
    private String dropAddress;
}
