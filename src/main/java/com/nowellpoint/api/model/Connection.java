package com.nowellpoint.api.model;

import java.time.Instant;

import javax.json.bind.annotation.JsonbTransient;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Connection {
	private String identity;
	private String connectedAs;
	private Instant connectedOn;
	private @JsonbTransient String connectionString;
	private String instance;
	
	@BsonCreator
	public Connection(
			@BsonProperty("identity") String identity, 
			@BsonProperty("connectedAs") String connectedAs, 
			@BsonProperty("connectedOn") Instant connectedOn,
			@BsonProperty("connectionString") String connectionString, 
			@BsonProperty("instance") String instance) {
		
		this.identity = identity;
		this.connectedAs = connectedAs;
		this.connectedOn = connectedOn;
		this.connectionString = connectionString;
		this.instance = instance;
	}
}