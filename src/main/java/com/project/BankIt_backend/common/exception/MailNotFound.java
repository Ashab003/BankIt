package com.project.BankIt_backend.common.exception;

public class MailNotFound extends RuntimeException {
    public MailNotFound(String message) {
        super(message);
    }
}
