package com.nowellpoint.api.service;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.nowellpoint.services.rest.model.Connection;
import com.nowellpoint.services.rest.model.ConnectionResult;
import com.nowellpoint.services.rest.model.sforce.AuthenticationRequest;
import com.nowellpoint.services.rest.model.sforce.Datastore;
import com.nowellpoint.services.rest.model.sforce.Salesforce;
import com.nowellpoint.services.rest.model.sforce.SalesforceServiceException;
import com.nowellpoint.services.rest.util.AWSConfiguration;

public class AbstractService {
	
	private static CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), 
			fromProviders(PojoCodecProvider.builder().automatic(true).build()));
	
	@Inject 
	protected MongoClient mongoClient;
	
	@Inject
	protected Event<ConnectionResult> connectionEvent;
	
	@Inject
	protected AWSConfiguration awsConfiguration;
	
	@Inject
	protected CryptographyService cryptographyService;
	
	protected MongoDatabase getDatabase() {    	
        return mongoClient.getDatabase("nowellpoint").withCodecRegistry(codecRegistry);
    }
	
	protected Datastore createDatastore(Connection connection) {

		var connectionString = cryptographyService.decryptBase64(connection.getConnectionString());
		
		var params = connectionString.split(" ", 4);
		
		var username = params[0];
		var password = params[1];
		var clientId = params[2];
		var clientSecret = params[3];
		
		var authenticationRequest = AuthenticationRequest.builder()
				.authEndpoint(connection.getAuthEndpoint())
				.clientId(clientId)
				.clientSecret(clientSecret)
				.connectionId(connection.getConnectionId())
				.password(password)
				.username(username)
				.build();
		
		return createDatastore(authenticationRequest);
	}
	
	protected Datastore createDatastore(AuthenticationRequest authenticationRequest) {
		var datastore = Salesforce.createDatastore(authenticationRequest);
		
		ConnectionResult connectionResult = null;
		
		try {
			connectionResult = ConnectionResult.builder()
					.authEndpoint(authenticationRequest.getAuthEndpoint())
					.connectionId(authenticationRequest.getConnectionId())
					.token(datastore.getToken())
					.identity(datastore.getIdentity())
					.organization(datastore.getOrganization())
					.success(Boolean.TRUE)
					.build();
		} catch (SalesforceServiceException e) {
			connectionResult = ConnectionResult.builder()
					.authEndpoint(authenticationRequest.getAuthEndpoint())
					.connectionId(authenticationRequest.getConnectionId())
					.errorMessage(e.getMessage())
					.success(Boolean.FALSE)
					.build();
		} finally {
			connectionEvent.fireAsync(connectionResult);
		}
		
		return datastore;
	}
}