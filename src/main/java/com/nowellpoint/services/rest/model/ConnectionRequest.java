package com.nowellpoint.services.rest.model;

import javax.validation.constraints.NotEmpty;

import lombok.Getter;

@Getter
public class ConnectionRequest {
	
	@NotEmpty
	private String clientId;
	
	@NotEmpty
	private String clientSecret;
	
	@NotEmpty
	private String username;
	
	@NotEmpty
	private String password;
}