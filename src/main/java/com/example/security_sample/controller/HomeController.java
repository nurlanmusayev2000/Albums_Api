package com.example.security_sample.controller;

import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class HomeController {

	@GetMapping("/")
	public String demo() {
		System.out.println("helo ");
		return "hello world";
	}

	@GetMapping("/test")
	@Tag(name = "Test", description = "The test api")
	@SecurityRequirement(name = "studyeasy-demo-api")
	public String test() {
		return "Test Api";
	}

}
