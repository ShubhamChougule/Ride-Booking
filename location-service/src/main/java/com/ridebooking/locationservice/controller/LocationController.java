package com.ridebooking.locationservice.controller;

import com.ridebooking.locationservice.dto.DriverLocationRequest;
import com.ridebooking.locationservice.dto.NearbyDriverResponse;
import com.ridebooking.locationservice.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/locations")
@Slf4j
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @PatchMapping("/drivers/update")
    public ResponseEntity<String> updateDriverLocation(@RequestBody DriverLocationRequest driverLocationRequest) {
        locationService.updateDriverLocation(driverLocationRequest);

        return ResponseEntity.ok("Driver Location updated");
    }


    @GetMapping("/drivers/nearby")
    public ResponseEntity<List<NearbyDriverResponse>> updateDriverLocation(@RequestParam double latitude,
    @RequestParam double longitude, @RequestParam double radius
                                                       ) {
        List<NearbyDriverResponse> driverList = locationService.findNearbyDriver(latitude, longitude, radius);

        return ResponseEntity.ok(driverList);
    }


    @GetMapping("/drivers/{driverId}")
    public ResponseEntity<String> updateDriverLocation(@PathVariable String driverId) {
        locationService.removeDriver(driverId);

        return ResponseEntity.ok("Driver removed..");
    }
}
