package com.nowellpoint.api.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.microprofile.config.Config;
import org.jboss.logging.Logger;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
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
import com.nowellpoint.services.rest.model.CreateUserRequest;
import com.nowellpoint.services.rest.model.Key;
import com.nowellpoint.services.rest.model.Keys;
import com.nowellpoint.services.rest.model.Token;
import com.nowellpoint.services.rest.util.ConfigProperties;
import com.nowellpoint.services.rest.util.JsonbUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SigningKeyResolverAdapter;

@ApplicationScoped
public class IdentityProviderService {
	
	private static final String USERNAME                  = "USERNAME";
	private static final String PASSWORD                  = "PASSWORD";
	private static final String NEW_PASSWORD              = "NEW_PASSWORD";
	private static final String NEW_PASSWORD_REQUIRED     = "NEW_PASSWORD_REQUIRED";
	private static final String REFRESH_TOKEN             = "REFRESH_TOKEN";
	
	private static String AWS_REGION;
	private static String COGNITO_IDP_JWKS_URL;
	private static String COGNITO_CLIENT_ID;
	private static String COGNITO_USER_POOL_ID;
	private static Keys keys;

	private static char[] chars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'q', 'w', 'e', 'r', 't', 'z', 'u', 'i', 'o', 'p', 'a', 's',
	        'd', 'f', 'g', 'h', 'j', 'k', 'l', 'y', 'x', 'c', 'v', 'b', 'n', 'm', 'Q', 'W', 'E', 'R', 'T', 'Z', 'U', 'I', 'O', 'P', 'A',
	        'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 'Y', 'X', 'C', 'V', 'B', 'N', 'M', '<', '=', '>', '?', '@' };
	
	@Inject
	Config config;
	
	@Inject
	Logger logger;

	@Inject
	Event<Claims> authenticationEvent; 
	
	@PostConstruct
	public void init() {
		
		AWS_REGION                   = config.getValue(ConfigProperties.AWS_REGION, String.class);
		COGNITO_IDP_JWKS_URL         = config.getValue(ConfigProperties.COGNITO_IDP_JWKS_URL, String.class);
		COGNITO_CLIENT_ID            = config.getValue(ConfigProperties.COGNITO_CLIENT_ID, String.class);
		COGNITO_USER_POOL_ID         = config.getValue(ConfigProperties.COGNITO_USER_POOL_ID, String.class);
		
		try {
			URIBuilder builder = new URIBuilder(String.format(COGNITO_IDP_JWKS_URL, AWS_REGION, COGNITO_USER_POOL_ID));
			
			HttpGet get = new HttpGet(builder.build());
			
			final HttpResponse response = HttpClients.createDefault()
					.execute(get);

			if (response.getStatusLine().getStatusCode() == 200) {
				keys = JsonbUtil.fromJson(response.getEntity().getContent(), Keys.class); 
			} else {
				logger.error(IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8.name()));
			}
			
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public String createUser(CreateUserRequest request) {
		AWSCognitoIdentityProvider cognitoClient = getAmazonCognitoIdentityClient();
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
		              new AttributeType()
		              .withName("phone_number")
		              .withValue("+1".concat(request.getPhone())),
		              new AttributeType()
		              .withName("zoneinfo")
		              .withValue(request.getTimeZone()),
		              new AttributeType()
		              .withName("email_verified")
		              .withValue("false"))
		              .withTemporaryPassword(generateTemporaryPassword(8))
		              .withMessageAction("SUPPRESS")
		              .withDesiredDeliveryMediums(DeliveryMediumType.EMAIL)
		              .withForceAliasCreation(Boolean.FALSE);
		 
		AdminCreateUserResult createUserResult = cognitoClient.adminCreateUser(cognitoRequest);
		
		return createUserResult.getUser()
				.getAttributes()
				.stream()
				.filter(a -> "sub".equals(a.getName()))
				.findFirst()
				.get()
				.getValue();
	}
	
	public Token authenticate(String username, String password) throws UnsupportedEncodingException {
		final Map<String, String> authParams = new HashMap<>();
		authParams.put(USERNAME, username);  
		authParams.put(PASSWORD, password);
		
		AuthenticationResultType authenticationResult = authenticate(authParams);

		Jws<Claims> claims = getClaims(authenticationResult.getAccessToken());

		authenticationEvent.fireAsync(claims.getBody());
	    
		try {
			String accessToken = generateAccessToken(claims);
			System.out.println(accessToken);
		} catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException | CertificateException
				| IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    Token token = Token.builder()
	    		//.accessToken(accessToken)
	    		.accessToken(authenticationResult.getAccessToken())
	    		.expiresIn(authenticationResult.getExpiresIn().longValue())
	    		.refreshToken(authenticationResult.getRefreshToken())
	    		.tokenType(authenticationResult.getTokenType())
				.build();
		
		return token;
	}
	
	public Token refreshToken(String refreshToken) throws UnsupportedEncodingException {
		final Map<String, String> authParams = new HashMap<>();
		authParams.put(REFRESH_TOKEN, refreshToken);  
		
		AuthenticationResultType authenticationResult = refreshToken(authParams);

		Jws<Claims> claims = getClaims(authenticationResult.getAccessToken());
	    
	    String accessToken = null;
		try {
			accessToken = generateAccessToken(claims);
		} catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException | CertificateException
				| IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    Token token = Token.builder()
	    		.accessToken(accessToken)
	    		.expiresIn(authenticationResult.getExpiresIn().longValue())
	    		.refreshToken(refreshToken)
	    		.tokenType(authenticationResult.getTokenType())
	    		.build();
		
		return token;
	}
	
	public void revokeToken(String accessToken) {
		GlobalSignOutRequest globalSignOutRequest = new GlobalSignOutRequest().withAccessToken(accessToken);
		AWSCognitoIdentityProvider cognitoClient = getAmazonCognitoIdentityClient();
		cognitoClient.globalSignOut(globalSignOutRequest);
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
	
	@PreDestroy() 
	public void sthudown() {
		getAmazonCognitoIdentityClient().shutdown();
	}
	
	private AuthenticationResultType refreshToken(Map<String, String> authParams) {
		AuthenticationResultType authenticationResultType = null;
		AWSCognitoIdentityProvider cognitoClient = getAmazonCognitoIdentityClient();
		
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
	    AWSCognitoIdentityProvider cognitoClient = getAmazonCognitoIdentityClient();
	 
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
	
	private String generateAccessToken(Jws<Claims> claims) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException {	
		//InputStream is = getClass().getClassLoader().getResourceAsStream("keystore.jks");
		FileInputStream is = new FileInputStream("/Users/jherson/workspace/nowellpoint-api/keystore.jks");
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(is, "password".toCharArray());

        java.security.Key key = keystore.getKey("selfsigned", "password".toCharArray());
        
        //Certificate cert = keystore.getCertificate("selfsigned");
        //PublicKey publicKey = cert.getPublicKey();
	    
	    return Jwts.builder()
				.setHeaderParam("kid", claims.getHeader().getKeyId())
				.setId(claims.getBody().getId())
				.setIssuer(claims.getBody().getIssuer())
				.setAudience("nowellpoint.com")
				.setSubject(claims.getBody().getSubject())
				.setExpiration(claims.getBody().getExpiration())
				.setIssuedAt(claims.getBody().getIssuedAt())
				.claim("groups", claims.getBody().get("cognito:groups"))
				.signWith(SignatureAlgorithm.valueOf(claims.getHeader().getAlgorithm()), key)
				.compact();
	  
	}
	
	private Jws<Claims> getClaims(String token) {
		return Jwts.parser()
				.setSigningKeyResolver(new SigningKeyResolverAdapter() {
					@SuppressWarnings("rawtypes")
					public java.security.Key resolveSigningKey(JwsHeader jwsHeader, Claims claims) {
						Key key = keys.getKey(jwsHeader.getKeyId()).get();
						try {
							BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(key.getModulus()));
							BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(key.getExponent()));
							return KeyFactory.getInstance(key.getKeyType()).generatePublic(new RSAPublicKeySpec(modulus, exponent));
						} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
							e.printStackTrace();
							return null;
						}
					}})
				.parseClaimsJws(token);
	}
	
	private AWSCognitoIdentityProvider getAmazonCognitoIdentityClient() {
	    return AWSCognitoIdentityProviderClientBuilder.standard()
	    		.withCredentials(new EnvironmentVariableCredentialsProvider())
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
}