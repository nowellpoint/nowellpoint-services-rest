package com.nowellpoint.services.rest.model;

import javax.json.bind.annotation.JsonbProperty;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Token {
	@JsonbProperty(value="id") private String id;
	@JsonbProperty(value="environment_url") private String environmentUrl;
	@JsonbProperty(value="access_token") private String accessToken;
	@JsonbProperty(value="refresh_token") private String refreshToken;
	@JsonbProperty(value="token_type") private String tokenType;
	@JsonbProperty(value="expires_in") private Long expiresIn;
}