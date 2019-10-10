package com.nowellpoint.api.util;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyVisibilityStrategy;

public class JsonbUtil {
	
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
}