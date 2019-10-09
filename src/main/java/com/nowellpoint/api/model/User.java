package com.nowellpoint.api.model;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class User {
	private @BsonId String id;
	private String firstName;
	private String lastName;
	private String email;
	private String phone;
	private String timeZone;
	
	@BsonCreator
	public User(
			@BsonId String id, 
			@BsonProperty("firstName") String firstName, 
			@BsonProperty("lastName") String lastName, 
			@BsonProperty("email") String email,
			@BsonProperty("phone") String phone,
			@BsonProperty("timeZone") String timeZone) {
		
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phone = phone;
		this.timeZone = timeZone;
	}
}