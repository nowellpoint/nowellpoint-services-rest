package com.nowellpoint.api.service;

import javax.enterprise.context.RequestScoped;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.nowellpoint.api.model.Organization;
import com.nowellpoint.api.model.OrganizationRequest;
import com.nowellpoint.client.sforce.Authenticators;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthRequests;
import com.nowellpoint.client.sforce.Salesforce;
import com.nowellpoint.client.sforce.SalesforceClientBuilder;
import com.nowellpoint.client.sforce.UsernamePasswordGrantRequest;
import com.nowellpoint.client.sforce.model.Token;

@RequestScoped
public class OrganizationService extends AbstractService {
	
	public OrganizationService() {
		
	}
	
	public Organization create(OrganizationRequest request) {
		
		UsernamePasswordGrantRequest authRequest = OauthRequests.PASSWORD_GRANT_REQUEST.builder()
				.setClientId(request.getClientId())
				.setClientSecret(request.getClientSecret())
				.setUsername(request.getUsername())
				.setPassword(request.getPassword())
				.build();
		
		OauthAuthenticationResponse response = Authenticators.PASSWORD_GRANT_AUTHENTICATOR
				.authenticate(authRequest);
		
		Token token = response.getToken();
		
		Salesforce client = SalesforceClientBuilder.defaultClient(token);
		
		Organization organization = Organization.builder()
    			.id(client.getOrganization().getId())
    			.instanceUrl(token.getInstanceUrl())
    			.organizationId(token.getId())
    			.username(request.getUsername())
    			.build();
		
		insert(organization);
		
		return organization;
	}
	
	public void insert(Organization organization){        
        getCollection().insertOne(organization);
    }
	
	public Organization find(String id) {
		return getCollection().find(new Document("_id", id)).first();
	}

    private MongoCollection<Organization> getCollection() {
        return getDatabase().getCollection("organizations", Organization.class);
    }
}