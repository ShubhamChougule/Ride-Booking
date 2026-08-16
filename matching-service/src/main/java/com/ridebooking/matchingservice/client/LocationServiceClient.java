package com.ridebooking.matchingservice.client;

import com.ridebooking.matchingservice.dto.NearbyDriverResponse;
import com.ridebooking.matchingservice.event.RideRequestedEvent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "location-service", url="${location.service.url}")
public interface LocationServiceClient {


    @GetMapping("api/v1/locations/drivers/nearby")
    List<NearbyDriverResponse> getNearbyDrivers(@RequestParam double latitude,
                                                @RequestParam double longitude,
                                                @RequestParam double radius);
}
