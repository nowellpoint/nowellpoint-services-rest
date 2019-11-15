package com.nowellpoint.services.rest.model;

import java.util.List;
import java.util.Optional;

import javax.json.bind.annotation.JsonbProperty;

import lombok.Getter;

@Getter
public class Keys {
	@JsonbProperty(value="keys") private List<Key> keys;
	
	public Optional<Key> getKey(String keyId) {
		return keys.stream().filter(k -> k.getKeyId().equals(keyId)).findFirst();
	}
}