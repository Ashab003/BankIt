package com.project.BankIt_backend.common.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String message) {
        System.out.println("\nACCOUNT_NOT_FOUND\n");

        super(message);
    }
}
