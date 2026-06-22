package com.project.BankIt_backend.common.exception;

public class BeneficiaryNotFoundException extends RuntimeException {
    public BeneficiaryNotFoundException(String message) {
        super(message);
    }
}
