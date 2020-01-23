package com.nowellpoint.services.rest.model;

import com.amazonaws.services.cognitoidp.model.AWSCognitoIdentityProviderException;

public class IdentityProviderException extends ServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6131272419664807453L;
	
	public IdentityProviderException(AWSCognitoIdentityProviderException exception) {
		super(exception.getMessage());
		this.setCode("USER_SETUP_FAILED");
		this.setSource(exception.getServiceName());
		this.setDetail(exception.getErrorMessage());
		this.setTitle(exception.getErrorCode());
	}
}