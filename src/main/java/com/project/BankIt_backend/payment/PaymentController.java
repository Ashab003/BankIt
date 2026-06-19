package com.project.BankIt_backend.payment;

import com.project.BankIt_backend.transaction.dto.TransactionResponseDTO;
import com.project.BankIt_backend.transaction.dto.TransferRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponseDTO> transferMoney(
            @Valid @RequestBody TransferRequestDTO transferRequestDTO) {

        TransactionResponseDTO response = paymentService.transferMoney(transferRequestDTO);

        return ResponseEntity.ok(response);
    }
}
