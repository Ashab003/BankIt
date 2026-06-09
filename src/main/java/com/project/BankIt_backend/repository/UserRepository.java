package com.project.BankIt_backend.repository;

import com.project.BankIt_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    Optional<User> findByUserId(Long userId);
    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameOrEmail(String username, String email);
}
