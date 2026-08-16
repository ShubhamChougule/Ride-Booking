package com.ridebooking.rideservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ridebooking.rideservice.model.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
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
