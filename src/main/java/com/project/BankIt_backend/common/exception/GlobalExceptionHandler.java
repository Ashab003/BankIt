package com.project.BankIt_backend.common.exception;

import com.project.BankIt_backend.common.exception.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // DTO Validation Errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        ));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        new ErrorResponse(
                                "INTERNAL_SERVER_ERROR",
                                "An unexpected error occurred"
                        )
                );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        new ErrorResponse(
                                "BAD_CREDENTIALS",
                                ex.getMessage()
                        )
                );
    }

    @ExceptionHandler(AccountInactiveException.class)
    public ResponseEntity<ErrorResponse> handleAccountInactive(
            AccountInactiveException ex) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(
                        new ErrorResponse(
                                "ACCOUNT_INACTIVE",
                                ex.getMessage()
                        )
                );
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> accountNotFound(
            AccountNotFoundException ex
    ){

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        new ErrorResponse(
                                "ACCOUNT_NOT_FOUND",
                                ex.getMessage()
                        )
                );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> illegalArgument(
            IllegalArgumentException ex
    ){

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        new ErrorResponse(
                                "BAD_REQUEST",
                                ex.getMessage()
                        )
                );
    }

    @ExceptionHandler(InvalidRole.class)
    public ResponseEntity<ErrorResponse> invalidRole(
            InvalidRole ex
    ){

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        new ErrorResponse(
                                "INVALID_ROLE",
                                ex.getMessage()
                        )
                );
    }

    @ExceptionHandler(BeneficiaryAlreadyExists.class)
    public ResponseEntity<ErrorResponse> beneficiaryAlreadyExists(
            BeneficiaryAlreadyExists ex
    ) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        new ErrorResponse(
                                "BENEFICIARY_ALREADY_EXISTS",
                                ex.getMessage()
                        )
                );
    }

    @ExceptionHandler(BeneficiaryUnauthorizedAccessException.class)
    public ResponseEntity<ErrorResponse> beneficiaryAccessDenied(
            BeneficiaryUnauthorizedAccessException ex
    ){

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(
                        new ErrorResponse(
                                "BENEFICIARY_ACCESS_DENIED",
                                ex.getMessage()
                        )
                );
    }

    @ExceptionHandler(InvalidBeneficiaryException.class)
    public ResponseEntity<ErrorResponse> invalidBeneficiary(
            InvalidBeneficiaryException ex
    ){

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        new ErrorResponse(
                                "INVALID_BENEFICIARY",
                                ex.getMessage()
                        )
                );
    }

    @ExceptionHandler(BeneficiaryNotFoundException.class)
    public ResponseEntity<ErrorResponse> beneficiaryNotFound(
            BeneficiaryNotFoundException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        new ErrorResponse(
                                "BENEFICIARY_NOT_FOUND",
                                ex.getMessage()
                        )
                );
    }


}