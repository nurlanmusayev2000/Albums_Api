package com.example.security_sample.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.security_sample.model.Account;
import com.example.security_sample.repository.AccountRepository;

@Service
public class AccountService implements UserDetailsService{
	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;



	public Account save(Account account) {
		account.setPassword(passwordEncoder.encode(account.getPassword()));
		if (account.getRole() == null) {
			account.setRole("ROLE_USER");
		}
		return accountRepository.save(account);
	}

	public Optional<Account> findByEmail(String email) {
		return accountRepository.findByEmail(email);
	}

	public List<Account> findAll() {
		return accountRepository.findAll();
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {


		Optional<Account> optionalAcc = accountRepository.findByEmail(email);
		if (!optionalAcc.isPresent()) {
			throw new UsernameNotFoundException("Email not found");
		}

		Account account = optionalAcc.get();

		List<GrantedAuthority> grantedAuthority = new ArrayList<>();
		grantedAuthority.add(new SimpleGrantedAuthority(account.getRole()));
		return new User(account.getEmail(), account.getPassword(), grantedAuthority);
	}

}
