package com.nowellpoint.api.service;

import javax.inject.Inject;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.nowellpoint.services.rest.util.AWSConfiguration;

public abstract class AbstractAWSService {
	
	@Inject
	protected AWSConfiguration awsConfiguration;
	
	protected AWSStaticCredentialsProvider getAWSCredentialsProvider() {
		return new AWSStaticCredentialsProvider(new BasicAWSCredentials(
				awsConfiguration.accessKey(), 
				awsConfiguration.secretAccessKey()));	
	}
}