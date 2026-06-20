package com.project.BankIt_backend.payment.dto;

import com.project.BankIt_backend.common.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutgoingRequest_ResponseDTO {
    private Long requestId;

    private String fullName;

    private BigDecimal amount;

    private RequestStatus status;

    private String note;

    private LocalDateTime createdAt;
}
