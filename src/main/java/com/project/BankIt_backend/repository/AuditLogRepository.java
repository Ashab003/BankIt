package com.project.BankIt_backend.repository;

import com.project.BankIt_backend.entity.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<Beneficiary, Long> {
}
