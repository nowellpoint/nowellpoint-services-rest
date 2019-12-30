package com.nowellpoint.services.rest.model;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder=true)
@RegisterForReflection
public class Subscription {
	private String planId;
	private String name;
	private Double price;
	private String billingFrequency;
	
	@BsonCreator
	public Subscription(
			@BsonProperty("planId") String planId,
			@BsonProperty("name") String name,
			@BsonProperty("price") Double price,
			@BsonProperty("billingFrequency") String billingFrequency) {
		
		this.planId = planId;
		this.name = name;
		this.price = price;
		this.billingFrequency = billingFrequency;
	}
}