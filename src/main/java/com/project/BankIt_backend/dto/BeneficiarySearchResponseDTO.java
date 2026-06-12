package com.project.BankIt_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BeneficiarySearchResponseDTO {

    private Long userId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String accountNumber;
}