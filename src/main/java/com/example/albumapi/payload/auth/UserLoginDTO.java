package com.example.security_sample.payload.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserLoginDTO {

	@Email
	private String email;

	@Size(min = 6 , max = 20)
	private String password;

}
