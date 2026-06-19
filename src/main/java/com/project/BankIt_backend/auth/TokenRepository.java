package com.project.BankIt_backend.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query("""
    SELECT t
    FROM Token t
    WHERE t.user.userId = :userId
      AND t.expired = false
      AND t.revoked = false
""")
    List<Token> getAllValidTokensByUser(Long userId);

    Optional<Token> findByToken(String Token);
}

