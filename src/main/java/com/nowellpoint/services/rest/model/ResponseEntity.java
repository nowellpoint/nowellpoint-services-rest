package com.nowellpoint.services.rest.model;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@RegisterForReflection
public class ResponseEntity {
	
	private Object record;
	private Boolean success;
	private List<Error> errors;

	public static ResponseEntity of(Object record) {
		return ResponseEntity.builder()
				.success(Boolean.TRUE)
				.record(record)
				.build();
	}
	
	public static ResponseEntity of(Set<? extends ConstraintViolation<?>> violations) {
		return ResponseEntity.builder()
				.success(Boolean.FALSE)
				.errors(violations.stream()
						.map(violation -> Error.of(violation))
						.collect(Collectors.toList()))
				.build();
	}
}