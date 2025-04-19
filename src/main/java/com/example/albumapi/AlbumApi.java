package com.example.security_sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@SpringBootApplication
@SecurityScheme(name = "studyeasy-demo-api",scheme = "bearer",type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
public class SecuritySampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecuritySampleApplication.class, args);
	}

}
