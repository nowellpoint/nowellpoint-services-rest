package com.nowellpoint.services.rest;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;

import com.mongodb.client.MongoClient;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@Readiness
@RequestScoped
public class DatabaseConnectionHealthCheck implements HealthCheck {
	
	@Inject 
	protected MongoClient mongoClient;
	

	@Override
	public HealthCheckResponse call() {
		
		HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("Database connection health check");

        try {
        	mongoClient.listDatabaseNames();
            responseBuilder.up();
        } catch (Exception e) {
            responseBuilder.down();
        }

        return responseBuilder.build();
	}
}