package com.example.security_sample.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(
		info = @Info(
				title = "demo api",
				version = "Versions 1.0")
)
public class SwaggerConfig {

}
