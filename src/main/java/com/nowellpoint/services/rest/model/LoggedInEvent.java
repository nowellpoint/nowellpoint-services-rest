package com.nowellpoint.services.rest.model;

import java.time.Instant;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@RegisterForReflection
public class LoggedInEvent {
	private String subject;
	private String audience;
	private Instant expiration;
	private String id;
	private Instant issuedAt;
	private String issuer;
}