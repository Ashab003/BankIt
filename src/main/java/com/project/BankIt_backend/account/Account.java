package com.project.BankIt_backend.account;

/*
 Name          Null?    Type
------------- -------- ------------
ACCOUNTID     NOT NULL NUMBER
USERID        NOT NULL NUMBER
ACCOUNTNUMBER NOT NULL VARCHAR2(30)
BALANCE                NUMBER(18,2)
CURRENCY               VARCHAR2(10)
STATUS                 VARCHAR2(20)
CREATEDATE              TIMESTAMP(6)
*/

import com.project.BankIt_backend.user.User;
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
@Table(name = "ACCOUNTS")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ACCOUNTID", unique = true, nullable = false)
    private Long accountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USERID")
    private User user;

    @Column(name = "ACCOUNTNUMBER")
    private String accountNo;

    @Column(name = "BALANCE")
    private BigDecimal balance;

    @Column(name = "CURRENCY")
    private String currency;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "CREATEDAT")
    private LocalDateTime createdAt;

}
