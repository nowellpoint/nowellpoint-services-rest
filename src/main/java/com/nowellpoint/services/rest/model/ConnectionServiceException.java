package com.nowellpoint.services.rest.model;

public class ConnectionServiceException extends ServiceException {

	private static final long serialVersionUID = 6131272419664807453L;
	
	public ConnectionServiceException(String code, String detail, String source, String title) {
		super(401, code, detail, source, title);
	}
}