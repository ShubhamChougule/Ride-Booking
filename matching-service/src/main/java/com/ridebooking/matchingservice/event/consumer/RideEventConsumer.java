package com.ridebooking.matchingservice.event.consumer;


import com.ridebooking.matchingservice.event.RideRequestedEvent;
import com.ridebooking.matchingservice.service.MatchingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RideEventConsumer {

    private final MatchingService matchingService;

    @KafkaListener(
            topics = "ride.requested",
            groupId = "matching-service-group")
    public void rideRequestedEvent(RideRequestedEvent event) {
        try {
            matchingService.matchNearbyDriver(event);
        } catch (Exception ex) {
            log.error("Error occurred while processing ride request : {}", event.getRideId());
        }
    }
}
