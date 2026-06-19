package com.project.BankIt_backend.beneficiary.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BeneficiaryAddRequestDTO {

    @NotBlank(message = "Account number is required")
    private String accountNumber;

}