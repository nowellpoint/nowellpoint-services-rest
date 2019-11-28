package com.nowellpoint.services.rest.model;

import java.util.List;
import java.util.Optional;

import javax.json.bind.annotation.JsonbProperty;

import lombok.Getter;

@Getter
public class JsonWebKeys {
	@JsonbProperty(value="keys") private List<JsonWebKey> keys;
	
	public Optional<JsonWebKey> getKey(String keyId) {
		return keys.stream().filter(k -> k.getKeyId().equals(keyId)).findFirst();
	}
}