package com.project.BankIt_backend.controller;

import com.project.BankIt_backend.dto.TransferRequestDTO;
import com.project.BankIt_backend.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final PaymentService paymentService;

    @PostMapping("/transfer")
    public ResponseEntity<String> transferMoney(
            @Valid @RequestBody TransferRequestDTO transferRequestDTO) {

        paymentService.transferMoney(transferRequestDTO);

        return ResponseEntity.ok("Transfer completed successfully");
    }
}