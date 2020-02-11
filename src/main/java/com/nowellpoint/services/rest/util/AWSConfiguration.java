package com.nowellpoint.services.rest.util;

import io.quarkus.arc.config.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ConfigProperties(prefix = "aws")
public interface AWSConfiguration {

	@ConfigProperty(name = "access.key") 
    String accessKey();
	
	@ConfigProperty(name = "secret.access.key") 
    String secretAccessKey();
	
	@ConfigProperty(name = "region") 
    String region();
}