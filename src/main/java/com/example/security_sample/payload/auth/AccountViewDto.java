package com.example.security_sample.payload.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AccountViewDto {

	private Long id;

	private String email;

	private String role;

}
