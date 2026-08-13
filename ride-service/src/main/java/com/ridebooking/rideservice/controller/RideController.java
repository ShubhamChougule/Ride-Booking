package com.ridebooking.rideservice.controller;


import com.ridebooking.rideservice.dto.RideRequest;
import com.ridebooking.rideservice.dto.RideResponse;
import com.ridebooking.rideservice.service.RideService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rides")
@Slf4j
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;

    public ResponseEntity<RideResponse> requestRide(@RequestBody RideRequest rideRequest) {

        return null;
    }
}
