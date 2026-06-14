package com.project.BankIt_backend.service;

import com.project.BankIt_backend.dto.MyTransactionResponseDTO;
import com.project.BankIt_backend.dto.PaginatedTransactionResponseDTO;
import com.project.BankIt_backend.dto.TransactionResponseDTO;
import com.project.BankIt_backend.entity.Transaction;
import com.project.BankIt_backend.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public List<MyTransactionResponseDTO> getAllTransactions() {
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
                .map(this::convertToMyTrTDTO)
                .toList();

    }

    //for pagination in front-end
    public PaginatedTransactionResponseDTO getAllTransactions(
            int page,
            int size
    )
    {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by("transactionDate")
                                .descending()
                );

        System.out.println("PAGE = " + page);
        System.out.println("SIZE = " + size);
        System.out.println("USERNAME = " + username);

        Page<MyTransactionResponseDTO> transactionPage =
                transactionRepository
                        .findBySenderAccount_User_UsernameOrReceiverAccount_User_Username(
                                username,
                                username,
                                pageable
                        )
                        .map(this::convertToMyTrTDTO);

        return new PaginatedTransactionResponseDTO(
                transactionPage.getContent(),
                transactionPage.getNumber(),
                transactionPage.getTotalPages(),
                !transactionPage.isLast(),
                transactionPage.getTotalElements()
        );
    }

    public TransactionResponseDTO getTransactionById(Long transactionId) {

        Transaction transaction = transactionRepository
                .findById(transactionId)
                .orElseThrow(() ->
                        new RuntimeException("Transaction not found"));

        return convertToTrDTO(transaction);
    }

    public TransactionResponseDTO convertToTrDTO(Transaction transaction){
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

    public MyTransactionResponseDTO convertToMyTrTDTO(Transaction transaction){
        return new MyTransactionResponseDTO(
                transaction.getReceiverAccount().getUser().getFullName(),
                transaction.getAmount(),
                transaction.getTransactionDate()
        );
    }

}
