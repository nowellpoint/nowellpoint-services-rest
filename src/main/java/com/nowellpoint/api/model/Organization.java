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
@Builder
public class Organization {
	private @BsonId String id;
	private String username;
	private String organizationId;
	private String instanceUrl;
	
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
			@BsonProperty("username") String username, 
			@BsonProperty("organizationId") String organizationId, 
			@BsonProperty("instanceUrl") String instanceUrl) {
		
		this.id = id;
		this.username = username;
		this.organizationId = organizationId;
		this.instanceUrl = instanceUrl;
	}
}