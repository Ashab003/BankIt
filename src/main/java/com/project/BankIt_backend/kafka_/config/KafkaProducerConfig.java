package com.project.BankIt_backend.kafka_.config;

import com.project.BankIt_backend.kafka_.event.TransactionCompletedEvent;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, TransactionCompletedEvent> producerFactory(KafkaProperties properties) {
        return new DefaultKafkaProducerFactory<>(properties.buildProducerProperties());
    }

    @Bean public KafkaTemplate<String, TransactionCompletedEvent> kafkaTemplate(
            ProducerFactory<String, TransactionCompletedEvent> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }

}
