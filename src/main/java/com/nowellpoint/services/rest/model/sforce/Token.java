package com.nowellpoint.services.rest.model.sforce;

import javax.json.bind.annotation.JsonbProperty;

import lombok.Getter;

@Getter
public class Token {
	@JsonbProperty(value="access_token") private String accessToken;
	@JsonbProperty(value="refresh_token") private String refreshToken;
	@JsonbProperty(value="signature") private String signature;
	@JsonbProperty(value="scope") private String scope;
	@JsonbProperty(value="instance_url") private String instanceUrl;
	@JsonbProperty(value="id") private String id;
	@JsonbProperty(value="token_type") private String tokenType;
	@JsonbProperty(value="issued_at") private String issuedAt;
}