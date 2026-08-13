package com.ridebooking.rideservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

public class KafkaConfig {

    /*
    * Ride Service will receive RIDE request -> It will save it in DB and publish EVENT
    * to matching service
    *
    * PRODUCER ===> RIDE SERVICE
    * CONSUMER ===> MATCHING SERVICE
     */
    @Bean
    public NewTopic rideRequestedTopic() {
        return TopicBuilder.name("ride.requested")
                .partitions(3)
                .replicas(1)
                .build();
    }


    /*
     * Ride Service will subscribe to matching service EVENTS
     * to get matched result
     *
     * PRODUCER ===> MATCHING SERVICE
     * CONSUMER ===> RIDE SERVICE
     */
    @Bean
    public NewTopic rideMatchedTopic() {
        return TopicBuilder.name("ride.matched")
                .partitions(3)
                .replicas(1)
                .build();
    }

}
