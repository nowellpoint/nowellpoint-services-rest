package com.nowellpoint.services.rest.model.sforce;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;

@Getter
@RegisterForReflection
public class SalesforceServiceException extends RuntimeException {
	
	private static final long serialVersionUID = 2726007023971974319L;
	
	private int statusCode;
	private String error;
	private String errorDescription;
	private String message;
	private String errorCode;
	
	public SalesforceServiceException(int statusCode, ApiError error) {
		super(error.getErrorCode() + ": " + error.getErrorDescription() != null ? error.getErrorDescription() : error.getMessage());
		this.statusCode = statusCode;
		this.errorCode = error.getErrorCode();
		this.message = error.getMessage();
		this.error = error.getError();
		this.errorDescription = error.getErrorDescription();
	}
}