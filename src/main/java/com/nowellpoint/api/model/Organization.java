package com.nowellpoint.api.model;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Organization {
	private @BsonId String id;
	private String username;
	private String organizationId;
	private String instanceUrl;
	
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