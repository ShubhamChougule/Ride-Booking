package com.ridebooking.locationservice.service;

import com.ridebooking.locationservice.dto.DriverLocationRequest;
import com.ridebooking.locationservice.dto.NearbyDriverResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class LocationService {

    private static final String DRIVERS_GEO_KEY = "drivers:locations";
    private final RedisTemplate<String, String> redisTemplate;

    public void updateDriverLocation(DriverLocationRequest driverLocationRequest) {
        log.info("Updating driver location for driver : {}", driverLocationRequest.getDriverId());

        Point driverPoint = new Point(
                driverLocationRequest.getLongitude(),
                driverLocationRequest.getLatitude()
        );

        redisTemplate.opsForGeo().add(DRIVERS_GEO_KEY, driverPoint, driverLocationRequest.getDriverId());

        log.info("Driver {} Location updated to {}", driverLocationRequest.getDriverId(), driverPoint);
    }


    public List<NearbyDriverResponse> findNearbyDriver(double latitude, double longitude, double radius) {

        log.info("Finding nearby driver of location {} - {}, radius : {}", longitude, latitude, radius);

        List<NearbyDriverResponse> responses = new ArrayList<>();

        Circle searchArea = new Circle(new Point(longitude, latitude), new Distance(radius, Metrics.KILOMETERS));

        GeoResults<RedisGeoCommands.GeoLocation<String>> nearestDrivers = redisTemplate.opsForGeo().radius(DRIVERS_GEO_KEY, searchArea,
                RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                        .includeCoordinates()
                        .includeDistance()
                        .sortAscending()
                        .limit(10)
        );

        if (nearestDrivers != null) {
            nearestDrivers.getContent().forEach(item -> {
                responses.add(new NearbyDriverResponse(
                        item.getContent().getName(),
                        item.getContent().getPoint().getY(),
                        item.getContent().getPoint().getX(),
                        nearestDrivers.getAverageDistance().getValue()
                ));
            });
        }

        log.info("Found {} drivers within radius : {} ", nearestDrivers.getContent().size(), radius);

        return responses;
    }


    public void removeDriver(String driverId) {
        log.info("Removing driver {}", driverId);

        redisTemplate.opsForGeo().remove(DRIVERS_GEO_KEY, driverId);

        log.info("Removed driver {}", driverId);
    }
}
