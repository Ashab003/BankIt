package com.project.BankIt_backend.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyTransactionResponseDTO {
    private String receiverUsername;
    private BigDecimal amount;
    private LocalDateTime localDateTime;
}
