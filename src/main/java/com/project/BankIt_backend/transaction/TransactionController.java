package com.project.BankIt_backend.transaction;

import com.project.BankIt_backend.transaction.dto.PaginatedTransactionResponseDTO;
import com.project.BankIt_backend.transaction.dto.TransactionResponseDTO;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    //used in showing transaction list in the frontend
    @GetMapping("/my-transactions")
    public ResponseEntity<PaginatedTransactionResponseDTO>
    getAllTransactions(
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "10")
            int size
    ) {

        return ResponseEntity.ok(
                transactionService.getAllTransactions(
                        page,
                        size
                )
        );
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> getTransactionById(@Parameter Long transactionId) {

        return ResponseEntity.ok(
                transactionService.getTransactionById(transactionId)
        );
    }

}