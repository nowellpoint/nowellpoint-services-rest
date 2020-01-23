package com.nowellpoint.services.rest.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = 5146705965247045523L;
	
	private String code;
	private String detail;
	private String source;
	private String title;
	
	public ServiceException(String errorMessage) {
		super(errorMessage);
	}
}