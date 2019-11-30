package com.nowellpoint.services.rest.model;

import com.nowellpoint.services.rest.model.sforce.Identity;
import com.nowellpoint.services.rest.model.sforce.Organization;
import com.nowellpoint.services.rest.model.sforce.Token;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ConnectionResult {
	@Builder.Default private boolean success = Boolean.TRUE;
	private Token token;
	private Identity identity;
	private Organization organization;
	private String errorMessage;
}