package com.nowellpoint.services.rest.model.sforce;

import javax.json.bind.annotation.JsonbProperty;

import lombok.Getter;

@Getter
public class ApiError {
	@JsonbProperty("message") private String message;
	@JsonbProperty("errorCode") private String errorCode;
	@JsonbProperty("fields") private String[] fields;
	@JsonbProperty("error") private String error;
	@JsonbProperty("error_description") private String errorDescription;
}