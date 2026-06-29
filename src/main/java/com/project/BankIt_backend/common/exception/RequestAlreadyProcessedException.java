package com.project.BankIt_backend.common.exception;

public class RequestAlreadyProcessedException extends RuntimeException {
    public RequestAlreadyProcessedException(String message) {
        super(message);
    }
}
