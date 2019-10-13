package com.nowellpoint.api.model;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Attributes {
	private String href;
	private String type;
	
	@BsonCreator
	public Attributes(
			@BsonProperty("href") String href, 
			@BsonProperty("type") String type) {
		
		this.href = href;
		this.type = type;
	}
}