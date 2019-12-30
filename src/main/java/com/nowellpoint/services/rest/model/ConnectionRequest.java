package com.nowellpoint.services.rest.model;

import javax.validation.constraints.NotEmpty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;

@Getter
@RegisterForReflection
public class ConnectionRequest {

	@NotEmpty(message="Instance is the endpoint to be used for authentication and must be set to either production or sandbox. If the value is production then the endpoint of: https://login.salesforce.com will be used and if sandbox is set then https://test.salesforce.com will be used")
	private String instance = "production";
	
	@NotEmpty(message = "A client id is request to create or update a connection. A client id can be retrived from a connected app as described here: https://resources.docs.salesforce.com/sfdc/pdf/api_rest.pdf")
	private String clientId;
	
	@NotEmpty(message = "A client secret is request to create or update a connection. A client secret can be retrived from a connected app as described here: https://resources.docs.salesforce.com/sfdc/pdf/api_rest.pdf")
	private String clientSecret;
	
	@NotEmpty(message="A valid Salesforce username must be provided to create or update a connection")
	private String username;
	
	@NotEmpty(message="A valid Salesforce password (with security token) for the given user username be provided to create or update a connection")
	private String password;
}