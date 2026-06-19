package com.project.BankIt_backend.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponseDTO {

    private BigDecimal totalBalance;

    private Long beneficiaryCount;

    private Long transactionsThisMonth;

    private BigDecimal moneySentThisMonth;

    private BigDecimal moneyReceivedThisMonth;
}