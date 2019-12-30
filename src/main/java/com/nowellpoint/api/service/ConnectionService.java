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
import com.nowellpoint.services.rest.model.Connection;
import com.nowellpoint.services.rest.model.ConnectionRequest;
import com.nowellpoint.services.rest.model.ConnectionResult;
import com.nowellpoint.services.rest.model.Organization;
import com.nowellpoint.services.rest.model.ServiceException;
import com.nowellpoint.services.rest.util.ConfigProperties;
import com.nowellpoint.services.rest.util.SecureValue;

@RequestScoped
public class ConnectionService extends AbstractService {
	
	@Inject
	Config config;
	
	@Inject
	SalesforceService salesforceService;
	
	public ConnectionService() {
		
	}
	
	public Organization findById(String id) {
		return getCollection().find(new Document("_id", id)).first();
	}
	
	public Optional<Connection> findById(String organizationId, String connectionId) {
		Organization organization = findById(organizationId);
		return organization.getConnections()
				.stream()
				.filter(c -> c.getConnectionId().equals(connectionId))
				.findFirst();
	}
	
	public Connection create(String organizationId, ConnectionRequest connectionRequest) {
		
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
			throw new ServiceException(connectionResult.getErrorMessage());
		}
	}
	
	public Connection update(String organizationId, String connectionId, ConnectionRequest connectionRequest) {
		
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
			throw new ServiceException(connectionResult.getErrorMessage());
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