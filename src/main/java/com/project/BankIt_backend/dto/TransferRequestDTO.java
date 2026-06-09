package com.project.BankIt_backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequestDTO {

    private String recipientIdentifier;

    private BigDecimal amount;

    private String description;
}