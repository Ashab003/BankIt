package com.project.BankIt_backend.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestStats_ResponseDTO {
    Long pendingCount;
    Long rejectCount;
    Long approveCount;
}
