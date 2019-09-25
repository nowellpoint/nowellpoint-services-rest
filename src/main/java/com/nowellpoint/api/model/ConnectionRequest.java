package com.nowellpoint.api.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConnectionRequest {
	private String clientId;
	private String clientSecret;
	private String username;
	private String password;
	private String securityToken;
}