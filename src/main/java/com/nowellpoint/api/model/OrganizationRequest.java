package com.nowellpoint.api.model;

import lombok.Getter;

@Getter
public class OrganizationRequest {
	private String clientId;
	private String clientSecret;
	private String username;
	private String password;
}