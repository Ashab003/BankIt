package com.project.BankIt_backend.payment;

import com.project.BankIt_backend.common.enums.RequestStatus;
import com.project.BankIt_backend.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "PAYMENT_REQUESTS")
public class PaymentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUESTER_ID")
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUESTED_FROM_ID")
    private User requestedFrom;

    private BigDecimal amount;

    @Column(length = 500)
    private String note;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "RESPONDED_AT")
    private LocalDateTime respondedAt;
}
