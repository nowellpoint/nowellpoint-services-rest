package com.nowellpoint.services.rest.model;

import java.util.List;
import java.util.Optional;

import javax.json.bind.annotation.JsonbProperty;

import lombok.Getter;

@Getter
public class JWKSet {
	@JsonbProperty(value="keys") private List<JWK> keys;
	
	public Optional<JWK> getKey(String keyId) {
		return keys.stream().filter(k -> k.getKeyId().equals(keyId)).findFirst();
	}
}