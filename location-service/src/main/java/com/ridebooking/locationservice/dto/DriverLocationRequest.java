package com.ridebooking.locationservice.dto;

import lombok.Data;

@Data
public class DriverLocationRequest {
    private String driverId;
    private double latitude;
    private double longitude;
}

