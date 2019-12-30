package com.nowellpoint.services.rest.model;

import java.time.Instant;

import javax.json.bind.annotation.JsonbTransient;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.UriBuilder;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

import com.nowellpoint.services.rest.JaxRsActivator;
import com.nowellpoint.services.rest.ConnectionResource;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder=true)
@RegisterForReflection
public class Connection {
	private String connectionId;
	private String instanceName;
	private String authEndpoint;
	private String identity;
	private String connectedAs;
	private Instant connectedOn;
	private String status;
	private Boolean sandbox;
	private String instance;
	private String error;
	private Instant createdOn;
	private Instant updatedOn;
	private @JsonbTransient String connectionString;
	
	public static final String CONNECTED = "CONNECTED";
	public static final String FAILED = "FAILED";
	
	
	@BsonIgnore
	public Attributes getAttributes() {
		return Attributes.builder()
				.href(UriBuilder.fromPath(JaxRsActivator.class.getAnnotation(ApplicationPath.class).value())
						.path(ConnectionResource.class)
						.path("{id}")
						.build(getConnectionId())
						.toString())
				.type(Organization.class.getSimpleName())
				.build();
	}
	
	@BsonCreator
	public Connection(
			@BsonProperty("connectionId") String connectionId,
			@BsonProperty("instanceName") String instanceName,
			@BsonProperty("authEndpoint") String authEndpoint,
			@BsonProperty("identity") String identity, 
			@BsonProperty("connectedAs") String connectedAs, 
			@BsonProperty("connectedOn") Instant connectedOn,
			@BsonProperty("status") String status,
			@BsonProperty("sandbox") Boolean sandbox,
			@BsonProperty("instance") String instance,
			@BsonProperty("error") String error,
			@BsonProperty("createdOn") Instant createdOn,
			@BsonProperty("updatedOn") Instant updatedOn,
			@BsonProperty("connectionString") String connectionString) {
		
		this.connectionId = connectionId;
		this.instanceName = instanceName;
		this.authEndpoint = authEndpoint;
		this.identity = identity;
		this.connectedAs = connectedAs;
		this.connectedOn = connectedOn;
		this.status = status;
		this.sandbox = sandbox;
		this.instance = instance;
		this.error = error;
		this.createdOn = createdOn;
		this.updatedOn = updatedOn;
		this.connectionString = connectionString;
	}
}