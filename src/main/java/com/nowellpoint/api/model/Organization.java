package com.nowellpoint.api.model;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.UriBuilder;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

import com.nowellpoint.api.JaxRsActivator;
import com.nowellpoint.api.OrganizationResource;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder=true)
public class Organization {
	private @BsonId String id;
	private String name;
	private String instanceUrl;
	private String organizationType;
	private Connection connection;
	private Address address;
	
	@BsonIgnore
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
			@BsonProperty("instanceUrl") String instanceUrl,
			@BsonProperty("organizationType") String organizationType,
			@BsonProperty("connection") Connection connection,
			@BsonProperty("address") Address address) {
		
		this.id = id;
		this.name = name;
		this.instanceUrl = instanceUrl;
		this.organizationType = organizationType;
		this.connection = connection;
		this.address = address;
	}
}