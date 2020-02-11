package com.nowellpoint.services.rest.model.sforce;

import javax.json.bind.annotation.JsonbProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;

@Getter
@RegisterForReflection
public class ApiError {
	@JsonbProperty("message") private String message;
	@JsonbProperty("errorCode") private String errorCode;
	@JsonbProperty("fields") private String[] fields;
	@JsonbProperty("error") private String error;
	@JsonbProperty("error_description") private String errorDescription;
}