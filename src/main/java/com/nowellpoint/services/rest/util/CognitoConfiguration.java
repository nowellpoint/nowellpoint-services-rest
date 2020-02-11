package com.nowellpoint.services.rest.util;

import io.quarkus.arc.config.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ConfigProperties(prefix = "cognito")
public interface CognitoConfiguration {

	@ConfigProperty(name = "idp.jwks.url") 
    String idpJwksUrl();
	
	@ConfigProperty(name = "client.id") 
    String clientId();
	
	@ConfigProperty(name = "user.pool.id") 
    String userPoolId();
}