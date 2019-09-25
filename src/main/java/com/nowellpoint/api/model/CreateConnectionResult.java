package com.nowellpoint.api.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateConnectionResult {
	private Connection connection;
}