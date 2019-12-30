package com.nowellpoint.services.rest.model;

import java.util.Locale;
import java.util.TimeZone;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@RegisterForReflection
public class UserRequest {
	@Email 
	private String email;
	
	private String firstName;
	
	@NotEmpty 
	private String lastName;
	
	@NotEmpty 
	private String phone;
	
	@NotEmpty(message="A valid time zone must be provided. Valid time zones at this time are: America/New_York")
	private @Builder.Default String timeZone = TimeZone.getDefault().getID();
	
	@NotEmpty
	private @Builder.Default String locale = Locale.getDefault().toString();
}