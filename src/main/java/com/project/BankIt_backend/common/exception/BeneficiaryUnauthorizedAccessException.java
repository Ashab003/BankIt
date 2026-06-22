package com.project.BankIt_backend.common.exception;

public class BeneficiaryUnauthorizedAccessException extends RuntimeException {
    public BeneficiaryUnauthorizedAccessException(String message) {
        super(message);
    }
}
