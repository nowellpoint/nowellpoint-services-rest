package com.nowellpoint.services.rest.model.sforce;

import com.nowellpoint.services.rest.model.sforce.annotation.Column;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;

@Getter
@RegisterForReflection
public class Address {
	@Column(value="street") private String street;
	@Column(value="city") private String city;
	@Column(value="state") private String state;
	@Column(value="stateCode") private String stateCode;
	@Column(value="country") private String country;
	@Column(value="countryCode") private String countryCode;
	@Column(value="postalCode") private String postalCode;
	@Column(value="longitude") private Double longitude;
	@Column(value="latitude") private Double latitude;
}