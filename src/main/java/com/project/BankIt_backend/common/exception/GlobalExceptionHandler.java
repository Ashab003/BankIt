package com.project.BankIt_backend.common.exception;

import com.project.BankIt_backend.common.exception.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.authentication.BadCredentialsException;

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
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {

        System.out.println("\nGENERIC HANDLER\n");

        ex.printStackTrace();

        System.out.println("\nEXCEPTION TYPE = " + ex.getClass().getName() + "\n");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        "INTERNAL_SERVER_ERROR",
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex) {

        System.out.println("\nBAD CREDENTIALS HANDLER\n");

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

        System.out.println("\nACCOUNT_NOT_FOUND\n");

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

        System.out.println("\nBENEFICIARY_ALREADY_EXISTS\n");
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        new ErrorResponse(
                                "BENEFICIARY_ALREADY_EXISTS",
                                ex.getMessage()
                        )
                );
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> otpCooldown(
            UsernameNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS) //429
                .body(
                        new ErrorResponse(
                                "OTP_COOLDOWN",
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
        System.out.println("\nINVALID_BENEFICAIRY_HIT\n");

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


    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ErrorResponse> unauthorizedAccess(
            UnauthorizedAccessException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        new ErrorResponse(
                                "UNAUTHORIZED_ACCESS",
                                ex.getMessage()
                        )
                );
    }

    @ExceptionHandler(MailNotFound.class)
    public ResponseEntity<ErrorResponse> unauthorizedAccess(
            MailNotFound ex
    ) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        new ErrorResponse(
                                "NOT_FOUND",
                                ex.getMessage()
                        )
                );
    }

    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<ErrorResponse> emailSendingException(
            EmailSendingException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        new ErrorResponse(
                                "EMAIL_SENDING_FAILED",
                                ex.getMessage()
                        )
                );
    }

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<ErrorResponse> invalidOtp(
            InvalidOtpException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        new ErrorResponse(
                                "INVALID_OTP",
                                ex.getMessage()
                        )
                );
    }

    @ExceptionHandler(OtpExpiredException.class)
    public ResponseEntity<ErrorResponse> invalidOtp(
            OtpExpiredException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.GONE)
                .body(
                        new ErrorResponse(
                                "OTP_EXPIRED",
                                ex.getMessage()
                        )
                );
    }

    @ExceptionHandler(OtpCooldownException.class)
    public ResponseEntity<ErrorResponse> otpCooldown(
            OtpCooldownException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND) //404
                .body(
                        new ErrorResponse(
                                "USER_NOT_FOUND",
                                ex.getMessage()
                        )
                );
    }


    @ExceptionHandler(InvalidTransferAmountException.class)
    public ResponseEntity<ErrorResponse> invalidTransferAmount(
            InvalidTransferAmountException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) //400
                .body(
                        new ErrorResponse(
                                "INVALID_TRANSFER_AMOUNT",
                                ex.getMessage()
                        )
                );
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> insufficientBalance(
            InsufficientBalanceException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) //400
                .body(
                        new ErrorResponse(
                                "INSUFFICIENT_BALANCE",
                                ex.getMessage()
                        )
                );
    }


    @ExceptionHandler(RequestNotFoundException.class)
    public ResponseEntity<ErrorResponse> insufficientBalance(
            RequestNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND) //404
                .body(
                        new ErrorResponse(
                                "REQUEST_NOT_FOUND",
                                ex.getMessage()
                        )
                );
    }

    @ExceptionHandler(RequestAlreadyProcessedException.class)
    public ResponseEntity<ErrorResponse> requestAlreadyProcessed(
            RequestAlreadyProcessedException ex
    ){
        return ResponseEntity
                .status(HttpStatus.CONFLICT) //409
                .body(
                        new ErrorResponse(
                                "REQUEST_NOT_FOUND",
                                ex.getMessage()
                        )
                );
    }
}