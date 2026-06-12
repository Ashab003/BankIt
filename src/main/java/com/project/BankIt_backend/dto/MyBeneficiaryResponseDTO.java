package com.project.BankIt_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyBeneficiaryResponseDTO {
    private Long beneficiaryId;
    private String FullName;
    private String Email;
    private String PhoneNumber;
    private String accountNumber;
}
