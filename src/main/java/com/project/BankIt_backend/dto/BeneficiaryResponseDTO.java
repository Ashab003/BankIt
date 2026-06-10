package com.project.BankIt_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiaryResponseDTO {

    private Long beneficiaryId;

    private String beneficiaryName;

    private String beneficiaryAccountNumber;

    private LocalDateTime createdAt;
}