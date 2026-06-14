package com.project.BankIt_backend.dto;

import com.project.BankIt_backend.dto.MyTransactionResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedTransactionResponseDTO {

    private List<MyTransactionResponseDTO> transactions;

    private int currentPage;

    private int totalPages;

    private boolean hasNext;

    private long totalRecords;
}