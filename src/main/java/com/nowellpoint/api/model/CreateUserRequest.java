package com.nowellpoint.api.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import lombok.Getter;

@Getter
public class CreateUserRequest {
	
	@Email
	private String email;
	
	private String firstName;
	
	@NotEmpty
	private String lastName;
	
	@NotEmpty
	private String phone;
	
	@NotEmpty
	private String countryCode;
	
	@NotEmpty(message="A valid time zone must be provided. Valid time zones at this time are: America/New_York")
	private String timeZone;
}