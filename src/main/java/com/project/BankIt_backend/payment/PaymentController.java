package com.project.BankIt_backend.payment;

import com.project.BankIt_backend.payment.dto.*;
import com.project.BankIt_backend.user.User;
import com.project.BankIt_backend.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final UserService userService;

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponseDTO> transferMoney(
            @Valid @RequestBody TransferRequestDTO transferRequestDTO) {

        TransactionResponseDTO response = paymentService.transferMoney(transferRequestDTO);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/send-request")
    public ResponseEntity<SendRequest_ResponseDTO> sendRequest(
            @RequestBody SendRequestDTO requestDTO){

        User requestedUser =
                paymentService.sendRequest(requestDTO);

        SendRequest_ResponseDTO response =
                new SendRequest_ResponseDTO();

        response.setRequestedFromName(
                requestedUser.getFullName()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/load-incoming-req")
    public ResponseEntity<List<IncomingRequest_ResponseDTO>> loadIncomingRequest(){
        User currentUser =
                userService.getCurrentUser();

        return ResponseEntity.ok(
                paymentService.incomingRequest(currentUser)
        );
    }

    @PostMapping("/approve-request/{requestId}")
    public ResponseEntity<ApproveRequest_ResponseDTO> approveRequest(@PathVariable Long requestId){

        PaymentRequest request =
                paymentService.approveRequest(requestId);

        ApproveRequest_ResponseDTO response =
                new ApproveRequest_ResponseDTO();

        response.setReceiverUsername(request.getRequester().getFullName());

        return ResponseEntity.ok(response);

    }

    @PostMapping("/reject-request/{requestId}")
    public ResponseEntity<RejectRequest_ResponseDTO> rejectRequest(@PathVariable Long requestId){

        PaymentRequest request =
                paymentService.rejectRequest(requestId);

        RejectRequest_ResponseDTO response =
                new RejectRequest_ResponseDTO();

        response.setReceiverUsername(request.getRequester().getFullName());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats-count")
    public  ResponseEntity<RequestStats_ResponseDTO> statsOfRequestStatus(){

        RequestStats_ResponseDTO requestStatsResponseDTO = new RequestStats_ResponseDTO();
        requestStatsResponseDTO = paymentService.countOfStatus();

        return ResponseEntity.ok(requestStatsResponseDTO);
    }

    @GetMapping("/outgoing-request-lists")
    public ResponseEntity<List<OutgoingRequest_ResponseDTO>> loadOutgoingRequest(){


        return ResponseEntity.ok(
                paymentService.listOfOutgoingRequest()
        );
    }
}
