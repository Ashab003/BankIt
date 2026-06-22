package com.project.BankIt_backend.common.exception;

public class InvalidRole extends RuntimeException {
    public InvalidRole(String message) {
        super(message);
    }
}
