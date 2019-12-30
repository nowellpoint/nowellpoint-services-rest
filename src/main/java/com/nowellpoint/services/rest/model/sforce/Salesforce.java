package com.nowellpoint.services.rest.model.sforce;

public class Salesforce {
	
	public static Datastore createDatastore(AuthenticationRequest authenticationRequest) {
		return new DatastoreImpl(authenticationRequest);
	}
}