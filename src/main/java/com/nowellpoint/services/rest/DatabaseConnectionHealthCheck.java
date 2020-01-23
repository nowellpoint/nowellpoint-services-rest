package com.nowellpoint.services.rest;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;

import com.mongodb.client.MongoClient;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@Readiness
@RequestScoped
public class DatabaseConnectionHealthCheck implements HealthCheck {
	
	@Inject 
	protected MongoClient mongoClient;
	
	@Override
	public HealthCheckResponse call() {
		
		Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
		mongoLogger.setLevel(Level.SEVERE); 
		
		HealthCheckResponseBuilder builder = HealthCheckResponse.named("MongoDB connection health check").up();
        try {
            StringBuilder databases = new StringBuilder();
            for (String db : mongoClient.listDatabaseNames()) {
                if (databases.length() != 0) {
                    databases.append(", ");
                }
                databases.append(db);
            }
            return builder.withData("databases", databases.toString()).build();
        } catch (Exception e) {
            return builder.down().withData("reason", e.getMessage()).build();
        }    
	}
}