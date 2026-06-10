package com.project.BankIt_backend.service;

import com.project.BankIt_backend.dto.TransactionResponseDTO;
import com.project.BankIt_backend.entity.Transaction;
import com.project.BankIt_backend.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;


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

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public List<TransactionResponseDTO> getAllTransactions() {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();


        return transactionRepository
                .findBySenderAccount_User_UsernameOrReceiverAccount_User_Username(
                        username,
                        username
                )
                .stream()
                .map(this::convertToDTO)
                .toList();

    }

    public TransactionResponseDTO getTransactionById(Long transactionId) {

        Transaction transaction = transactionRepository
                .findById(transactionId)
                .orElseThrow(() ->
                        new RuntimeException("Transaction not found"));

        return convertToDTO(transaction);
    }

    public TransactionResponseDTO convertToDTO(Transaction transaction){
        return new TransactionResponseDTO(
                transaction.getTransactionId(),
                transaction.getSenderAccount().getAccountNo(),
                transaction.getReceiverAccount().getAccountNo(),
                transaction.getAmount(),
                transaction.getTransactionType(),
                transaction.getStatus(),
                transaction.getReferenceNumber(),
                transaction.getDescription(),
                transaction.getTransactionDate()
        );
    }
}
