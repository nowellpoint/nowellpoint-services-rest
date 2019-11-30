package com.nowellpoint.api.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
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
import javax.json.Json;
import javax.json.JsonObject;

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
import com.nowellpoint.services.rest.model.JsonWebKey;
import com.nowellpoint.services.rest.model.JsonWebKeys;
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
	private static JsonWebKeys keys;

	private static final char[] chars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'q', 'w', 'e', 'r', 't', 'z', 'u', 'i', 'o', 'p', 'a', 's',
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
				keys = JsonbUtil.fromJson(response.getEntity().getContent(), JsonWebKeys.class); 
			} else {
				logger.error(IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8.name()));
			}
			
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public String createUser(CreateUserRequest request) {
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
	
	public Token authenticate(String username, String password) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		final Map<String, String> authParams = new HashMap<>();
		authParams.put(USERNAME, username);  
		authParams.put(PASSWORD, password);
		
		AuthenticationResultType authenticationResult = authenticate(authParams);

		Jws<Claims> claims = getClaims(authenticationResult.getAccessToken());
	    
		String accessToken = generateAccessToken(claims);
		
	    Token token = Token.builder()
	    		.id(claims.getBody().getSubject())
	    		.accessToken(accessToken)
	    		.expiresIn(authenticationResult.getExpiresIn().longValue())
	    		.refreshToken(authenticationResult.getRefreshToken())
	    		.tokenType(authenticationResult.getTokenType())
				.build();
	    
	    authenticationEvent.fireAsync(claims.getBody());
		
		return token;
	}
	
	public Token refreshToken(String refreshToken) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		final Map<String, String> authParams = new HashMap<>();
		authParams.put(REFRESH_TOKEN, refreshToken);  
		
		AuthenticationResultType authenticationResult = refreshToken(authParams);

		Jws<Claims> claims = getClaims(authenticationResult.getAccessToken());
	    
	    String accessToken = generateAccessToken(claims);
	    
	    Token token = Token.builder()
	    		.id(claims.getBody().getSubject())
	    		.accessToken(accessToken)
	    		.expiresIn(authenticationResult.getExpiresIn().longValue())
	    		.refreshToken(refreshToken)
	    		.tokenType(authenticationResult.getTokenType())
	    		.build();
		
		return token;
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
	
	private String generateAccessToken(Jws<Claims> claims) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException {
		String keyStore = System.getProperty("javax.net.ssl.keyStore");
		char[] keyStorePassword = System.getProperty("javax.net.ssl.keyStorePassword").toCharArray();
		
		InputStream inputStream = new FileInputStream(keyStore);
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(inputStream, keyStorePassword);

        Key privateKey = keystore.getKey("selfsigned", keyStorePassword);
        
        Certificate certificate = keystore.getCertificate("selfsigned");
        PublicKey publicKey = certificate.getPublicKey();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;
        
        JsonObject jwk = Json.createObjectBuilder()
        		.add("kty", rsaPublicKey.getAlgorithm())
        		.add("kid", claims.getHeader().getKeyId())
        		.add("n", Base64.getUrlEncoder().encodeToString(rsaPublicKey.getModulus().toByteArray()))
        		.add("e", Base64.getUrlEncoder().encodeToString(rsaPublicKey.getPublicExponent().toByteArray()))
        		.add("alq", "RS256")
        		.add("use", "sig")
        		.build();
        
        JsonObject jwks = Json.createObjectBuilder()
        		.add("keys", Json.createArrayBuilder().add(jwk))
        		.build();
        
        System.out.println("******");
        System.out.println(jwks);
        System.out.println("******");
	    
	    return Jwts.builder()
				.setHeaderParam("kid", claims.getHeader().getKeyId())
				.setId(claims.getBody().getId())
				.setIssuer(claims.getBody().getIssuer())
				.setAudience("nowellpoint.com")
				.setSubject(claims.getBody().getSubject())
				.setExpiration(claims.getBody().getExpiration())
				.setIssuedAt(claims.getBody().getIssuedAt())
				.claim("groups", claims.getBody().get("cognito:groups"))
				.signWith(SignatureAlgorithm.valueOf(claims.getHeader().getAlgorithm()), privateKey)
				.compact();
	  
	}
	
	private Jws<Claims> getClaims(String token) {
		return Jwts.parser()
				.setSigningKeyResolver(new SigningKeyResolverAdapter() {
					@SuppressWarnings("rawtypes")
					public java.security.Key resolveSigningKey(JwsHeader jwsHeader, Claims claims) {
						JsonWebKey key = keys.getKey(jwsHeader.getKeyId()).get();
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
	
	private AWSCognitoIdentityProvider getCognitoIdentityClient() {
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