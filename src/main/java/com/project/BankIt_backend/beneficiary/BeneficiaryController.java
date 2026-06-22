package com.project.BankIt_backend.beneficiary;

import com.project.BankIt_backend.beneficiary.dto.BeneficiaryAddRequestDTO;
import com.project.BankIt_backend.beneficiary.dto.BeneficiaryAddResponseDTO;
import com.project.BankIt_backend.beneficiary.dto.BeneficiarySearchResponseDTO;
import com.project.BankIt_backend.beneficiary.dto.MyBeneficiaryResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/beneficiary")
@RequiredArgsConstructor
public class BeneficiaryController {
    private final BeneficiaryService beneficiaryService;

    @DeleteMapping("/{beneficiaryId}")
    public ResponseEntity<Void> removeBeneficiary(
            @PathVariable Long beneficiaryId) {

        beneficiaryService.removeBeneficiary(beneficiaryId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/add")
    public ResponseEntity<BeneficiaryAddResponseDTO> addBeneficiary(
            @RequestBody BeneficiaryAddRequestDTO dto) {

        BeneficiaryAddResponseDTO response =
                beneficiaryService.addBeneficiary(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<MyBeneficiaryResponseDTO>> getAllListOfBeneficiary() {
        return ResponseEntity.ok(
                beneficiaryService.getMyBeneficiaries()
        );
    }

    @GetMapping("/{beneficiaryId}")
    public MyBeneficiaryResponseDTO getBeneficiary(
            @PathVariable Long beneficiaryId) {
        return beneficiaryService.getBeneficiary(beneficiaryId);
    }

    @GetMapping("/search/{accountNumber}")
    public BeneficiarySearchResponseDTO findUserByAccountNumber(
            @PathVariable String accountNumber) {

        return beneficiaryService.findUserByAccountNumber(accountNumber);
    }
}
