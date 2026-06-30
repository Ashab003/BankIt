package com.project.BankIt_backend.fraud_detection;

import com.project.BankIt_backend.common.enums.FraudRule;
import com.project.BankIt_backend.common.enums.FraudSeverity;
import com.project.BankIt_backend.common.enums.FraudStatus;
import com.project.BankIt_backend.transaction.Transaction;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "FRAUD_ALERT")
public class FraudAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ALERT_ID")
    private Long alertId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRANSACTION_ID", nullable = false)
    private Transaction transaction;

    @Enumerated(EnumType.STRING)
    @Column(name = "RULE_TRIGGERED", nullable = false)
    private FraudRule ruleTriggered;

    @Column(name = "DESCRIPTION")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "SEVERITY", nullable = false)
    private FraudSeverity severity;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private FraudStatus status;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;
}
