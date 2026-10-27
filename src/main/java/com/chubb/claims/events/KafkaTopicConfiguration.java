package com.chubb.claims.events;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfiguration {
    @Bean
    public NewTopic claimEventsTopic() {
        return TopicBuilder.name("claim-events").partitions(1).replicas(1).build();
    }
}
