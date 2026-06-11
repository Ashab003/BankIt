package com.project.BankIt_backend.controller;

import com.project.BankIt_backend.dto.MyTransactionResponseDTO;
import com.project.BankIt_backend.dto.TransactionResponseDTO;
import com.project.BankIt_backend.dto.TransferRequestDTO;
import com.project.BankIt_backend.entity.Transaction;
import com.project.BankIt_backend.service.PaymentService;
import com.project.BankIt_backend.service.TransactionService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/my-transactions")
    public ResponseEntity<List<MyTransactionResponseDTO>> getAllTransactions() {

        return ResponseEntity.ok(
                transactionService.getAllTransactions()
        );
    }



    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> getTransactionById(@Parameter Long transactionId) {

        return ResponseEntity.ok(
                transactionService.getTransactionById(transactionId)
        );
    }
}