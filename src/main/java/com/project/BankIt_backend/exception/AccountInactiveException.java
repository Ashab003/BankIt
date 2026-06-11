package com.project.BankIt_backend.exception;

public class AccountInactiveException
        extends RuntimeException {

    public AccountInactiveException(String message) {
        super(message);
    }
}
