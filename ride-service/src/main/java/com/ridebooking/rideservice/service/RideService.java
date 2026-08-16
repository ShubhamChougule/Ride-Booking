package com.ridebooking.rideservice.service;

import com.ridebooking.rideservice.dto.RideRequest;
import com.ridebooking.rideservice.dto.RideResponse;
import com.ridebooking.rideservice.event.RideRequestedEvent;
import com.ridebooking.rideservice.model.Ride;
import com.ridebooking.rideservice.model.RideStatus;
import com.ridebooking.rideservice.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class RideService {

    private final RideRepository rideRepository;
    private final KafkaTemplate<String, RideRequestedEvent> kafkaTemplate;

    private static final String RIDE_REQUESTED_TOPIC = "ride.requested";


    // create ride in db
    public RideResponse requestRide(RideRequest request) {
        log.info("New Ride request from rider : {}", request.getRiderId());

        Ride ride = Ride.builder()
                .riderId(request.getRiderId())
                .pickupLatitude(request.getPickupLatitude())
                .pickupLongitude(request.getPickupLongitude())
                .pickUpAddress(request.getPickUpAddress())
                .dropAddress(request.getDropAddress())
                .dropLatitude(request.getDropLatitude())
                .dropLongitude(request.getDropLongitude())
                .rideStatus(RideStatus.REQUESTED)
                .estimatedFare(calculateEstimatedFare(request))
                .build();

        Ride savedRide = rideRepository.save(ride);

        RideRequestedEvent event = RideRequestedEvent.builder()
                .riderId(savedRide.getRiderId())
                .rideId(savedRide.getRideId())
                .pickupLongitude(savedRide.getPickupLongitude())
                .pickupLatitude(savedRide.getPickupLatitude())
                .pickUpAddress(savedRide.getPickUpAddress())
                .dropAddress(savedRide.getDropAddress())
                .dropLatitude(savedRide.getDropLatitude())
                .dropLongitude(savedRide.getDropLongitude())
                .build();

        kafkaTemplate.send(RIDE_REQUESTED_TOPIC, event.getRideId(), event);

        log.info("Ride request event published to kafka ride id : {}", event.getRideId());

        savedRide.setRideStatus(RideStatus.MATCHING);
        rideRepository.save(savedRide);

        return mapToRideResponse(savedRide);
    }

    private RideResponse mapToRideResponse(Ride ride) {
        return RideResponse.builder()
                .rideId(ride.getRideId())
                .driverId(ride.getDriverId())
                .riderId(ride.getRiderId())
                .pickupLongitude(ride.getPickupLongitude())
                .pickupLatitude(ride.getPickupLatitude())
                .pickUpAddress(ride.getPickUpAddress())
                .dropLongitude(ride.getDropLongitude())
                .dropLatitude(ride.getDropLatitude())
                .dropAddress(ride.getDropAddress())
                .rideStatus(ride.getRideStatus())
                .estimatedFare(ride.getEstimatedFare())
                .actualFare(ride.getActualFare())
                .rideStartedAt(ride.getRideStartedAt())
                .rideCompletedAt(ride.getRideCompletedAt())
                .createdAt(ride.getCreatedAt())
                .updatedAt(ride.getUpdatedAt())
                .build();
    }


    public void updateRideWithDriver(String driverId, String rideId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(() ->
                new RuntimeException("No Ride found with given ride id"));

        ride.setDriverId(driverId);
        ride.setRideStatus(RideStatus.ACCEPTED);
        rideRepository.save(ride);
    }

    private double calculateEstimatedFare(RideRequest request) {
        // Simplified Haversine distance calculation formula

        double lat1 = Math.toRadians(request.getPickupLatitude());
        double lat2 = Math.toRadians(request.getDropLatitude());

        double lon1 = Math.toRadians(request.getPickupLongitude());
        double lon2 = Math.toRadians(request.getDropLongitude());

        double latDiff = lat1 - lat2;
        double lonDiff = lon1 - lon2;

        double a = Math.pow(Math.sin(latDiff / 2), 2) +
                Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(lonDiff / 2), 2);

        double c = Math.asin(Math.sqrt(a));
        double distanceInKm = 6371 * c;

        // base fare 50 + 12 per km

        double fare = 50 + (distanceInKm * 12);

        return Math.round(fare * 100.0) / 100.0;
    }

    public RideResponse getRideDetails(String rideId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(() ->
                new RuntimeException("No Ride found with given ride id"));
        return mapToRideResponse(ride);
    }

    public List<RideResponse> getAllRides(String riderId) {
        List<Ride> rideList = rideRepository.findByRiderIdOrderByCreatedAtDesc(riderId);

        return rideList.stream().map(this::mapToRideResponse).toList();
    }

    public RideResponse startRide(String rideId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(() ->
                new RuntimeException("No Ride found with given ride id"));

        if (ride.getRideStatus() != RideStatus.ACCEPTED) {
            throw new RuntimeException("Ride can not be started because current ride status is " + ride.getRideStatus());
        }

        ride.setRideStatus(RideStatus.RIDE_STARTED);
        ride.setRideStartedAt(LocalDateTime.now());
        return mapToRideResponse(rideRepository.save(ride));
    }

    public RideResponse completeRide(String rideId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(() ->
                new RuntimeException("No Ride found with given ride id"));

        if (ride.getRideStatus() != RideStatus.RIDE_STARTED) {
            throw new RuntimeException("Ride can not be ended because current ride status is " + ride.getRideStatus());
        }

        ride.setRideStatus(RideStatus.RIDE_COMPLETED);
        ride.setRideCompletedAt(LocalDateTime.now());
        ride.setActualFare(getActualFare(ride));
        return mapToRideResponse(rideRepository.save(ride));
    }

    private double getActualFare(Ride ride) {
        return 0;
    }


    public RideResponse cancelRide(String rideId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(() ->
                new RuntimeException("No Ride found with given ride id"));

        if (ride.getRideStatus() != RideStatus.RIDE_STARTED) {
            throw new RuntimeException("Ride can not be ended because current ride status is " + ride.getRideStatus());
        }

        ride.setRideStatus(RideStatus.RIDE_CANCELLED);
        return mapToRideResponse(rideRepository.save(ride));
    }

}
