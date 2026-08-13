package com.ridebooking.rideservice.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideRequest {

    @NotNull(message = "Rider Id is required")
    private String riderId;

    private double pickupLongitude;
    private double pickupLatitude;
    private String pickUpAddress;

    private double dropLongitude;
    private double dropLatitude;
    private String dropAddress;
}
