package com.project.BankIt_backend.kafka_.consumer;

import com.project.BankIt_backend.fraud_detection.FraudDetectionService;
import com.project.BankIt_backend.kafka_.event.TransactionCompletedEvent;
import com.project.BankIt_backend.transaction.Transaction;
import com.project.BankIt_backend.transaction.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.project.BankIt_backend.kafka_.config.KafkaTopicConfig.TRANSACTION_TOPIC;


@Component
@RequiredArgsConstructor
@Slf4j
public class FraudDetectionConsumer {

    private final TransactionRepository transactionRepository;
    private final FraudDetectionService fraudDetectionService;

    @KafkaListener(topics = TRANSACTION_TOPIC)
    @Transactional
    public void consume(TransactionCompletedEvent transactionCompletedEvent) {

        Long transactionId = transactionCompletedEvent.getTransactionId();
        log.info("Received transaction event {}", transactionId);

        Transaction transaction =
                transactionRepository.findById(transactionId)
                        .orElseThrow(() ->
                                new IllegalArgumentException("Transaction not found"));

        fraudDetectionService.analyze(transaction);

    }
}
