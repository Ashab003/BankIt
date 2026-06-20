package com.project.BankIt_backend.payment.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SendRequestDTO {

        private String recipientIdentifier;

        private BigDecimal amount;

        private String note;

}
