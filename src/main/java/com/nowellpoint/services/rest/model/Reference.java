package com.nowellpoint.services.rest.model;

import javax.json.bind.annotation.JsonbProperty;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.UriBuilder;

import com.nowellpoint.services.rest.JaxRsActivator;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder=true)
@RegisterForReflection
public class Reference {
	private String type;
	private @JsonbProperty("$ref") String href;
	
	public static Reference of(Class<?> resource, Class<?> type, String id) {
		return Reference.builder()
				.href(UriBuilder.fromPath(JaxRsActivator.class.getAnnotation(ApplicationPath.class).value())
						.path(resource)
						.path("{id}")
						.build(id)
						.toString())
				.type(type.getSimpleName())
				.build();
	}
}