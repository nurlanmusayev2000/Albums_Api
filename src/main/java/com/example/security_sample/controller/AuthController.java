package com.example.security_sample.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.security_sample.model.Account;
import com.example.security_sample.payload.auth.AccountDTO;
import com.example.security_sample.payload.auth.AccountViewDto;
import com.example.security_sample.payload.auth.ProfileDTO;
import com.example.security_sample.payload.auth.TokenDTO;
import com.example.security_sample.payload.auth.UserLoginDTO;
import com.example.security_sample.service.AccountService;
import com.example.security_sample.service.TokenService;
import com.example.security_sample.utils.constants.AccountError;
import com.example.security_sample.utils.constants.AccountSucces;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/auth")
@Tag(name = "Auth Controller", description = "controller for account management")
@Slf4j
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final TokenService tokenService;

	@Autowired
	AccountService accountService;

	public AuthController(AuthenticationManager authenticationManager, TokenService tokenService) {
		this.authenticationManager = authenticationManager;
		this.tokenService = tokenService;
	}


	@PostMapping("/token")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<TokenDTO> token(@Valid @RequestBody UserLoginDTO userLogin)
			throws AuthenticationException {
				System.out.println("in");
				try {
			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(userLogin.getEmail(), userLogin.getPassword()));
			return ResponseEntity.ok(new TokenDTO(tokenService.generateToken(authentication)));
		} catch (Exception e) {
			log.debug(AccountError.TOKEN_GENERATOR_ERROR.toString() + " : " + e.getMessage());
			return new ResponseEntity<>(new TokenDTO(null), HttpStatus.BAD_REQUEST);
		}
	}


	@PostMapping( value = "/add/user",consumes = "application/json" ,produces = "application/json")
	@ResponseStatus(HttpStatus.CREATED)
	@ApiResponse(responseCode = "201", description = "new user has creared")
	@ApiResponse(responseCode = "400", description = "please enter valid values")
	@Operation(summary = "add new user")
	public ResponseEntity<String> addUser(@Valid @RequestBody AccountDTO accountDTO) {
		try {
			Account account = new Account();
			account.setEmail(accountDTO.getEmail());
			account.setPassword(accountDTO.getPassword());
			accountService.save(account);
			return ResponseEntity.ok(AccountSucces.ACCOUNT_ADDED.toString());
		} catch (Exception e) {
			log.debug(AccountError.TOKEN_GENERATOR_ERROR.toString() + " : " + e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("null");
		}
	}

	@GetMapping(value = "/users", produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	@ApiResponse(responseCode = "200", description = "Users listed")
	@ApiResponse(responseCode = "404", description = "something went wrong")
	@Operation(summary = " get all users")
	@SecurityRequirement(name = "studyeasy-demo-api")
	public List<AccountViewDto> listOfUsers() {

		List<AccountViewDto> acc = new ArrayList<>();
		for (Account account : accountService.findAll()) {
			acc.add(new AccountViewDto(account.getId(), account.getEmail(), account.getRole()));
		}

		return acc;
	}

	@GetMapping(value = "/profile", produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	@ApiResponse(responseCode = "200", description = "Users listed")
	@ApiResponse(responseCode = "404", description = "something went wrong")
	@Operation(summary = " get authonticated users")
	@SecurityRequirement(name = "studyeasy-demo-api")
	public ProfileDTO profile(Authentication authentication) {

		String email = authentication.getName();

		Optional<Account> acc = accountService.findByEmail(email);
		if (acc.isPresent()) {
			Account account = acc.get();
			ProfileDTO profileDTO = new ProfileDTO(account.getId(), account.getEmail(), account.getRole());
			return profileDTO;
		}
		return null;
	}

}
