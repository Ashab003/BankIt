package com.project.BankIt_backend.kafka_.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

public class KafkaTopicConfig {

    public static final String TRANSACTION_TOPIC = "transaction-events";

    @Bean
    public NewTopic transactionTopic() {

        return TopicBuilder
                .name(TRANSACTION_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
