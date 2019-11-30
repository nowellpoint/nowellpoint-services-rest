package com.nowellpoint.services.rest.model.sforce;

import javax.json.bind.annotation.JsonbProperty;

import lombok.Getter;

@Getter
public class Address {
	@JsonbProperty(value="street") private String street;
	@JsonbProperty(value="city") private String city;
	@JsonbProperty(value="state") private String state;
	@JsonbProperty(value="stateCode") private String stateCode;
	@JsonbProperty(value="country") private String country;
	@JsonbProperty(value="countryCode") private String countryCode;
	@JsonbProperty(value="postalCode") private String postalCode;
	@JsonbProperty(value="longitude") private Double longitude;
	@JsonbProperty(value="latitude") private Double latitude;
}