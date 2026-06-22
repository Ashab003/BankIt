package com.project.BankIt_backend.common.exception;

public class BeneficiaryAlreadyExists extends RuntimeException {
    public BeneficiaryAlreadyExists(String message) {
        super(message);
    }
}
