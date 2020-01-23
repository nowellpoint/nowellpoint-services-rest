package com.nowellpoint.services.rest.model;

import java.time.Instant;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@RegisterForReflection
public class EmailEvent {
	private Instant sentDate; 
	private String subject;
	private String body;
	private String toId;
	private String status;
}