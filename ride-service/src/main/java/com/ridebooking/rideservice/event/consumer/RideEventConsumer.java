package com.ridebooking.rideservice.event.consumer;

import com.ridebooking.rideservice.event.RideMatchedEvent;
import com.ridebooking.rideservice.service.RideService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RideEventConsumer {

    private final RideService rideService;

    @KafkaListener(
            topics = "ride.matched",
            groupId = "ride-service-group")
    public void rideMatchedEventListener(RideMatchedEvent event) {
        try {
            log.info("Ride Matched Event Received for Ride Id : {} || Driver Id : {}", event.getRideId(), event.getDriverId());
            rideService.updateRideWithDriver(event.getRideId(), event.getDriverId());
            log.info("Ride Matched Event Consumed for Ride Id : {} || Driver Id : {}", event.getRideId(), event.getDriverId());
        } catch (Exception ex) {
            log.error("Error occurred while processing ride request : {}", event.getRideId());
        }
    }
}