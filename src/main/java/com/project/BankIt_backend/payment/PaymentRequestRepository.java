package com.project.BankIt_backend.payment;

import com.project.BankIt_backend.common.enums.RequestStatus;
import com.project.BankIt_backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface PaymentRequestRepository extends JpaRepository<PaymentRequest, Long> {

    List<PaymentRequest> findAllByRequestedFrom(User user);

    List<PaymentRequest> findAllByStatus(RequestStatus status);

    List<PaymentRequest> findAllByRequestedFromAndStatus(User requestedFrom, RequestStatus status);

    List<PaymentRequest> findAllByRequester(User requester);

    long countByStatusAndRequester_UserId(RequestStatus status, Long userId);
}
