package com.ridebooking.rideservice.controller;


import com.ridebooking.rideservice.dto.RideRequest;
import com.ridebooking.rideservice.dto.RideResponse;
import com.ridebooking.rideservice.service.RideService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rides")
@Slf4j
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;

    @PostMapping("/request")
    public ResponseEntity<RideResponse> requestRide(@RequestBody RideRequest rideRequest) {
        log.info("Ride Requested : {}", rideRequest);

        RideResponse response = rideService.requestRide(rideRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/{rideId}")
    public ResponseEntity<RideResponse> getRide(@PathVariable Long rideId) {
        log.info("Get ride details of Ride Id : {}", rideId);

        RideResponse response = rideService.getRideDetails(rideId);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/{riderId}")
    public ResponseEntity<List<RideResponse>> getAllRides(@PathVariable Long riderId) {
        log.info("Fetching all rides of Rider : {}", riderId);

        List<RideResponse> response = rideService.getAllRides(riderId);

        return ResponseEntity.ok(response);
    }


    @PutMapping("/start/{rideId}")
    public ResponseEntity<RideResponse> startRide(@PathVariable Long rideId) {
        log.info("Starting the ride : {}", rideId);

        RideResponse response = rideService.startRide(rideId);

        return ResponseEntity.ok(response);
    }


    @PutMapping("/complete/{rideId}")
    public ResponseEntity<RideResponse> completeRide(@PathVariable Long rideId) {
        log.info("Completing the ride : {}", rideId);

        RideResponse response = rideService.completeRide(rideId);

        return ResponseEntity.ok(response);
    }


    @PutMapping("/cancel/{rideId}")
    public ResponseEntity<RideResponse> cancelRide(@PathVariable Long rideId) {
        log.info("Cancelling the ride : {}", rideId);

        RideResponse response = rideService.cancelRide(rideId);

        return ResponseEntity.ok(response);
    }

}
