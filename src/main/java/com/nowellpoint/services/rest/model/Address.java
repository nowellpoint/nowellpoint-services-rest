package com.nowellpoint.services.rest.model;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder=true)
@RegisterForReflection
public class Address {
	private String street;
	private String locality;
	private String regionCode;
	private String region;
	private String countryCode;
	private String postalCode;
	
	public String getCountry() {
		String country = null;
		switch (countryCode) {
		case "US" : country = "United States";
		}
		return country;
	}
	
	@BsonCreator
	public Address(
			@BsonProperty("street") String street,
			@BsonProperty("locality") String locality,
			@BsonProperty("regionCode") String regionCode,
			@BsonProperty("region") String region,
			@BsonProperty("countryCode") String countryCode,
			@BsonProperty("postalCode") String postalCode) {
		
		this.street = street;
		this.locality = locality;
		this.regionCode = regionCode;
		this.region = region;
		this.countryCode = countryCode;
		this.postalCode = postalCode;
	}
}