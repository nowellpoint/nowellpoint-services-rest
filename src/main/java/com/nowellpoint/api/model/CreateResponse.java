package com.nowellpoint.api.model;

import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;

import lombok.Getter;

@Getter
public class CreateResponse {
	
	private Object record;
	private String message;
	private Boolean success;

	public CreateResponse(Object record) {
		this.success = Boolean.TRUE;
		this.record = record;
	}
	
	public CreateResponse(Set<? extends ConstraintViolation<?>> violations) {
		this.success = Boolean.FALSE;
		this.message = violations.stream()
				.map(v -> v.getMessage())
				.collect(Collectors.joining(", "));
	}
}