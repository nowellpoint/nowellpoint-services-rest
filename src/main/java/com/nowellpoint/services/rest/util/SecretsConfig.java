package com.nowellpoint.services.rest.util;

import java.util.HashMap;
import java.util.Map;

import javax.json.bind.JsonbBuilder;

import org.eclipse.microprofile.config.spi.ConfigSource;

//import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;

@SuppressWarnings("unchecked")
public class SecretsConfig implements ConfigSource {
	
	private static final String NAME = "AWSSecretsManager";
	private static Map<String,String> PROPERTIES = new HashMap<>();
	
	static {
//		AWSSecretsManager client = AWSSecretsManagerClientBuilder.standard()
//    			.withRegion(System.getenv("AWS_REGION"))
//    			.withCredentials(new EnvironmentVariableCredentialsProvider())
//    			.build();
		
		AWSSecretsManager client = AWSSecretsManagerClientBuilder.defaultClient();

        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest().withSecretId(
        		System.getProperty("secret.name") != null ? System.getProperty("secret.name"): System.getenv("AWS_SECRET_NAME"));

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