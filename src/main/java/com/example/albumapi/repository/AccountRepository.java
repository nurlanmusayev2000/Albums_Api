package com.example.security_sample.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.security_sample.model.Account;


@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
	Optional<Account> findByEmail(String email);
}
