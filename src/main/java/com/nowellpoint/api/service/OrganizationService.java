package com.nowellpoint.api.service;

import javax.enterprise.context.RequestScoped;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.nowellpoint.api.model.Address;
import com.nowellpoint.api.model.Connection;
import com.nowellpoint.api.model.ConnectionRequest;
import com.nowellpoint.api.model.Organization;
import com.nowellpoint.client.sforce.Authenticators;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthRequests;
import com.nowellpoint.client.sforce.Salesforce;
import com.nowellpoint.client.sforce.SalesforceClientBuilder;
import com.nowellpoint.client.sforce.UsernamePasswordGrantRequest;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.util.SecureValue;

@RequestScoped
public class OrganizationService extends AbstractService {
	
	public OrganizationService() {
		
	}
	
	public Organization findById(String id) {
		return getCollection().find(new Document("_id", id)).first();
	}
	
	public Organization build(ConnectionRequest request) {
		
		UsernamePasswordGrantRequest authRequest = OauthRequests.PASSWORD_GRANT_REQUEST.builder()
				.setClientId(request.getClientId())
				.setClientSecret(request.getClientSecret())
				.setUsername(request.getUsername())
				.setPassword(request.getPassword())
				.build();
		
		OauthAuthenticationResponse response = Authenticators.PASSWORD_GRANT_AUTHENTICATOR
				.authenticate(authRequest);
		
		Token token = response.getToken();
		
		String connectionString = SecureValue.encryptBase64(new StringBuilder().append(request.getUsername())
				.append("|")
				.append(request.getPassword())
				.append("|")
				.append(request.getClientId())
				.append("|")
				.append(request.getClientSecret())
				.toString());
		
		Salesforce client = SalesforceClientBuilder.defaultClient(token);
		
		Address address = Address.builder()
				.city(client.getOrganization().getAddress().getCity())
				.country(client.getOrganization().getAddress().getCountry())
				.countryCode(client.getOrganization().getAddress().getCountryCode())
				.postalCode(client.getOrganization().getAddress().getPostalCode())
				.state(client.getOrganization().getAddress().getState())
				.stateCode(client.getOrganization().getAddress().getStateCode())
				.street(client.getOrganization().getAddress().getStreet())
				.build();
		
		Connection connection = Connection.builder()
				.connectedAs(request.getUsername())
				.connectionString(connectionString)
				.id(token.getId())
				.instanceUrl(token.getInstanceUrl())
				.build();
		
		Organization organization = Organization.builder()
				.connection(connection)
    			.id(client.getOrganization().getId())
    			.instanceUrl(token.getInstanceUrl())
    			.address(address)
    			.name(client.getOrganization().getName())
    			.build();
		
		insert(organization);
		
		return organization;
	}
	
	public Organization find(String id) {
		return getCollection().find(new Document("_id", id)).first();
	}
	
	private void insert(Organization organization) {        
        getCollection().insertOne(organization);
    }
	
	private void replace(Organization organization) {
		getCollection().replaceOne(new Document("_id", organization.getId()), organization);
	}

    private MongoCollection<Organization> getCollection() {
        return getDatabase().getCollection("organizations", Organization.class);
    }
}