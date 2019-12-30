package com.nowellpoint.services.rest.model;

import java.time.Instant;
import java.util.List;

import javax.json.bind.annotation.JsonbProperty;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.UriBuilder;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

import com.nowellpoint.services.rest.JaxRsActivator;
import com.nowellpoint.services.rest.OrganizationResource;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

@Value
@Builder(toBuilder=true)
@RegisterForReflection
public class Organization {
	private @BsonId String id;
	private String name;
	private String organizationType;
	private Instant createdOn;
	private Instant updatedOn;
	private @Singular List<Connection> connections;
	private Address address;
	private Subscription subscription;
	
	@BsonIgnore
	@JsonbProperty("attributes")
	public Attributes getAttributes() {
		return Attributes.builder()
				.href(UriBuilder.fromPath(JaxRsActivator.class.getAnnotation(ApplicationPath.class).value())
						.path(OrganizationResource.class)
						.path("{id}")
						.build(getId())
						.toString())
				.type(Organization.class.getSimpleName())
				.build();
	}
	
	@BsonCreator
	public Organization(
			@BsonId String id,  
			@BsonProperty("name") String name,
			@BsonProperty("organizationType") String organizationType,
			@BsonProperty("createdOn") Instant createdOn,
			@BsonProperty("updatedOn") Instant updatedOn,
			@BsonProperty("connections") List<Connection> connections,
			@BsonProperty("address") Address address,
			@BsonProperty("subscription") Subscription subscription) {
		
		this.id = id;
		this.name = name;
		this.organizationType = organizationType;
		this.createdOn = createdOn;
		this.updatedOn = updatedOn;
		this.connections = connections;
		this.address = address;
		this.subscription = subscription;
	}
}