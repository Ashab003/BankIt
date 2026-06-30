package com.project.BankIt_backend.kafka_.producer;

import com.project.BankIt_backend.kafka_.config.KafkaTopicConfig;
import com.project.BankIt_backend.kafka_.event.TransactionCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionEventProducer {

    private final KafkaTemplate<String, TransactionCompletedEvent> kafkaTemplate;

    public void publishTransaction(TransactionCompletedEvent event) {

        log.info("Publishing transaction {} to Kafka",
                event.getTransactionId());

        kafkaTemplate.send(
                KafkaTopicConfig.TRANSACTION_TOPIC,
                event.getTransactionId().toString(),
                event
        );
    }
}
