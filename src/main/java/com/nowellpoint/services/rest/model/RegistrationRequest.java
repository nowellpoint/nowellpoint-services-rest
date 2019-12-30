package com.nowellpoint.services.rest.model;

import java.util.Locale;
import java.util.TimeZone;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import lombok.Getter;

@Getter
public class RegistrationRequest {
	
	@Email 
	private String email;
	
	private String givenName;
	
	@NotEmpty 
	private String familyName;
	
	private String phone;
	
	@NotEmpty 
	private String countryCode;
	
	@NotEmpty(message="A valid time zone must be provided. Valid time zones at this time are: America/New_York")
	private String timeZone = TimeZone.getDefault().getID();
	
	@NotEmpty
	private String companyName;
	
	@NotEmpty
	private String locale = Locale.getDefault().toString();
}