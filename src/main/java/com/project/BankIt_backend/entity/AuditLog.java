package com.project.BankIt_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;

/*
Name        Null?    Type
----------- -------- -------------
LOGID       NOT NULL NUMBER
USERID      NOT NULL NUMBER
ACTION      NOT NULL VARCHAR2(200)
TIMESTAMP            TIMESTAMP(6)
DESCRIPTION          VARCHAR2(500)
 */

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "AUDIT_LOG")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOGID")
    private Long logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USERID")
    private User user;

    @NonNull
    @Column(name = "ACTION")
    private String action;

    @Column(name = "TIMESTAMP")
    private LocalDateTime timeStamp;

    @Column(name = "DESCRIPTION")
    private String description;
}
