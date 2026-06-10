package com.project.BankIt_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BeneficiaryRequestDTO {

    @NotBlank(message = "Account number is required")
    private String accountNumber;

}