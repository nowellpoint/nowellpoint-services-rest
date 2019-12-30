package com.nowellpoint.services.rest.model;

import lombok.Getter;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;

@Getter
@Builder
@RegisterForReflection
public class Registration {
	private String userId;
	private String username;
	private String email;
	private String firstName;
	private String lastName;
	private String phone;
	private String timeZone;
	private String locale;
	private String companyName;
	private String organizationId;
	private Address address;
	private Reference user;
	private Reference organization;
}