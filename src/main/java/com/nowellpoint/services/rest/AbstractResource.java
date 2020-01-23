package com.nowellpoint.services.rest;

import org.eclipse.microprofile.jwt.JsonWebToken;

public abstract class AbstractResource {

	protected String getOrganizationId(JsonWebToken jwt) {
		return jwt.getAudience()
				.stream()
				.findFirst()
				.get();
	}
}