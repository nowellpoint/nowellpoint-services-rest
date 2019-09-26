package com.nowellpoint.api.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.bson.Document;

import com.mongodb.client.MongoClient;
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

@ApplicationScoped
public class OrganizationService {
	
	@Inject MongoClient mongoClient;
	
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
    			.id(token.getId())
    			.instanceUrl(token.getInstanceUrl())
    			.organizationId(client.getOrganization().getId())
    			.username(request.getUsername())
    			.build();
		
		add(organization);
		
		return organization;
	}
	
	public void add(Organization organization){
        Document document = new Document()
                .append("_id", organization.getId())
                .append("instanceUrl", organization.getInstanceUrl())
                .append("organizationId", organization.getOrganizationId())
                .append("username", organization.getUsername());
        
        getCollection().insertOne(document);
    }

    private MongoCollection<Document> getCollection() {
        return mongoClient.getDatabase("nowellpoint").getCollection("organizations");
    }
}