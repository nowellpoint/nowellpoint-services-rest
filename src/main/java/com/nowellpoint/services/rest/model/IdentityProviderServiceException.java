package com.nowellpoint.services.rest.model;

import com.amazonaws.services.cognitoidp.model.AWSCognitoIdentityProviderException;

public class IdentityProviderServiceException extends ServiceException {

	private static final long serialVersionUID = 6131272419664807453L;
	
	public IdentityProviderServiceException(AWSCognitoIdentityProviderException exception) {
		super(404, "USER_SETUP_FAILED", exception.getErrorMessage(), exception.getServiceName(), exception.getErrorCode());
	}
	
	public IdentityProviderServiceException(int statusCode, String code, String detail, String source, String title) {
		super(statusCode, code, detail, source, title);
	}
}