package com.nowellpoint.api.model;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Connection {
	private String id;
	private String connectedAs;
	private String instanceUrl;
	private String connectionString;
	
	@BsonCreator
	public Connection(
			@BsonProperty("id") String id, 
			@BsonProperty("connectedAs") String connectedAs, 
			@BsonProperty("connectionString") String connectionString, 
			@BsonProperty("instanceUrl") String instanceUrl) {
		
		this.id = id;
		this.connectedAs = connectedAs;
		this.connectionString = connectionString;
		this.instanceUrl = instanceUrl;
	}
}