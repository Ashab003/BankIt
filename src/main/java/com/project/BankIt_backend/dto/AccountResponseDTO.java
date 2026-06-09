package com.project.BankIt_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AccountResponseDTO {

    private String accountNo;
    private BigDecimal balance;
    private String currency;
    private String status;
}