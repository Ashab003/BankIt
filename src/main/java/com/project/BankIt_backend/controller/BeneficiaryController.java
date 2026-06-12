package com.project.BankIt_backend.controller;

import com.project.BankIt_backend.dto.BeneficiaryRequestDTO;
import com.project.BankIt_backend.dto.BeneficiaryResponseDTO;
import com.project.BankIt_backend.dto.MyBeneficiaryResponseDTO;
import com.project.BankIt_backend.entity.Beneficiary;
import com.project.BankIt_backend.service.BeneficiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/beneficiary")
@RequiredArgsConstructor
public class BeneficiaryController {
    private final BeneficiaryService beneficiaryService;

    @DeleteMapping("/{beneficiaryId}")
    public ResponseEntity<String> removeBeneficiary(
            @PathVariable Long beneficiaryId) {

        beneficiaryService.removeBeneficiary(beneficiaryId);

        return ResponseEntity.ok(
                "Beneficiary removed successfully");
    }

    @PostMapping("/add")
    public ResponseEntity<BeneficiaryResponseDTO>
    addBeneficiary(
            @RequestBody BeneficiaryRequestDTO dto) {

        return ResponseEntity.ok(
                beneficiaryService.addBeneficiary(dto)
        );
    }

    @GetMapping
    public ResponseEntity<List<MyBeneficiaryResponseDTO>>
    getAllListOfBeneficiary() {
        return ResponseEntity.ok(
                beneficiaryService.getMyBeneficiaries()
        );
    }

    @GetMapping("/{beneficiaryId}")
    public MyBeneficiaryResponseDTO getBeneficiary(
            @PathVariable Long beneficiaryId) {
        return beneficiaryService.getBeneficiary(beneficiaryId);
    }
}
