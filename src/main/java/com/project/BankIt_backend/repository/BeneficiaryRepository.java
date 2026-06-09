package com.project.BankIt_backend.repository;

import com.project.BankIt_backend.entity.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {
    	Optional<Beneficiary> findByUser_userId(Long userId);

}
