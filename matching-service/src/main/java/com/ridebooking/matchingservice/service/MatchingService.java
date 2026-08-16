package com.ridebooking.matchingservice.service;

import com.ridebooking.matchingservice.client.LocationServiceClient;
import com.ridebooking.matchingservice.dto.NearbyDriverResponse;
import com.ridebooking.matchingservice.event.RideMatchedEvent;
import com.ridebooking.matchingservice.event.RideRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MatchingService {

    private final LocationServiceClient locationServiceClient;
    public static final String RIDE_MATCHED_TOPIC = "ride.matched";
    public static final double RADIUS = 5.0;
    private final KafkaTemplate<String, RideMatchedEvent> kafkaTemplate;


    public void matchNearbyDriver(RideRequestedEvent event) {
        List<NearbyDriverResponse> nearbyDrivers = locationServiceClient.getNearbyDrivers(
                event.getPickupLatitude(),
                event.getPickupLongitude(),
                RADIUS
        );

        Optional<NearbyDriverResponse> nearbyDriverResponse = findBestDriver(nearbyDrivers);

        if (nearbyDriverResponse.isEmpty()) {
            log.warn("No Driver found in nearby area");
            return;
        }

        NearbyDriverResponse driver = nearbyDriverResponse.get();

        RideMatchedEvent rideMatchedEvent = RideMatchedEvent.builder()
                .rideId(event.getRideId())
                .riderId(event.getRiderId())
                .driverLatitude(driver.getLatitude())
                .driverLongitude(driver.getLongitude())
                .distanceToPickup(driver.getDistanceInKm())
                .build();

        kafkaTemplate.send(RIDE_MATCHED_TOPIC, event.getRideId(), rideMatchedEvent);
        log.info("Event Published to kafka topic : {}, Id : {}, Event Ride Matched : {}", RIDE_MATCHED_TOPIC, event.getRideId(), rideMatchedEvent);
    }


    /**
     * Find Nearest driver
     * algo -> 70% distance & 30% rating
     * <p>
     * Score = ( 1 / distance ) * distanceWeight + rating * ratingWeight;
     */
    private Optional<NearbyDriverResponse> findBestDriver(List<NearbyDriverResponse> nearbyDrivers) {
        double distanceWeight = 0.7;
        double ratingWeight = 0.3;

        return nearbyDrivers.stream().max(Comparator.comparingDouble(driver -> {
            double distanceScore = 1.0 / (driver.getDistanceInKm() + 1.0);
            double rating = 4.0 + Math.random();

            return (distanceScore * distanceWeight) + (rating * ratingWeight);
        }));
    }

}
