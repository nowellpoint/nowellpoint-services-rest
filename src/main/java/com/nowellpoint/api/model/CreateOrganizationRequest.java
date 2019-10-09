package com.nowellpoint.api.model;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

import lombok.Getter;

@Getter
public class CreateOrganizationRequest {
	private String clientId;
	private String clientSecret;
	private String username;
	private String password;
	
	@JsonbCreator
	public CreateOrganizationRequest(
			@JsonbProperty("clientId") String clientId,
			@JsonbProperty("clientSecret") String clientSecret,
			@JsonbProperty("username") String username,
			@JsonbProperty("password") String password) {
		
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.username = username;
		this.password = password;
	}
}