package com.nowellpoint.api.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
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
import com.amazonaws.services.cognitoidp.model.GlobalSignOutRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;
import com.nowellpoint.api.model.Key;
import com.nowellpoint.api.model.Keys;
import com.nowellpoint.api.model.Token;
import com.nowellpoint.api.util.ConfigProperties;
import com.nowellpoint.api.util.JsonbUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SigningKeyResolverAdapter;

@ApplicationScoped
public class IdentityProviderService {
	
	private static final String USERNAME = "USERNAME";
	private static final String PASSWORD = "PASSWORD";
	private static final String NEW_PASSWORD = "NEW_PASSWORD";
	
	private static String AWS_REGION;
	private static String COGNITO_IDP_JWKS_URL;
	private static String COGNITO_CLIENT_ID;
	private static String COGNITO_USER_POOL_ID;
	private static Keys keys;
	
	@Inject
	Config config;
	
	@Inject
	Logger logger;
	
	@PostConstruct
	public void init() {
		
		AWS_REGION = config.getValue(ConfigProperties.AWS_REGION, String.class);
		COGNITO_IDP_JWKS_URL = config.getValue(ConfigProperties.COGNITO_IDP_JWKS_URL, String.class);
		COGNITO_CLIENT_ID = config.getValue(ConfigProperties.COGNITO_CLIENT_ID, String.class);
		COGNITO_USER_POOL_ID = config.getValue(ConfigProperties.COGNITO_USER_POOL_ID, String.class);
		
		try {
			URIBuilder builder = new URIBuilder(String.format(COGNITO_IDP_JWKS_URL, 
					AWS_REGION, 
					COGNITO_USER_POOL_ID));
			
			HttpGet get = new HttpGet(builder.build());
			
			final HttpResponse response = HttpClients.createDefault()
					.execute(get);

			if (response.getStatusLine().getStatusCode() == 200) {
				keys = JsonbUtil.fromJson(response.getEntity().getContent(), Keys.class); 
			} else {
				logger.error(IOUtils.toString(response.getEntity().getContent(), "utf-8"));
			}
			
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public Token authenticate(String username, String password) throws UnsupportedEncodingException {
		final Map<String, String> authParams = new HashMap<>();
		authParams.put(USERNAME, username);  
		authParams.put(PASSWORD, password);
		
		AuthenticationResultType authenticationResult = authenticate(authParams);
	    
	    Jws<Claims> claims = getClaims(authenticationResult.getAccessToken());
	    
	    String jwt = Jwts.builder()
				.setHeaderParam("kid", claims.getHeader().getKeyId())
				.setId(claims.getBody().getId())
				.setIssuer(claims.getBody().getIssuer())
				.setAudience("nowellpoint.com")
				.setSubject(claims.getBody().getSubject())
				.setExpiration(claims.getBody().getExpiration())
				.setIssuedAt(claims.getBody().getIssuedAt())
				.claim("scope", claims.getBody().get("cognito:groups"))
				.signWith(SignatureAlgorithm.HS256, System.getenv("AWS_SECRET_ACCESS_KEY").getBytes("UTF-8"))
				.compact();
	    
	    Token token = Token.builder()
	    		.accessToken(jwt)
	    		.expiresIn(authenticationResult.getExpiresIn().longValue())
	    		.refreshToken(authenticationResult.getRefreshToken())
	    		.tokenType(authenticationResult.getTokenType())
	    		.build();
		
		return token;
	}
	
	public void revoke(String accessToken) {
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
	
	private AuthenticationResultType authenticate(Map<String, String> authParams) {
		AuthenticationResultType authenticationResultType = null;
	    AWSCognitoIdentityProvider cognitoClient = getAmazonCognitoIdentityClient();
	 
	    final InitiateAuthRequest authRequest = new InitiateAuthRequest()
	    		.withAuthFlow(AuthFlowType.USER_PASSWORD_AUTH)
	    		.withClientId(COGNITO_CLIENT_ID)
	    		.withAuthParameters(authParams);
	    
	    InitiateAuthResult authResult = cognitoClient.initiateAuth(authRequest);
	    
	    if (StringUtils.isNotBlank(authResult.getChallengeName())) {
	    	if ("NEW_PASSWORD_REQUIRED".equals(authResult.getChallengeName())) {
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
	    		.withRegion(Regions.US_EAST_1)
	    		.build();
	}
}