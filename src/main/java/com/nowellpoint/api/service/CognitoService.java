package com.nowellpoint.api.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jwk.HttpsJwks;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.resolvers.HttpsJwksVerificationKeyResolver;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AWSCognitoIdentityProviderException;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserResult;
import com.amazonaws.services.cognitoidp.model.AdminDeleteUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminDisableUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminRespondToAuthChallengeRequest;
import com.amazonaws.services.cognitoidp.model.AdminRespondToAuthChallengeResult;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.cognitoidp.model.ChallengeNameType;
import com.amazonaws.services.cognitoidp.model.DeliveryMediumType;
import com.amazonaws.services.cognitoidp.model.GetUserRequest;
import com.amazonaws.services.cognitoidp.model.GetUserResult;
import com.amazonaws.services.cognitoidp.model.GlobalSignOutRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.MessageActionType;
import com.nowellpoint.services.rest.model.IdentityProviderServiceException;
import com.nowellpoint.services.rest.model.UserRequest;
import com.nowellpoint.services.rest.util.CognitoConfiguration;

@ApplicationScoped
public class CognitoService extends AbstractAWSService {
	
	private static final String USERNAME                  = "USERNAME";
	private static final String PASSWORD                  = "PASSWORD";
	private static final String NEW_PASSWORD              = "NEW_PASSWORD";
	private static final String NEW_PASSWORD_REQUIRED     = "NEW_PASSWORD_REQUIRED";
	
	private static HttpsJwksVerificationKeyResolver jwksKeyResolver;
	
	@Inject
	protected CognitoConfiguration cognitoConfiguration;
	
	@PostConstruct
	public void init() {		
		jwksKeyResolver = new HttpsJwksVerificationKeyResolver(new HttpsJwks(String.format(cognitoConfiguration.idpJwksUrl(), awsConfiguration.region(), cognitoConfiguration.userPoolId())));
	}
	
	@PreDestroy() 
	public void sthudown() {
		getCognitoIdentityClient().shutdown();
	}
	
	public AuthenticationResultType authenticate(Map<String, String> authParams) {
		
	    AWSCognitoIdentityProvider cognitoClient = getCognitoIdentityClient();
	 
	    final InitiateAuthRequest authRequest = new InitiateAuthRequest()
	    		.withAuthFlow(AuthFlowType.USER_PASSWORD_AUTH)
	    		.withClientId(cognitoConfiguration.clientId())
	    		.withAuthParameters(authParams);
	    
	    InitiateAuthResult authResult = cognitoClient.initiateAuth(authRequest);
	    
	    AuthenticationResultType authenticationResultType = null;
	    
	    if (StringUtils.isNotBlank(authResult.getChallengeName())) {
	    	if (NEW_PASSWORD_REQUIRED.equals(authResult.getChallengeName())) {
	    		final Map<String, String> challengeResponses = new HashMap<>();
	    		challengeResponses.put(USERNAME, authParams.get(USERNAME));
	    		challengeResponses.put(PASSWORD, authParams.get(PASSWORD));
	    		challengeResponses.put(NEW_PASSWORD, authParams.get(PASSWORD));
	    		
	    		final AdminRespondToAuthChallengeRequest request = new AdminRespondToAuthChallengeRequest()
	    				.withChallengeName(ChallengeNameType.NEW_PASSWORD_REQUIRED)
	    				.withChallengeResponses(challengeResponses)
	    				.withClientId(cognitoConfiguration.clientId())
	    	    		.withUserPoolId(cognitoConfiguration.userPoolId())
	    				.withSession(authResult.getSession());
	    		
	    		AdminRespondToAuthChallengeResult resultChallenge = cognitoClient.adminRespondToAuthChallenge(request);
	    		authenticationResultType = resultChallenge.getAuthenticationResult();
	    	}
	    } else {
	    	authenticationResultType = authResult.getAuthenticationResult();
	    }
	    
	    return authenticationResultType;
	}
	
	public AuthenticationResultType refreshToken(Map<String, String> authParams) {
		AuthenticationResultType authenticationResultType = null;
		AWSCognitoIdentityProvider cognitoClient = getCognitoIdentityClient();
		
		final InitiateAuthRequest authRequest = new InitiateAuthRequest()
	    		.withAuthFlow(AuthFlowType.REFRESH_TOKEN)
	    		.withClientId(cognitoConfiguration.clientId())
	    		.withAuthParameters(authParams);
		
		InitiateAuthResult authResult = cognitoClient.initiateAuth(authRequest);
		
		if (StringUtils.isNotBlank(authResult.getChallengeName())) {
			
		} else {
			authenticationResultType = authResult.getAuthenticationResult();
		}
		
		return authenticationResultType;
	}
	
	public String createUser(UserRequest request) {
		AdminCreateUserRequest cognitoUserRequest = new AdminCreateUserRequest()
		       .withUserPoolId(cognitoConfiguration.userPoolId())
			   .withUsername(request.getEmail())
		       .withUserAttributes(
		    		  new AttributeType()
		    		  .withName("email")
		    		  .withValue(request.getEmail()),
		              new AttributeType()
		              .withName("name")
		              .withValue(request.getFirstName()),
		              new AttributeType()
		              .withName("family_name")
		              .withValue(request.getLastName()),
		            //  new AttributeType()
		            //  .withName("phone_number")
		           //   .withValue("+1".concat(request.getPhone())),
		              new AttributeType()
		              .withName("zoneinfo")
		              .withValue(request.getTimeZone()),
		              new AttributeType()
		              .withName("email_verified")
		              .withValue("false"))
		              .withTemporaryPassword(request.getPassword())
		              .withMessageAction(MessageActionType.SUPPRESS)
		              .withDesiredDeliveryMediums(DeliveryMediumType.EMAIL)
		              .withForceAliasCreation(Boolean.FALSE);
		
		return createUser(cognitoUserRequest);
	}

	public JwtClaims getClaims(String jwt) throws InvalidJwtException {
		JwtConsumer jwtConsumer = new JwtConsumerBuilder()
	            .setRequireExpirationTime() 
	            .setAllowedClockSkewInSeconds(30) 
	            .setRequireSubject()
	        //    .setExpectedIssuer("Issuer") 
	        //    .setExpectedAudience("Audience") 
	            .setVerificationKeyResolver(jwksKeyResolver) 
	            .setJwsAlgorithmConstraints(ConstraintType.WHITELIST, AlgorithmIdentifiers.RSA_USING_SHA256)
	            .build(); 
			
		return jwtConsumer.processToClaims(jwt);
	}
	
	/**
     * <p>
     * The Identity provider service method to revoke the given <code>access token</code> with a global sign out.
     * </p>
     * 
     * @return void
     */
	
	public void revokeToken(String accessToken) {
		GlobalSignOutRequest globalSignOutRequest = new GlobalSignOutRequest().withAccessToken(accessToken);
		AWSCognitoIdentityProvider cognitoClient = getCognitoIdentityClient();
		cognitoClient.globalSignOut(globalSignOutRequest);
	}
	
	/**
     * <p>
     * The Identity provider service method to disable a <code>user</code> in the given user pool.
     * </p>
     * 
     * @return void
     */
	
	public void disableUser(String username) {
		AdminDisableUserRequest disableUserRequest = new AdminDisableUserRequest()
				.withUserPoolId(cognitoConfiguration.userPoolId())
				.withUsername(username);
		
		getCognitoIdentityClient().adminDisableUser(disableUserRequest);
	}
	
	/**
     * <p>
     * The Identity provider service method to delete a <code>user</code> from the given user pool.
     * </p>
     * 
     * @return void
     */
	
	public void deleteUser(String username) {
		AdminDeleteUserRequest deleteUserRequest = new AdminDeleteUserRequest()
				.withUserPoolId(cognitoConfiguration.userPoolId())
				.withUsername(username);
		
		getCognitoIdentityClient().adminDeleteUser(deleteUserRequest);
	}
	
	public String getUser(String accessToken) {
		AWSCognitoIdentityProvider cognitoClient = getCognitoIdentityClient();
		GetUserRequest request = new GetUserRequest().withAccessToken(accessToken);
		GetUserResult result = cognitoClient.getUser(request);
		return result.getUsername();
	}
	
	private String createUser(AdminCreateUserRequest cognitoUserRequest) {
		AWSCognitoIdentityProvider cognitoClient = getCognitoIdentityClient();
		
		AdminCreateUserResult createUserResult = null;
		
		try {
			createUserResult = cognitoClient.adminCreateUser(cognitoUserRequest);
		} catch (AWSCognitoIdentityProviderException e) {
			throw new IdentityProviderServiceException(e);
		}
		
		return createUserResult.getUser()
				.getAttributes()
				.stream()
				.filter(a -> "sub".equals(a.getName()))
				.findFirst()
				.get()
				.getValue();
	}
	
	private AWSCognitoIdentityProvider getCognitoIdentityClient() {
	    return AWSCognitoIdentityProviderClientBuilder.standard()
	    		.withCredentials(getAWSCredentialsProvider())
	    		.withRegion(awsConfiguration.region())
	    		.build();
	}
}