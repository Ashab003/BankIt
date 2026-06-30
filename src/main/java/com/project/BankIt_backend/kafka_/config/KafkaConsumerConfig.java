package com.project.BankIt_backend.kafka_.config;

import com.project.BankIt_backend.kafka_.event.TransactionCompletedEvent;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, TransactionCompletedEvent> consumerFactory(
            KafkaProperties properties) {

        return new DefaultKafkaConsumerFactory<>(
                properties.buildConsumerProperties()
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TransactionCompletedEvent> kafkaListenerContainerFactory(
            ConsumerFactory<String, TransactionCompletedEvent> consumerFactory
    ) {

        ConcurrentKafkaListenerContainerFactory<String, TransactionCompletedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);

        return factory;
    }
}