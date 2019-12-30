package com.nowellpoint.services.rest.util;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.inject.Singleton;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyVisibilityStrategy;

import io.quarkus.jsonb.JsonbConfigCustomizer;

@Singleton
public class JsonbUtil implements JsonbConfigCustomizer {
	
	private static Jsonb jsonb;
	
	static {
		JsonbConfig config = new JsonbConfig()
				.withNullValues(true)
				.withPropertyVisibilityStrategy(new PropertyVisibilityStrategy() {
					@Override
					public boolean isVisible(Field field) {
						return Modifier.isPrivate(field.getModifiers());
					}
					
					@Override
					public boolean isVisible(Method method) {
						return false;
					}
				});
		
		jsonb = JsonbBuilder.create(config);
	}
	
	public static <T> T fromJson(InputStream source, Class<T> type) {
		return jsonb.fromJson(source, type);
	}
	
	public static <T> T fromJson(String source, Class<T> type) {
		return jsonb.fromJson(source, type);
	}

	@Override
	public void customize(JsonbConfig jsonbConfig) {
		jsonbConfig.withNullValues(true).withPropertyVisibilityStrategy(new PropertyVisibilityStrategy() {
			@Override
			public boolean isVisible(Field field) {
				return Modifier.isPrivate(field.getModifiers());
			}
			
			@Override
			public boolean isVisible(Method method) {
				return true;
			}
		});	
	}
	
	public static String toJson(Object object) {
		return jsonb.toJson(object);
	}
}