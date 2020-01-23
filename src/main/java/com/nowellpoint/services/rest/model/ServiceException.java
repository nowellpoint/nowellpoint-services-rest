package com.nowellpoint.services.rest.model;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = 5146705965247045523L;
	
	private int statusCode;
	private String code;
	private String detail;
	private String source;
	private String title;
	
	public ServiceException(int statusCode, String code, String detail, String source, String title) {
		super(detail);
		this.statusCode = statusCode;
		this.code = code;
		this.detail = detail;
		this.source = source;
		this.title = title;
	}
}