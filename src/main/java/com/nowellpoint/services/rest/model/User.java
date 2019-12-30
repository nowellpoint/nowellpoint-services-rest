package com.nowellpoint.services.rest.model;

import java.time.Instant;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.UriBuilder;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

import com.nowellpoint.services.rest.JaxRsActivator;
import com.nowellpoint.services.rest.UserResource;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder=true)
@RegisterForReflection
public class User {
	private @BsonId String id;
	private String firstName;
	private String lastName;
	private String email;
	private Boolean emailVerified;
	private String username;
	private String phone;
	private String timeZone;
	private String locale;
	private Instant lastLoggedIn;
	private Instant createdOn;
	private Instant updatedOn;
	private String organizationId;
	
	public String getName() {
		return firstName != null ? firstName.concat(" ").concat(lastName) : lastName;
	}
	
	@BsonIgnore
	public Attributes getAttributes() {
		return Attributes.builder()
				.href(UriBuilder.fromPath(JaxRsActivator.class.getAnnotation(ApplicationPath.class).value())
						.path(UserResource.class)
						.path("{id}")
						.build(getId())
						.toString())
				.type(User.class.getSimpleName())
				.build();
	}
	
	@BsonCreator
	public User(
			@BsonId String id, 
			@BsonProperty("firstName") String firstName, 
			@BsonProperty("lastName") String lastName, 
			@BsonProperty("email") String email,
			@BsonProperty("emailVerified") Boolean emailVerified,
			@BsonProperty("username") String username,
			@BsonProperty("phone") String phone,
			@BsonProperty("timeZone") String timeZone,
			@BsonProperty("locale") String locale,
			@BsonProperty("lastLoggedIn") Instant lastLoggedIn,
			@BsonProperty("createdOn") Instant createdOn,
			@BsonProperty("updatedOn") Instant updatedOn,
			@BsonProperty("organizationId") String organizationId) {
		
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.emailVerified = emailVerified;
		this.username = username;
		this.phone = phone;
		this.timeZone = timeZone;
		this.locale = locale;
		this.lastLoggedIn = lastLoggedIn;
		this.createdOn = createdOn;
		this.updatedOn = updatedOn;
		this.organizationId = organizationId;
	}
}