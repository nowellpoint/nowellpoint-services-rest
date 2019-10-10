package com.nowellpoint.api.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Attributes {
	private String href;
	private String type;
}