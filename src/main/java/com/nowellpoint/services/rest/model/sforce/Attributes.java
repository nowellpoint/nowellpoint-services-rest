package com.nowellpoint.services.rest.model.sforce;

import com.nowellpoint.services.rest.model.sforce.annotation.Column;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;

@Getter
@RegisterForReflection
public class Attributes {
	@Column(value="type") private String type;
	@Column(value="url") private String url;
}