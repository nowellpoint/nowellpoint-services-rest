package com.nowellpoint.services.rest.model;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@RegisterForReflection
public class AccessTokenRequest {
	private String id;
	private String subject;
	private String audience;
	private String issuer;
	private Long issuedAt;
	private Long expiresAt;
	private List<String> groups;
}