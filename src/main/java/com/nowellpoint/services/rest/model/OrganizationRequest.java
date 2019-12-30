package com.nowellpoint.services.rest.model;

import javax.validation.constraints.NotEmpty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@RegisterForReflection
public class OrganizationRequest {
	
	private String street;
	
	private String locality;
	
	private String region;
	
	private String postalCode;
	
	@NotEmpty 
	private String countryCode;
	
	@NotEmpty
	private String name;
}