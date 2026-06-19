package com.project.BankIt_backend.statement;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/statement")
@RequiredArgsConstructor

public class StatementController {

    private final StatementService statementService;

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> downloadStatement(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) throws Exception {

        byte[] pdf =
                statementService.generateStatementPdf(
                        startDate,
                        endDate
                );

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=BankIt_Statement.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

}