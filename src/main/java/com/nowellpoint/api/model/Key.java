package com.nowellpoint.api.model;

import javax.json.bind.annotation.JsonbProperty;

import lombok.Getter;

@Getter
public class Key {
	@JsonbProperty(value="alg") private String algorithm;
	@JsonbProperty(value="kid") private String keyId;
	@JsonbProperty(value="kty") private String keyType;
	@JsonbProperty(value="e") private String exponent;
	@JsonbProperty(value="n") private String modulus;
	@JsonbProperty(value="use") private String useage;
}