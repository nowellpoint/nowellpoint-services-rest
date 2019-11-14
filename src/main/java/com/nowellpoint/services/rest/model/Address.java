package com.nowellpoint.services.rest.model;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder=true)
public class Address {
	private String street;
	private String city;
	private String stateCode;
	private String state;
	private String countryCode;
	private String country;
	private String postalCode;
	
	@BsonCreator
	public Address(
			@BsonProperty("street") String street,
			@BsonProperty("city") String city,
			@BsonProperty("stateCode") String stateCode,
			@BsonProperty("state") String state,
			@BsonProperty("countryCode") String countryCode,
			@BsonProperty("country") String country,
			@BsonProperty("postalCode") String postalCode) {
		
		this.street = street;
		this.city = city;
		this.stateCode = stateCode;
		this.state = state;
		this.countryCode = countryCode;
		this.country = country;
		this.postalCode = postalCode;
	}
}