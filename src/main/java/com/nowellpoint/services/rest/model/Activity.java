package com.nowellpoint.services.rest.model;

import java.time.Instant;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder=true)
@RegisterForReflection
public class Activity {
	private Instant eventDate;
	private String description;
	private String status;
	private String subject;
	private String type;
	private Instant createdOn;
	private Instant updatedOn;
	
	public enum ActivityType {
		EMAIL("Email");
		
		private final String activityType;

		ActivityType(String activityType) {
	        this.activityType = activityType;
	    }
	    
	    public String getActivityType() {
	        return this.activityType;
	    }
	}
	
	@BsonCreator
	public Activity(
			@BsonProperty("eventDate") Instant eventDate,
			@BsonProperty("description") String description,
			@BsonProperty("status") String status,
			@BsonProperty("subject") String subject,
			@BsonProperty("type") String type,
			@BsonProperty("createdOn") Instant createdOn,
			@BsonProperty("updatedOn") Instant updatedOn) {
		
		this.eventDate = eventDate;
		this.description = description;
		this.status = status;
		this.subject = subject;
		this.type = type;
		this.createdOn = createdOn;
		this.updatedOn = updatedOn;
	}
}