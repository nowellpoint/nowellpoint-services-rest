package com.nowellpoint.api.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
//import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
//import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
//import javax.json.Json;
//import javax.json.JsonObject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.microprofile.config.Config;
import org.jboss.logging.Logger;
import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jwk.HttpsJwks;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.resolvers.HttpsJwksVerificationKeyResolver;
import org.jose4j.lang.JoseException;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
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
import com.amazonaws.services.cognitoidp.model.GlobalSignOutRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.MessageActionType;
import com.nowellpoint.services.rest.IdentityResource;
import com.nowellpoint.services.rest.JaxRsActivator;
import com.nowellpoint.services.rest.model.IdentityProviderServiceException;
import com.nowellpoint.services.rest.model.LoggedInEvent;
import com.nowellpoint.services.rest.model.Token;
import com.nowellpoint.services.rest.model.User;
import com.nowellpoint.services.rest.model.UserRequest;
import com.nowellpoint.services.rest.util.ConfigProperties;

import io.quarkus.mailer.ReactiveMailer;

@ApplicationScoped
public class IdentityProviderService {
	
	private static final String USERNAME                  = "USERNAME";
	private static final String PASSWORD                  = "PASSWORD";
	private static final String NEW_PASSWORD              = "NEW_PASSWORD";
	private static final String NEW_PASSWORD_REQUIRED     = "NEW_PASSWORD_REQUIRED";
	private static final String REFRESH_TOKEN             = "REFRESH_TOKEN";
	
	private static String AWS_ACCESS_KEY;
	private static String AWS_SECRET_ACCESS_KEY;
	private static String AWS_REGION;
	private static String COGNITO_IDP_JWKS_URL;
	private static String COGNITO_CLIENT_ID;
	private static String COGNITO_USER_POOL_ID;
	private static String KEYSTORE_PASSWORD;
	private static String KEYSTORE;
	
	private HttpsJwksVerificationKeyResolver httpsJwksKeyResolver;

	private static final char[] chars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'q', 'w', 'e', 'r', 't', 'z', 'u', 'i', 'o', 'p', 'a', 's',
	        'd', 'f', 'g', 'h', 'j', 'k', 'l', 'y', 'x', 'c', 'v', 'b', 'n', 'm', 'Q', 'W', 'E', 'R', 'T', 'Z', 'U', 'I', 'O', 'P', 'A',
	        'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 'Y', 'X', 'C', 'V', 'B', 'N', 'M', '<', '=', '>', '?', '@' };
	
	@Inject
	Config config;
	
	@Inject
	Logger logger;

	@Inject
	Event<LoggedInEvent> loggedInEvent; 
	
	@Inject
	UserService userService;
	
	@Inject
	ReactiveMailer reactiveMailer;
	
	@Inject
	EmailService emailService;
	
	@PostConstruct
	public void init() {
		
		AWS_ACCESS_KEY               = config.getValue(ConfigProperties.AWS_ACCESS_KEY, String.class);
		AWS_SECRET_ACCESS_KEY        = config.getValue(ConfigProperties.AWS_SECRET_ACCESS_KEY, String.class);
		AWS_REGION                   = config.getValue(ConfigProperties.AWS_REGION, String.class);
		COGNITO_IDP_JWKS_URL         = config.getValue(ConfigProperties.COGNITO_IDP_JWKS_URL, String.class);
		COGNITO_CLIENT_ID            = config.getValue(ConfigProperties.COGNITO_CLIENT_ID, String.class);
		COGNITO_USER_POOL_ID         = config.getValue(ConfigProperties.COGNITO_USER_POOL_ID, String.class);
		KEYSTORE_PASSWORD            = config.getValue("javax.net.ssl.keyStorePassword", String.class);
		KEYSTORE                     = config.getValue("javax.net.ssl.keyStore", String.class);
		
		HttpsJwks httpsJkws = new HttpsJwks(String.format(COGNITO_IDP_JWKS_URL, AWS_REGION, COGNITO_USER_POOL_ID));
	    httpsJwksKeyResolver = new HttpsJwksVerificationKeyResolver(httpsJkws);
	}
	
	/**
	 * @param request
	 * @return
	 */
	public String createUser(UserRequest request) {
		AWSCognitoIdentityProvider cognitoClient = getCognitoIdentityClient();
		AdminCreateUserRequest cognitoRequest = new AdminCreateUserRequest()
		       .withUserPoolId(COGNITO_USER_POOL_ID)
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
		              .withTemporaryPassword(generateTemporaryPassword(12))
		              .withMessageAction(MessageActionType.SUPPRESS)
		              .withDesiredDeliveryMediums(DeliveryMediumType.EMAIL)
		              .withForceAliasCreation(Boolean.FALSE);
		 
		AdminCreateUserResult createUserResult = null;
		
		try {
			createUserResult = cognitoClient.adminCreateUser(cognitoRequest);
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
	
	public Token authenticate(String username, String password) {
		final Map<String, String> authParams = new HashMap<>();
		authParams.put(USERNAME, username);  
		authParams.put(PASSWORD, password);
		
		try {
			
			AuthenticationResultType authenticationResult = authenticate(authParams);
			
			JwtClaims claims = getClaims(authenticationResult.getAccessToken());
			
			User user = userService.findById(claims.getSubject());
			
			String accessToken = generateAccessToken(claims, user.getOrganizationId());
			
			Token token = Token.builder()
					.id(getId())
					.accessToken(accessToken)
					.expiresIn(authenticationResult.getExpiresIn().longValue())
					.refreshToken(authenticationResult.getRefreshToken())
					.tokenType(authenticationResult.getTokenType())
					.build();
			
			LoggedInEvent event = LoggedInEvent.builder()
					.audience(user.getOrganizationId())
					.expiration(Instant.ofEpochMilli(claims.getExpirationTime().getValueInMillis()))
					.id(claims.getJwtId())
					.issuedAt(Instant.ofEpochMilli(claims.getIssuedAt().getValueInMillis()))
					.issuer(claims.getIssuer())
					.subject(claims.getSubject())
					.build();
			
			loggedInEvent.fireAsync(event);
			
			return token;
			
		} catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | JoseException | InvalidJwtException | MalformedClaimException e) {
			logger.error(e);
			throw new IdentityProviderServiceException(500, e.getClass().getSimpleName().toUpperCase(), ExceptionUtils.getStackTrace(e), "authenticate()", e.getMessage());
		}
	}
	
	public Token refreshToken(String refreshToken) {
		final Map<String, String> authParams = new HashMap<>();
		authParams.put(REFRESH_TOKEN, refreshToken);  
		
		try {
			
			AuthenticationResultType authenticationResult = refreshToken(authParams);
			
			JwtClaims claims = getClaims(authenticationResult.getAccessToken());
			
			User user = userService.findById(claims.getSubject());
			
			String accessToken = generateAccessToken(claims, user.getOrganizationId());
			
			Token token = Token.builder()
					.id(getId())
					.accessToken(accessToken)
					.expiresIn(authenticationResult.getExpiresIn().longValue())
					.refreshToken(refreshToken)
					.tokenType(authenticationResult.getTokenType())
					.build();
			
			return token;
		
		} catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | JoseException | InvalidJwtException | MalformedClaimException e) {
			logger.error(e);
			throw new IdentityProviderServiceException(500, e.getClass().getSimpleName().toUpperCase(), ExceptionUtils.getStackTrace(e), "refreshToken()", e.getMessage());
		}
	}
	
	public void revokeToken(String accessToken) {
		GlobalSignOutRequest globalSignOutRequest = new GlobalSignOutRequest().withAccessToken(accessToken);
		AWSCognitoIdentityProvider cognitoClient = getCognitoIdentityClient();
		cognitoClient.globalSignOut(globalSignOutRequest);
	}
	
	public void disableUser(String username) {
		AdminDisableUserRequest disableUserRequest = new AdminDisableUserRequest()
				.withUserPoolId(COGNITO_USER_POOL_ID)
				.withUsername(username);
		
		getCognitoIdentityClient().adminDisableUser(disableUserRequest);
	}
	
	public void deleteUser(String username) {
		AdminDeleteUserRequest deleteUserRequest = new AdminDeleteUserRequest()
				.withUserPoolId(COGNITO_USER_POOL_ID)
				.withUsername(username);
		
		getCognitoIdentityClient().adminDeleteUser(deleteUserRequest);
	}
	
	@PreDestroy() 
	public void sthudown() {
		getCognitoIdentityClient().shutdown();
	}
	
	private AuthenticationResultType refreshToken(Map<String, String> authParams) {
		AuthenticationResultType authenticationResultType = null;
		AWSCognitoIdentityProvider cognitoClient = getCognitoIdentityClient();
		
		final InitiateAuthRequest authRequest = new InitiateAuthRequest()
	    		.withAuthFlow(AuthFlowType.REFRESH_TOKEN)
	    		.withClientId(COGNITO_CLIENT_ID)
	    		.withAuthParameters(authParams);
		
		InitiateAuthResult authResult = cognitoClient.initiateAuth(authRequest);
		
		if (StringUtils.isNotBlank(authResult.getChallengeName())) {
			
		} else {
			authenticationResultType = authResult.getAuthenticationResult();
		}
		
		return authenticationResultType;
	}
	
	private AuthenticationResultType authenticate(Map<String, String> authParams) {
		AuthenticationResultType authenticationResultType = null;
	    AWSCognitoIdentityProvider cognitoClient = getCognitoIdentityClient();
	 
	    final InitiateAuthRequest authRequest = new InitiateAuthRequest()
	    		.withAuthFlow(AuthFlowType.USER_PASSWORD_AUTH)
	    		.withClientId(COGNITO_CLIENT_ID)
	    		.withAuthParameters(authParams);
	    
	    InitiateAuthResult authResult = cognitoClient.initiateAuth(authRequest);
	    
	    if (StringUtils.isNotBlank(authResult.getChallengeName())) {
	    	if (NEW_PASSWORD_REQUIRED.equals(authResult.getChallengeName())) {
	    		final Map<String, String> challengeResponses = new HashMap<>();
	    		challengeResponses.put(USERNAME, authParams.get(USERNAME));
	    		challengeResponses.put(PASSWORD, authParams.get(PASSWORD));
	    		challengeResponses.put(NEW_PASSWORD, authParams.get(PASSWORD));
	    		
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
	    
	    return authenticationResultType;
	}
	
	private String generateAccessToken(JwtClaims claims, String audience) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException, JoseException, MalformedClaimException {
		
		char[] keyStorePassword = KEYSTORE_PASSWORD.toCharArray();
		
		InputStream inputStream = new FileInputStream(KEYSTORE);
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(inputStream, keyStorePassword);

        Key privateKey = keystore.getKey("selfsigned", keyStorePassword);
        
//        Certificate certificate = keystore.getCertificate("selfsigned");
//        PublicKey publicKey = certificate.getPublicKey();
//        RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;
        
//        JsonObject jwk = Json.createObjectBuilder()
//        		.add("kty", rsaPublicKey.getAlgorithm())
//        		.add("kid", claims.getHeader().getKeyId())
//        		.add("n", Base64.getUrlEncoder().encodeToString(rsaPublicKey.getModulus().toByteArray()))
//        		.add("e", Base64.getUrlEncoder().encodeToString(rsaPublicKey.getPublicExponent().toByteArray()))
//        		.add("alq", "RS256")
//        		.add("use", "sig")
//        		.build();
        
//        JsonObject jwks = Json.createObjectBuilder()
//        		.add("keys", Json.createArrayBuilder().add(jwk))
//        		.build();
        
        
        
        JwtClaims jwtClaims = new JwtClaims();
        jwtClaims.setJwtId(claims.getJwtId());
        jwtClaims.setIssuer(claims.getIssuer());
        jwtClaims.setAudience(audience);
        jwtClaims.setSubject(claims.getSubject());
        jwtClaims.setExpirationTime(claims.getExpirationTime());
        jwtClaims.setIssuedAt(claims.getIssuedAt());
        jwtClaims.setClaim("groups", claims.getClaimValue("cognito:groups"));
        
        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(jwtClaims.toJson());
        jws.setKey(privateKey);
        jws.setKeyIdHeaderValue("NNRVsz2gkKaIt7YJyS0LJc7a1B5RSchnupQkbdgX7mc=");
        jws.setHeader("typ", "JWT");
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
        
        return jws.getCompactSerialization();
	}
	
	private JwtClaims getClaims(String jwt) throws InvalidJwtException {
		JwtConsumer jwtConsumer = new JwtConsumerBuilder()
	            .setRequireExpirationTime() 
	            .setAllowedClockSkewInSeconds(30) 
	            .setRequireSubject()
	        //    .setExpectedIssuer("Issuer") 
	        //    .setExpectedAudience("Audience") 
	            .setVerificationKeyResolver(httpsJwksKeyResolver) 
	            .setJwsAlgorithmConstraints(ConstraintType.WHITELIST, AlgorithmIdentifiers.RSA_USING_SHA256)
	            .build(); 
			
		return jwtConsumer.processToClaims(jwt);
	}
	
	private AWSCognitoIdentityProvider getCognitoIdentityClient() {
		AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_ACCESS_KEY));
	    return AWSCognitoIdentityProviderClientBuilder.standard()
	    		.withCredentials(credentialsProvider)
	    		.withRegion(AWS_REGION)
	    		.build();
	}

	private static String generateTemporaryPassword(int length) {
		StringBuilder stringBuilder = new StringBuilder();
	    for (int i = 0; i < length; i++) {
	        stringBuilder.append(chars[new Random().nextInt(chars.length)]);
	    }
	    return stringBuilder.toString();
	}
	
	private String getId() {
		return UriBuilder.fromPath("http://localhost:8080" + JaxRsActivator.class.getAnnotation(ApplicationPath.class).value()).path(IdentityResource.class)
				.build()
				.toString();
	}
}