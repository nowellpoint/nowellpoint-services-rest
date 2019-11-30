package com.nowellpoint.services.rest.model.sforce;

import lombok.Getter;

public class SalesforceServiceException extends RuntimeException {
	
	private static final long serialVersionUID = 2726007023971974319L;
	
	@Getter private int statusCode;
	@Getter private String error;
	@Getter private String errorDescription;
	
	public SalesforceServiceException(int statusCode, ApiError error) {
		super(error.getErrorCode() + ": " + error.getErrorDescription());
		this.statusCode = statusCode;
		this.error = error.getError();
		this.errorDescription = error.getErrorDescription();
	}
}