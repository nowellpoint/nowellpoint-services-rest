package com.nowellpoint.services.rest.model;

public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = 5146705965247045523L;
	
	public ServiceException(String errorMessage) {
		super(errorMessage);
	}
}