package com.nowellpoint.api.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Organization {
	private String id;
	private String username;
	private String organizationId;
	private String instanceUrl;
}