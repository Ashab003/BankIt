package com.project.BankIt_backend.beneficiary.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiaryAddResponseDTO {

    private Long accountId;

    private String beneficiaryName;

    private String beneficiaryAccountNumber;

    private LocalDateTime createdAt;
}