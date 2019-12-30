package com.nowellpoint.services.rest.model.sforce;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthenticationRequest {
	public enum Instance {
		PRODUCTION("https://login.salesforce.com/services/oauth2/token"),
		SANDBOX("https://test.salesforce.com/services/oauth2/token");
		
		private String authEndpoint;
		
		public String getAuthEndpoint() {
			return authEndpoint;
		}
		
		private Instance(String authEndpoint) {
			this.authEndpoint = authEndpoint;
		}
	}

	private String connectionId;
	private String authEndpoint;
	private String clientId;
	private String clientSecret;
	private String username;
	private String password;
}