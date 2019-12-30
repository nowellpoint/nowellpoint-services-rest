package com.nowellpoint.services.rest.model.sforce;

import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;
import javax.json.stream.JsonParser.Event;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.jboss.logging.Logger;

import com.nowellpoint.services.rest.model.sforce.annotation.Column;
import com.nowellpoint.services.rest.model.sforce.annotation.Id;
import com.nowellpoint.services.rest.model.sforce.annotation.OneToMany;
import com.nowellpoint.services.rest.model.sforce.annotation.OneToOne;

public class QueryResultImpl implements QueryResult {
	
	private static final Logger LOG = Logger.getLogger(QueryResultImpl.class);
	
	private Integer totalSize;
	private Boolean done;
	private String nextRecordsUrl;
	
	public QueryResultImpl() {
		
	}
	
	@Override
	public <T> List<T> getRecords(Class<T> type, InputStream stream) {
		JsonParserFactory factory = Json.createParserFactory(null);
        JsonParser parser = factory.createParser(stream);
		try {
			return parseQueryResult(type, parser);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			LOG.error(e);
		} finally {
			parser.close();
		}
		return null;
	}
	
	@Override
	public Integer getTotalSize() {
		return totalSize;
	}
	
	private <T> List<T> getRecords(Class<T> type, StringReader reader) {
		JsonParserFactory factory = Json.createParserFactory(null);
        JsonParser parser = factory.createParser(reader);
		try {
			return parseQueryResult(type, parser);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			LOG.error(e);
		} finally {
			parser.close();
		}
		return null;
	}
	
	private <T> List<T> parseQueryResult(Class<T> type, JsonParser parser) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		List<T> records = Collections.emptyList();
		
		while (parser.hasNext()) {
			
			Event event = parser.next();
			
			if (event == JsonParser.Event.KEY_NAME) {
				
				String key = parser.getString();
				
				switch (key) {
				
				case "done":
					event = parser.next();
					if (event == JsonParser.Event.VALUE_TRUE) {
						this.done = Boolean.TRUE;
					} else {
						this.done = Boolean.FALSE;
					}
					break;
				case "totalSize" :
					parser.next();
					this.totalSize = parser.getInt();
					break;
				case "nextRecordsUrl" :
					parser.next();
					this.nextRecordsUrl = parser.getString();
					break;
				case "records" :
					event = parser.next();
					if (event == JsonParser.Event.START_ARRAY) {	
						records = parseRecords(type, parser.getArrayStream());
						while (! done) {
							
						}
					}
					break;
				}
			}
		}
		return records;	
	}
	
	private <T> List<T> parseRecords(Class<T> type, Stream<JsonValue> jsonValue) throws IllegalAccessException, InstantiationException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		List<T> records = new ArrayList<T>();
		jsonValue.forEach(r -> {
			JsonObject object = r.asJsonObject();
			try {
				T record = parseObject(type, object);
				records.add(record);
			} catch (IllegalAccessException | InstantiationException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				LOG.error(e);
			}
		});
		return records;
	}
	
	private <T> T parseObject(Class<T> type, JsonObject object) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Map<String,Field> fields = FieldUtils.getAllFieldsList(type)
				.stream()
				.collect(Collectors.toMap(f -> {
					if (f.isAnnotationPresent(Id.class)) {
						return "Id";
					} else if (f.isAnnotationPresent(Column.class)) {
						return f.getAnnotation(Column.class).value();
					} else if (f.isAnnotationPresent(OneToOne.class)) {
						return f.getAnnotation(OneToOne.class).value();
					} else if (f.isAnnotationPresent(OneToMany.class)) {
						return f.getAnnotation(OneToMany.class).value();
					} else {
						return f.getName();
					}
				}, f -> f));
		
		Constructor<T> constructor = type.getConstructor();
		T record = constructor.newInstance();
		
		object.keySet().stream().forEach(key -> {
			
			Object value = null;
			Field target = null;
			
			if (fields.containsKey(key)) {
				target = fields.get(key);
				ValueType valueType = object.get(key).getValueType();
				if (valueType == ValueType.STRING) {
					JsonString json = object.getJsonString(key);
					if (target.isAnnotationPresent(Id.class)) {
						value = object.getString(key);
					} else if (target.getType().isAssignableFrom(LocalDate.class)) {
						value = LocalDate.parse(json.getString());
					} else if (target.getType().isAssignableFrom(LocalTime.class)) {
						value = LocalTime.parse(json.getString());
					} else if (target.getType().isAssignableFrom(LocalDateTime.class)) {
						value = LocalDateTime.parse(json.getString(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
					} else if (target.getType().isAssignableFrom(Instant.class)) {
						value = LocalDateTime.parse(json.getString(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).toInstant(ZoneOffset.UTC);
					} else {
						value = object.getString(key);
					}
				} else if (valueType == ValueType.NUMBER) {
					JsonNumber json = object.getJsonNumber(key);
					if (target.getType().isAssignableFrom(Long.class)) {
						value = Long.valueOf(json.longValue());
					} else if (target.getType().isAssignableFrom(Integer.class)) {
						value = Integer.valueOf(json.intValue());
					} else if (target.getType().isAssignableFrom(Double.class)) {
						value = Double.valueOf(json.doubleValue());
					} else if (target.getType().isAssignableFrom(BigDecimal.class)) {
						value = json.bigDecimalValue();
					} else if (target.getType().isAssignableFrom(BigInteger.class)) {
						value = json.bigIntegerValue();
					} else if (target.getType().isAssignableFrom(Number.class)) {
						value = json.numberValue();
					} else {
						value = object.getInt(key);
					}
				} else if (valueType == ValueType.TRUE) {
					value = Boolean.TRUE;
				} else if (valueType == ValueType.FALSE) {
					value = Boolean.FALSE;
				} else if (valueType == ValueType.OBJECT) {
					JsonObject json = object.getJsonObject(key);
					if (target.isAnnotationPresent(OneToMany.class)) {
				        value = parseList(target, json);
					} else {
						try {
							value = parseObject(target.getType(), json);
						} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							LOG.error(e);
						}
					}
				}
			}
			
			if (target != null && value != null) {
				try {
					FieldUtils.writeField(record, target.getName(), value, true);
				} catch (IllegalAccessException e) {
					LOG.error(e);
				}
			}
		});
		return record;
	}
	
	private List<?> parseList(Field field, JsonObject json) {
        Class<?> listType = getParameterizedType(field);								
        QueryResultImpl queryResult = new QueryResultImpl();
        return queryResult.getRecords(listType, new StringReader(json.toString()));
    }
	
	private Class<?> getParameterizedType(Field field) {
		ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
        return (Class<?>) parameterizedType.getActualTypeArguments()[0];		
	}
}