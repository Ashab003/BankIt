package com.project.BankIt_backend.beneficiary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {

	Optional<Beneficiary> findByUser_userId(Long userId);

	List<Beneficiary> findByUser_UserId(Long userId);

	Optional<Beneficiary> findByUser_UserIdAndBeneficiaryAccount_AccountId(
			Long userId,
			Long accountId
	);

	Long countByUser_UserId(Long userId);

}