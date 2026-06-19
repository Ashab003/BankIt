package com.project.BankIt_backend.transaction;

/*
Name              Null?    Type
----------------- -------- -------------
TRANSACTIONID     NOT NULL NUMBER
SENDERACCOUNTID   NOT NULL NUMBER
RECEIVERACCOUNTID NOT NULL NUMBER
AMOUNT            NOT NULL NUMBER(18,2)
TRANSACTIONTYPE   NOT NULL VARCHAR2(50)
STATUS                     VARCHAR2(20)
REFERENCENUMBER            VARCHAR2(100)
DESCRIPTION                VARCHAR2(500)
TRANSACTIONDATE            TIMESTAMP(6)

    */

import com.project.BankIt_backend.account.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "TRANSACTION")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TRANSACTIONID")
    private Long transactionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SENDERACCOUNTID", nullable = false)
    private Account senderAccount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RECEIVERACCOUNTID", nullable = false)
    private Account receiverAccount;

    @Column(name = "AMOUNT")
    private BigDecimal amount;

    @Column(name = "TRANSACTIONTYPE")
    private String transactionType;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "REFERENCENUMBER")
    private String referenceNumber;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "TRANSACTIONDATE")
    private LocalDateTime transactionDate;

}
