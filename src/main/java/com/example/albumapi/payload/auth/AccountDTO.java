package com.example.security_sample.payload.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AccountDTO {

	@Email
	@Schema(description = "email address" , example = "email@mail.com" , requiredMode = RequiredMode.REQUIRED)
	private String email;

	@Size(min = 6, max = 20)
	@Schema(description = "Password" , minLength = 6,maxLength = 20 , example = "password12345" , requiredMode = RequiredMode.REQUIRED)
	private String password;
}
