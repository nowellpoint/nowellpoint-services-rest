package com.nowellpoint.api.service;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.microprofile.config.Config;
import org.jboss.logging.Logger;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.lang.JoseException;

import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.nowellpoint.services.rest.IdentityResource;
import com.nowellpoint.services.rest.JaxRsActivator;
import com.nowellpoint.services.rest.model.AccessTokenRequest;
import com.nowellpoint.services.rest.model.IdentityProviderServiceException;
import com.nowellpoint.services.rest.model.LoggedInEvent;
import com.nowellpoint.services.rest.model.Token;
import com.nowellpoint.services.rest.model.User;
import com.nowellpoint.services.rest.model.UserRequest;

import io.quarkus.mailer.ReactiveMailer;

@ApplicationScoped
public class IdentityProviderService {
	
	private static final String USERNAME                  = "USERNAME";
	private static final String PASSWORD                  = "PASSWORD";
	private static final String REFRESH_TOKEN             = "REFRESH_TOKEN";
	
	@Inject
	Config config;
	
	@Inject
	CognitoService cognitoService;
	
	@Inject
	CryptographyService cryptogrpahyService;
	
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
	
	/**
     * <p>
     * The Identity provider service method to create a <code>user</code> in the given user pool.
     * </p>
     * 
     * @return Returns the subject for the newly created user
     */
	
	public String createUser(UserRequest userRequest) {
		return cognitoService.createUser(userRequest);
	}
	
	/**
     * <p>
     * The Identity provider service method to authenticate the user the given <code>username</code> and <code>password</code>.
     * </p>
     * 
     * @return Returns the token object with the new access token that was created during the authentication step
     */
	
	public Token authenticate(String username, String password) {
		final Map<String, String> authParams = new HashMap<>();
		authParams.put(USERNAME, username);  
		authParams.put(PASSWORD, password);
		
		Token token = null;
		try {
			AuthenticationResultType authenticationResult = cognitoService.authenticate(authParams);
			token = createToken(authenticationResult);
		} catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | JoseException | InvalidJwtException | MalformedClaimException e) {
			logger.error(e);
			throw new IdentityProviderServiceException(500, e.getClass().getSimpleName().toUpperCase(), ExceptionUtils.getStackTrace(e), "authenticate()", e.getMessage());
		}
		
		return token;
	}
	
	/**
     * <p>
     * The Identity provider service method to refresh the given <code>access token</code>.
     * </p>
     * 
     * @return Returns the token object with the new access token that was created during the refresh token step
     */
	
	public Token refreshToken(String refreshToken) {
		final Map<String, String> authParams = new HashMap<>();
		authParams.put(REFRESH_TOKEN, refreshToken);  
		
		Token token = null;
		try {
			AuthenticationResultType authenticationResult = cognitoService.refreshToken(authParams);
			authenticationResult.setRefreshToken(refreshToken);
			token = createToken(authenticationResult);
		} catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | JoseException | InvalidJwtException | MalformedClaimException e) {
			logger.error(e);
			throw new IdentityProviderServiceException(500, e.getClass().getSimpleName().toUpperCase(), ExceptionUtils.getStackTrace(e), "refreshToken()", e.getMessage());
		}
		
		return token;
	}
	
	/**
     * <p>
     * The Identity provider service method to revoke the given <code>access token</code> with a global sign out.
     * </p>
     * 
     * @return void
     */
	
	public void revokeToken(String accessToken) {
		cognitoService.revokeToken(accessToken);
	}
	
	/**
     * <p>
     * The Identity provider service method to disable a <code>user</code> in the given user pool.
     * </p>
     * 
     * @return void
     */
	
	public void disableUser(String username) {
		cognitoService.disableUser(username);
	}
	
	/**
     * <p>
     * The Identity provider service method to delete a <code>user</code> from the given user pool.
     * </p>
     * 
     * @return void
     */
	
	public void deleteUser(String username) {
		cognitoService.deleteUser(username);
	}
	
	/**
     * <p>
     * The Identity provider service method to create an <code>access token</code> for the logged in user.
     * </p>
     * 
     * @return void
     */
	
	private Token createToken(AuthenticationResultType authenticationResult) throws InvalidJwtException, JoseException, MalformedClaimException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		
		/**
		 * parse the access token to get the claims
		 */
		
		JwtClaims claims = cognitoService.getClaims(authenticationResult.getAccessToken());
		
		/**
		 * call the user service to get the saved user
		 */
		
		User user = userService.findById(claims.getSubject());
		
		/**
		 * generate the new accessToken 
		 */
		
		AccessTokenRequest accessTokenRequest = AccessTokenRequest.builder()
				.audience(user.getOrganizationId())
				.expiresAt(claims.getExpirationTime().getValue())
				.id(claims.getJwtId())
				.issuedAt(claims.getIssuedAt().getValue())
				.issuer(claims.getIssuer())
				.subject(claims.getSubject())
				.build();
		
		String accessToken = cryptogrpahyService.generateAccessToken(accessTokenRequest);
		
		/**
		 * build the authentication token
		 */
		
		Token token = Token.builder()
				.id(getId())
				.accessToken(accessToken)
				.expiresIn(authenticationResult.getExpiresIn().longValue())
				.refreshToken(authenticationResult.getRefreshToken())
				.tokenType(authenticationResult.getTokenType())
				.build();
		
		/**
		 * fire the token event
		 */
		
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
	}
	
	private String getId() {
		return UriBuilder.fromPath("http://localhost:8080" + JaxRsActivator.class.getAnnotation(ApplicationPath.class).value()).path(IdentityResource.class)
				.build()
				.toString();
	}
}