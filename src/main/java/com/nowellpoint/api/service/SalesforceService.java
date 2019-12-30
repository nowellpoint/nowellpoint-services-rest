package com.nowellpoint.api.service;

import javax.enterprise.context.RequestScoped;

import com.nowellpoint.services.rest.model.ConnectionRequest;
import com.nowellpoint.services.rest.model.ConnectionResult;
import com.nowellpoint.services.rest.model.sforce.AuthenticationRequest;
import com.nowellpoint.services.rest.model.sforce.Datastore;
import com.nowellpoint.services.rest.model.sforce.Identity;
import com.nowellpoint.services.rest.model.sforce.Organization;
import com.nowellpoint.services.rest.model.sforce.Salesforce;
import com.nowellpoint.services.rest.model.sforce.SalesforceServiceException;
import com.nowellpoint.services.rest.model.sforce.Token;

@RequestScoped
public class SalesforceService {
	
	public ConnectionResult connect(ConnectionRequest request) {
		
		AuthenticationRequest.Instance instance = AuthenticationRequest.Instance.valueOf(request.getInstance().toUpperCase());
		
		AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
				.authEndpoint(instance.getAuthEndpoint())
				.clientId(request.getClientId())
				.clientSecret(request.getClientSecret())
				.password(request.getPassword())
				.username(request.getUsername())
				.build();
		
		ConnectionResult connectionResult = null;
		
		try {
			
			Datastore datastore = Salesforce.createDatastore(authenticationRequest);
			
			Token token = datastore.getToken();
			
			Identity identity = datastore.getIdentity();

			Organization organization = datastore.getOrganization();
			
			connectionResult = ConnectionResult.builder()
					.authEndpoint(instance.getAuthEndpoint())
					.token(token)
					.identity(identity)
					.organization(organization)
					.success(Boolean.TRUE)
					.build();
			
		} catch (SalesforceServiceException e) {
			connectionResult = ConnectionResult.builder()
					.authEndpoint(authenticationRequest.getAuthEndpoint())
					.errorMessage(e.getMessage())
					.success(Boolean.FALSE)
					.build();
			
		}
		
		return connectionResult;
	}
}