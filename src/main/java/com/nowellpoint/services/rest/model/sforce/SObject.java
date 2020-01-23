package com.nowellpoint.services.rest.model.sforce;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;

import com.nowellpoint.services.rest.model.sforce.annotation.Column;
import com.nowellpoint.services.rest.model.sforce.annotation.Id;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;

@Getter
@RegisterForReflection
abstract class SObject {
	@Id private String id;
	@Column(value="CreatedDate") private LocalDateTime createdDate;
	@Column(value="LastModifiedDate") private LocalDateTime lastModifiedDate;
	@Column(value="CurrencyIsoCode") private String currencyIsoCode;
	private Attributes attributes;
	
	protected JsonValue addCreatedDate() {
		return format(createdDate);
	}
	
	protected JsonValue addLastModifiedDate() {
		return format(lastModifiedDate);
	}
	
	protected JsonValue addCurrencyIsoCode() {
		return getOrDefault(currencyIsoCode);
	}
	
	protected JsonValue format(LocalDateTime value) {
		return value == null ? JsonValue.NULL : Json.createValue(value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
	}
	
	protected JsonValue getOrDefault(String value) {
		return value == null ? JsonValue.NULL : Json.createValue(value);
	}
	
	protected JsonValue getOrDefault(Double value) {
		return value == null ? JsonValue.NULL : Json.createValue(value);
	}
	
	protected JsonValue getOrDefault(Long value) {
		return value == null ? JsonValue.NULL : Json.createValue(value);
	}
	
	protected JsonValue getOrDefault(Integer value) {
		return value == null ? JsonValue.NULL : Json.createValue(value);
	}
	
	public abstract JsonObject asJsonObject();
}