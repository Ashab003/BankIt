package com.project.BankIt_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponseDTO {

    private Long transactionId;

    private String senderAccountNumber;

    private String receiverAccountNumber;

    private BigDecimal amount;

    private String transactionType;

    private String status;

    private String referenceNumber;

    private String description;

    private LocalDateTime transactionDate;
}