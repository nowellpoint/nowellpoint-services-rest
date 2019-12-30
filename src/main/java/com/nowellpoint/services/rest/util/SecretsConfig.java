package com.nowellpoint.services.rest.util;

import java.util.HashMap;
import java.util.Map;

import javax.json.bind.JsonbBuilder;

import org.eclipse.microprofile.config.spi.ConfigSource;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
//import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;

@SuppressWarnings("unchecked")
public class SecretsConfig implements ConfigSource {
	
	private static final String NAME = "AWSSecretsManager";
	private static Map<String,String> PROPERTIES = new HashMap<>();
	
	public SecretsConfig() {
		
		String accessKey = System.getProperty("aws.access.key") != null ? System.getProperty("aws.access.key") : System.getenv("AWS_ACCESS_KEY");
		String secretAccessKey = System.getProperty("aws.secret.access.key") != null ? System.getProperty("aws.secret.access.key") : System.getenv("AWS_SECRET_ACCESS_KEY");
		String region = System.getProperty("aws.region") != null ? System.getProperty("aws.region") : System.getenv("AWS_REGION");
		String secretId = System.getProperty("aws.secret.id") != null ? System.getProperty("aws.secret.id"): System.getenv("AWS_SECRET_NAME");
		
		BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretAccessKey);
		
		AWSSecretsManager client = AWSSecretsManagerClientBuilder.standard()
    			.withRegion(region)
    			.withCredentials(new AWSStaticCredentialsProvider(credentials))
    			.build();

        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest().withSecretId(secretId);

        GetSecretValueResult getSecretValueResult = client.getSecretValue(getSecretValueRequest);
        
        PROPERTIES.putAll(JsonbBuilder.create().fromJson(getSecretValueResult.getSecretString(), HashMap.class));
	}

	@Override
	public Map<String, String> getProperties() {
		return PROPERTIES;
	}

	@Override
	public String getValue(String propertyName) {
		if (PROPERTIES.containsKey(propertyName)){
            return PROPERTIES.get(propertyName);
        }
        return null;
	}

	@Override
	public String getName() {
		return NAME;
	}
}