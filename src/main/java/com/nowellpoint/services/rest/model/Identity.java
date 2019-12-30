package com.nowellpoint.services.rest.model;

import javax.json.bind.annotation.JsonbProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;

@Builder(toBuilder=true)
@RegisterForReflection
public class Identity {
	
	@JsonbProperty("sub") 
	private String id;
	
	@JsonbProperty("name") 
	private String name;
	
	@JsonbProperty("given_name") 
	private String givenName;
	
	@JsonbProperty("family_name") 
	private String familyName;
	
	@JsonbProperty("preferred_username")
	private String preferredUsername;
	
	@JsonbProperty("email") 
	private String email;
	
	@JsonbProperty("email_verified") 
	private Boolean emailVerified;
	
	@JsonbProperty("zoneinfo") 
	private String timeZone;
	
	@JsonbProperty("locale") 
	private String locale;
	
	@JsonbProperty("updated_at")
	private Long updatedAt;
}