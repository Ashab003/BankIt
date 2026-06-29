package com.project.BankIt_backend.auth.dto;

import com.project.BankIt_backend.common.enums.OTPVerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyOtpResponseDTO {
    private OTPVerificationStatus status;
}
