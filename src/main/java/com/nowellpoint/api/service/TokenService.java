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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.jboss.logging.Logger;

import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.nowellpoint.api.model.Key;
import com.nowellpoint.api.model.Keys;
import com.nowellpoint.api.model.Token;
import com.nowellpoint.api.util.JsonbUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SigningKeyResolverAdapter;

@ApplicationScoped
public class TokenService extends AbstractService {
	
	@Inject
	Logger logger;
	
	@Inject
	IdentityProviderService identityProviderService;
	
	private static final String COGNITO_USER_POOL_ID = "us-east-";
	
	private static Keys keys;
	
	@PostConstruct
	public void init() {
		try {
			URIBuilder builder = new URIBuilder(String.format("https://cognito-idp.%s.amazonaws.com/%s/.well-known/jwks.json", 
		    		System.getenv("AWS_REGION"), 
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

	public Token authorize(String username, String password) throws UnsupportedEncodingException {
		
	    AuthenticationResultType authenticationResult = identityProviderService.authenticate(username, password);
		
	    Jws<Claims> claims = getClaims(authenticationResult.getAccessToken());
	    
	    String jwt = Jwts.builder()
				.setHeaderParam("kid", claims.getHeader().getKeyId())
				.setId(claims.getBody().getId())
				.setIssuer(claims.getBody().getIssuer())
				.setAudience("nowellpoint.com")
				.setSubject(claims.getBody().getSubject())
				.setExpiration(claims.getBody().getExpiration())
				.setIssuedAt(claims.getBody().getIssuedAt())
				//.claim("scope", claims.getBody().get("groups"))
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
	
	@PreDestroy() 
	public void sthudown() {
		
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
}