package com.nowellpoint.api.service;

import java.time.Instant;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.bson.Document;
import org.eclipse.microprofile.config.Config;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.nowellpoint.services.rest.model.Address;
import com.nowellpoint.services.rest.model.Connection;
import com.nowellpoint.services.rest.model.ConnectionRequest;
import com.nowellpoint.services.rest.model.ConnectionResult;
import com.nowellpoint.services.rest.model.Organization;
import com.nowellpoint.services.rest.model.ServiceException;
import com.nowellpoint.services.rest.util.ConfigProperties;
import com.nowellpoint.services.rest.util.SecureValue;

@RequestScoped
public class OrganizationService extends AbstractService {
	
	@Inject
	Config config;
	
	@Inject
	SalesforceService salesforceService;
	
	public OrganizationService() {
		
	}
	
	public Organization findById(String id) {
		return getCollection().find(new Document("_id", id)).first();
	}
	
	public Organization build(ConnectionRequest request) {
		
		ConnectionResult connectionResult = salesforceService.connect(request);
		
		if (connectionResult.isSuccess()) {
			
			String key = config.getValue(ConfigProperties.AWS_SECRET_ACCESS_KEY, String.class);
			
			String connectionString = SecureValue.encryptBase64(key, new StringBuilder().append(request.getUsername())
					.append("|")
					.append(request.getPassword())
					.append("|")
					.append(request.getClientId())
					.append("|")
					.append(request.getClientSecret())
					.toString());
			
			Organization instance = find(connectionResult.getOrganization().getId());
			
			Address address = Address.builder()
					.city(connectionResult.getOrganization().getAddress().getCity())
					.country(connectionResult.getOrganization().getAddress().getCountry())
					.countryCode(connectionResult.getOrganization().getAddress().getCountryCode())
					.postalCode(connectionResult.getOrganization().getAddress().getPostalCode())
					.state(connectionResult.getOrganization().getAddress().getState())
					.stateCode(connectionResult.getOrganization().getAddress().getStateCode())
					.street(connectionResult.getOrganization().getAddress().getStreet())
					.build();
			
			Connection connection = Connection.builder()
					.connectedAs(connectionResult.getIdentity().getUsername())
					.connectedOn(Instant.now())
					.connectionString(connectionString)
					.identity(connectionResult.getToken().getId())
					.instance(connectionResult.getToken().getInstanceUrl())
					.build();
			
			Organization organization = Organization.builder()
					.connection(connection)
					.organizationType(connectionResult.getOrganization().getOrganizationType())
	    			.id(connectionResult.getOrganization().getId())
	    			.address(address)
	    			.name(connectionResult.getOrganization().getName())
	    			.createdOn(instance != null ? instance.getCreatedOn() : Instant.now())
	    			.updatedOn(Instant.now())
	    			.build();
			
			createOrUpdate(organization);
			
			return organization;
		} else {
			throw new ServiceException(connectionResult.getErrorMessage());
		}
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