package com.nowellpoint.api.model;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

import lombok.Getter;

@Getter
public class CreateUserRequest {
	private String email;
	private String firstName;
	private String lastName;
	private String phone;
	private String timeZone;
	
	@JsonbCreator
    public CreateUserRequest(
    		@JsonbProperty("email") String email,
    		@JsonbProperty("firstName") String firstName,
    		@JsonbProperty("lastName") String lastName,
    		@JsonbProperty("phone") String phone,
    		@JsonbProperty("timeZone") String timeZone) {
		
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.timeZone = timeZone;
    }
}