package com.nowellpoint.api.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;

import org.bson.Document;
import org.eclipse.microprofile.config.Config;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.nowellpoint.services.rest.model.Address;
import com.nowellpoint.services.rest.model.Connection;
import com.nowellpoint.services.rest.model.ConnectionRequest;
import com.nowellpoint.services.rest.model.ConnectionResult;
import com.nowellpoint.services.rest.model.ConnectionServiceException;
import com.nowellpoint.services.rest.model.Organization;
import com.nowellpoint.services.rest.model.OrganizationRequest;
import com.nowellpoint.services.rest.model.Subscription;
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
	
	public Organization create(OrganizationRequest organizationRequest) {
		
		Address address = Address.builder()
				.countryCode(organizationRequest.getCountryCode())
				.build();
		
		Subscription subscription = Subscription.builder()
				.name("Free")
				.planId("FREE")
				.price(Double.valueOf(0.00))
				.build();
		
		Organization organization = Organization.builder()
				.organizationType("Developer Edition")
    			.id(UUID.randomUUID().toString())
    			.address(address)
    			.subscription(subscription)
    			.name(organizationRequest.getName())
    			.createdOn(Instant.now())
    			.updatedOn(Instant.now())
    			.build();
		
		createOrUpdate(organization);
		
		return organization;
	}
	
	public Organization update(String id, OrganizationRequest organizationRequest) {
		
		Organization instance = findById(id);
		
		Address address = instance.getAddress().toBuilder()
				.countryCode(organizationRequest.getCountryCode())
				.locality(organizationRequest.getLocality())
				.postalCode(organizationRequest.getPostalCode())
				.region(organizationRequest.getRegion())
				.street(organizationRequest.getStreet())
				.build();
		
		Organization organization = instance.toBuilder()
				.address(address)
				.name(organizationRequest.getName())
				.updatedOn(Instant.now())
				.build();
		
		createOrUpdate(organization);
		
		return organization;
	}
	
	public Optional<Connection> getConnection(String organizationId, String connectionId) {
		Organization organization = findById(organizationId);
		return organization.getConnections()
				.stream()
				.filter(c -> c.getConnectionId().equals(connectionId))
				.findFirst();
	}
	
	public Connection createConnection(String organizationId, ConnectionRequest connectionRequest) {
		
		ConnectionResult connectionResult = salesforceService.connect(connectionRequest);
		
		if (connectionResult.isSuccess()) {
			
			String connectionString = encryptConnectionString(connectionRequest);
			
			Organization instance = findById(organizationId);
			
			Connection connection = Connection.builder()
					.authEndpoint(connectionResult.getAuthEndpoint())
					.connectedAs(connectionResult.getIdentity().getUsername())
					.connectedOn(Instant.now())
					.connectionString(connectionString)
					.createdOn(Instant.now())
					.connectionId(UUID.randomUUID().toString())
					.identity(connectionResult.getToken().getId())
					.instance(connectionResult.getToken().getInstanceUrl())
					.instanceName(connectionResult.getOrganization().getInstanceName())
					.sandbox(connectionResult.getOrganization().getIsSandbox())
					.status(Connection.CONNECTED)
	    			.updatedOn(Instant.now())
					.build();
			
			List<Connection> connections = instance.getConnections();
			connections.add(connection);
			
			Organization organization = instance.toBuilder().connections(connections).build();
			
			createOrUpdate(organization);
			
			return connection;
			
		} else {
			throw new ConnectionServiceException("CONNECTION_ERROR", connectionResult.getErrorMessage(), "Connection::create()", "Salesforce Authentication Failed");
		}
	}
	
	public Connection updateConnection(String organizationId, String connectionId, ConnectionRequest connectionRequest) {
		
		var connectionResult = salesforceService.connect(connectionRequest);
		
		if (connectionResult.isSuccess()) {
			
			var instance = findById(organizationId);
			var connectionString = encryptConnectionString(connectionRequest);
			
			Connection connection = instance.getConnections().stream().filter(c -> c.getConnectionId().equals(connectionId)).findFirst().get().toBuilder()
					.authEndpoint(connectionResult.getAuthEndpoint())
					.connectedAs(connectionResult.getIdentity().getUsername())
					.connectedOn(Instant.now())
					.connectionString(connectionString)
					.identity(connectionResult.getToken().getId())
					.instance(connectionResult.getToken().getInstanceUrl())
					.instanceName(connectionResult.getOrganization().getInstanceName())
					.sandbox(connectionResult.getOrganization().getIsSandbox())
					.status(Connection.CONNECTED)
	    			.updatedOn(Instant.now())
					.build();
			
			var connections = instance.getConnections();
			connections.add(connection);
			
			var organization = instance.toBuilder()
					.connections(connections)
					.build();
			
			createOrUpdate(organization);
			
			return connection;
			
		} else {
			throw new ConnectionServiceException("CONNECTION_ERROR", connectionResult.getErrorMessage(), "Connection::update()", "Salesforce Authentication Failed");
		}
	}
	
	public void onConnect(@ObservesAsync ConnectionResult connectionResult) {
		System.out.println("****** observed connectionResult: " + connectionResult.getAuthEndpoint());
//		var instance = findById(connectionResult.getConnectionId());
		
//		Connection connection = instance.getConnections().stream().filter(c -> c.getId().equals(connectionResult.getConnectionId())).findAny().get().toBuilder()
//				.connectedOn(connectionResult.isSuccess() ? Instant.now() : instance.g)
//				.status(connectionResult.isSuccess() ? Connection.CONNECTED : Connection.FAILED)
//				.status(connectionResult.isSuccess() ? null : connectionResult.getErrorMessage())
//				.build();
//		
//		createOrUpdate(organization);
		
	}
	
	private String encryptConnectionString(ConnectionRequest connectionRequest) {
		var secretKey = config.getValue(ConfigProperties.AWS_SECRET_ACCESS_KEY, String.class);
		return SecureValue.encryptBase64(secretKey, new StringBuilder().append(connectionRequest.getUsername())
				.append(" ")
				.append(connectionRequest.getPassword())
				.append(" ")
				.append(connectionRequest.getClientId())
				.append(" ")
				.append(connectionRequest.getClientSecret())
				.toString());
	}

	private void createOrUpdate(Organization organization) {
		getCollection().findOneAndReplace(new Document("_id", organization.getId()), organization, new FindOneAndReplaceOptions().upsert(Boolean.TRUE));
	}

    private MongoCollection<Organization> getCollection() {
        return getDatabase().getCollection("organizations", Organization.class);
    }
}