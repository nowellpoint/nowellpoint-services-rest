package com.nowellpoint.api.service;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import javax.inject.Inject;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

public class AbstractService {
	
	private static CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), 
			fromProviders(PojoCodecProvider.builder().automatic(true).build()));
	
	@Inject 
	protected MongoClient mongoClient;
	
	protected MongoDatabase getDatabase() {    	
        return mongoClient.getDatabase("nowellpoint").withCodecRegistry(codecRegistry);
    }
}