package com.project.BankIt_backend.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncomingRequest_ResponseDTO {

    Long requestId;
    String fullName;
    String note;
    LocalDateTime localDateTime;
    BigDecimal amount;
    String status;
}
