package com.nowellpoint.api.service;

import java.time.Instant;

import javax.enterprise.context.RequestScoped;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.nowellpoint.client.sforce.Authenticators;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthRequests;
import com.nowellpoint.client.sforce.Salesforce;
import com.nowellpoint.client.sforce.SalesforceClientBuilder;
import com.nowellpoint.client.sforce.UsernamePasswordGrantRequest;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.services.rest.model.Address;
import com.nowellpoint.services.rest.model.Connection;
import com.nowellpoint.services.rest.model.ConnectionRequest;
import com.nowellpoint.services.rest.model.Organization;
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
		
		Organization instance = find(client.getOrganization().getId());
		
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
				.connectedAs(client.getIdentity().getUsername())
				.connectedOn(Instant.now())
				.connectionString(connectionString)
				.identity(token.getId())
				.instance(token.getInstanceUrl())
				.build();
		
		Organization organization = Organization.builder()
				.connection(connection)
				.organizationType(client.getOrganization().getOrganizationType())
    			.id(client.getOrganization().getId())
    			.address(address)
    			.name(client.getOrganization().getName())
    			.createdOn(instance != null ? instance.getCreatedOn() : Instant.now())
    			.updatedOn(Instant.now())
    			.build();
		
		createOrUpdate(organization);
		
		return organization;
	}
	
	public Organization find(String id) {
		return getCollection().find(new Document("_id", id)).first();
	}
	
	private void createOrUpdate(Organization organization) {
		getCollection().findOneAndReplace(new Document("_id", organization.getId()), organization, new FindOneAndReplaceOptions().upsert(Boolean.TRUE));
	}

    private MongoCollection<Organization> getCollection() {
        return getDatabase().getCollection("organizations", Organization.class);
    }
}