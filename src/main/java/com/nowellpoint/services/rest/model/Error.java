package com.nowellpoint.services.rest.model;

import javax.validation.ConstraintViolation;
import javax.ws.rs.core.Response;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@RegisterForReflection
public class Error {
	private String status;
	private String source;
	private String code;
	private String title;
	private String detail;
	
	public static Error of(ConstraintViolation<?> violation) {
		return Error.builder()
				.code("CONSTRAINT_VIOLATION")
				.detail(String.format("%s has the value: '%s' %s", violation.getPropertyPath(), violation.getInvalidValue(), violation.getMessage()))
				.status(String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()))
				.source(String.valueOf(violation.getPropertyPath()))
				.title(formatTitle(violation))
				.build();
	}
	
	private static String formatTitle(ConstraintViolation<?> violation) {
		if (violation.getConstraintDescriptor().getAnnotation().annotationType().isAssignableFrom(javax.validation.constraints.NotEmpty.class)) {
			return "Value cannot be empty";
		} else if (violation.getConstraintDescriptor().getAnnotation().annotationType().isAssignableFrom(javax.validation.constraints.NotNull.class)) {
			return "Value cannot be null";
		} else {
			return "Invalid Value";
		}
	}
}