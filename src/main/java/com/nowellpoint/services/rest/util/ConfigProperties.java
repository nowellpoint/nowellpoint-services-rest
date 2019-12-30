package com.nowellpoint.services.rest.util;

public class ConfigProperties {
	
	/**
	 * environment variables
	 */
	
	public static final String AWS_REGION = "aws.region";
	public static final String AWS_ACCESS_KEY = "aws.access.key";
	public static final String AWS_SECRET_ACCESS_KEY = "aws.secret.access.key";
	
	/**
	 * application properties
	 */
	
	public static final String COGNITO_IDP_JWKS_URL = "cognito.idp.jwks.url";
	
	/**
	 * secret values
	 */
	
	public static final String COGNITO_CLIENT_ID = "congnito.client.id";
	public static final String COGNITO_USER_POOL_ID = "cognito.user.pool.id";	
	public static final String SALESFORCE_CLIENT_ID = "salesforce.client.id";
	public static final String SALESFORCE_CLIENT_SECRET = "salesforce.client.secret";
}