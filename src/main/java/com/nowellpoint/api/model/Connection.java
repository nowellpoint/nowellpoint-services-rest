package com.nowellpoint.api.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Connection {
	private String id;
	private String username;
	private String organizationId;
}