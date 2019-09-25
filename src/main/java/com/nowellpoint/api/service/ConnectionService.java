package com.nowellpoint.api.service;

import javax.enterprise.context.ApplicationScoped;

import com.nowellpoint.api.model.Connection;
import com.nowellpoint.api.model.ConnectionRequest;
import com.nowellpoint.client.sforce.Authenticators;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthException;
import com.nowellpoint.client.sforce.OauthRequests;
import com.nowellpoint.client.sforce.UsernamePasswordGrantRequest;
import com.nowellpoint.client.sforce.model.Token;

@ApplicationScoped
public class ConnectionService {
	
	public Connection create(ConnectionRequest request) {
		
		System.out.println(request.getClientId());
		System.out.println(request.getClientSecret());
		System.out.println(request.getPassword());
		System.out.println(request.getSecurityToken());
		System.out.println(request.getUsername());
		
		UsernamePasswordGrantRequest authRequest = OauthRequests.PASSWORD_GRANT_REQUEST.builder()
				.setClientId(request.getClientId())
				.setClientSecret(request.getClientSecret())
				.setUsername(request.getUsername())
				.setPassword(request.getPassword())
				.setSecurityToken(request.getSecurityToken())
				.build();
		
		try {
			OauthAuthenticationResponse response = Authenticators.PASSWORD_GRANT_AUTHENTICATOR
					.authenticate(authRequest);
			
			Token token = response.getToken();
			
			//client = SalesforceClientBuilder.defaultClient(token);
			
//			assertNotNull(token);
//			assertNotNull(token.getAccessToken());			
//			assertNotNull(token.getId());
//			assertNotNull(token.getInstanceUrl());
//			assertNotNull(token.getIssuedAt());
//			assertNotNull(token.getSignature());
//			assertNotNull(token.getTokenType());
			
			Connection connection = Connection.builder()
	    			.id(token.getId())
	    			.organizationId("5003f000000lRLTAA2")
	    			.username(request.getUsername())
	    			.build();
			
			return connection;
			
		} catch (OauthException e) {
			//System.out.println(e.getStatusCode());
			System.out.println(e.getMessage());
			//System.out.println(e.getErrorDescription());
		} 	
		
		return null;
	}
}