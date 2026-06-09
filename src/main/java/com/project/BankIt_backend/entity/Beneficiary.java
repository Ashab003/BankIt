package com.project.BankIt_backend.entity;

/*Name                 Null?    Type
-------------------- -------- -------------
BENEFICIARYID        NOT NULL NUMBER
USERID               NOT NULL NUMBER
BENEFICIARYACCOUNTID NOT NULL NUMBER
NICKNAME                      VARCHAR2(100)
CREATEDAT                     TIMESTAMP(6)  */

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "BENEFICIARY")
public class Beneficiary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BENEFICIARYID")
    private Long beneficiaryId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USERID", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BENEFICIARYACCOUNTID", nullable = false)
    private Account beneficiaryAccount;

    @Column(name = "NICKNAME")
    private String nickName;

    @Column(name = "CREATEDAT")
    private LocalDateTime createdAt;
}
