package com.nowellpoint.api.service;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.lang3.StringUtils;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminDeleteUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminDisableUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminRespondToAuthChallengeRequest;
import com.amazonaws.services.cognitoidp.model.AdminRespondToAuthChallengeResult;
import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.cognitoidp.model.ChallengeNameType;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;

@ApplicationScoped
public class IdentityProviderService {
	
	private static final String COGNITO_CLIENT_ID = "";
	private static final String COGNITO_USER_POOL_ID = "";
	
	public AuthenticationResultType authenticate(String username, String password) {
		AuthenticationResultType authenticationResultType = null;
	    AWSCognitoIdentityProvider cognitoClient = getAmazonCognitoIdentityClient();
	 
	    final Map<String, String> authParams = new HashMap<>();
	    authParams.put("USERNAME", username);  
	    authParams.put("PASSWORD", password);
	    
	    final InitiateAuthRequest authRequest = new InitiateAuthRequest()
	    		.withAuthFlow(AuthFlowType.USER_PASSWORD_AUTH)
	    		.withClientId(COGNITO_CLIENT_ID)
	    		.withAuthParameters(authParams);
	    
	    InitiateAuthResult authResult = cognitoClient.initiateAuth(authRequest);
	    
	    if (StringUtils.isNotBlank(authResult.getChallengeName())) {
	    	if ("NEW_PASSWORD_REQUIRED".equals(authResult.getChallengeName())) {
	    		final Map<String, String> challengeResponses = new HashMap<>();
	    		challengeResponses.put("USERNAME", username);
	    		challengeResponses.put("PASSWORD", password);
	    		challengeResponses.put("NEW_PASSWORD", password);
	    		
	    		final AdminRespondToAuthChallengeRequest request = new AdminRespondToAuthChallengeRequest()
	    				.withChallengeName(ChallengeNameType.NEW_PASSWORD_REQUIRED)
	    				.withChallengeResponses(challengeResponses)
	    				.withClientId(COGNITO_CLIENT_ID)
	    	    		.withUserPoolId(COGNITO_USER_POOL_ID)
	    				.withSession(authResult.getSession());
	    		
	    		AdminRespondToAuthChallengeResult resultChallenge = cognitoClient.adminRespondToAuthChallenge(request);
	    		authenticationResultType = resultChallenge.getAuthenticationResult();
	    	}
	    } else {
	    	authenticationResultType = authResult.getAuthenticationResult();
	    }
	    
	    cognitoClient.shutdown();
	    
	    return authenticationResultType;
	}
	
	public void disableUser(String username) {
		AdminDisableUserRequest disableUserRequest = new AdminDisableUserRequest()
				.withUserPoolId(COGNITO_USER_POOL_ID)
				.withUsername(username);
		
		getAmazonCognitoIdentityClient().adminDisableUser(disableUserRequest);
	}
	
	public void deleteUser(String username) {
		AdminDeleteUserRequest deleteUserRequest = new AdminDeleteUserRequest()
				.withUserPoolId(COGNITO_USER_POOL_ID)
				.withUsername(username);
		
		getAmazonCognitoIdentityClient().adminDeleteUser(deleteUserRequest);
	}
	
	private AWSCognitoIdentityProvider getAmazonCognitoIdentityClient() {
	    return AWSCognitoIdentityProviderClientBuilder.standard()
	    		.withCredentials(new EnvironmentVariableCredentialsProvider())
	    		.withRegion(Regions.US_EAST_1)
	    		.build();
	}
}