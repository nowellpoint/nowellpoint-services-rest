package com.nowellpoint.api;

//import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.services.rest.model.ConnectionRequest;
import com.nowellpoint.util.SecretsManager;

import java.nio.charset.StandardCharsets;

//@QuarkusTest
public class ConnectionResourceTest {

    //@Test
    public void testCreateConnection() {
    	
//    	System.out.println(SecretsManager.getSalesforceClientId());
//    	System.out.println(SecretsManager.getSalesforceClientSecret());
//    	System.out.println(System.getenv("SALESFORCE_USERNAME"));
//    	System.out.println(System.getenv("SALESFORCE_PASSWORD"));
//    	System.out.println(System.getenv("SALESFORCE_SECURITY_TOKEN"));
//    	
//    	ConnectionRequest request = ConnectionRequest.builder()
//    			.clientId(SecretsManager.getSalesforceClientId())
//				.clientSecret(SecretsManager.getSalesforceClientSecret())
//				.username(System.getenv("SALESFORCE_USERNAME"))
//				.password(System.getenv("SALESFORCE_PASSWORD"))
//				.securityToken(System.getenv("SALESFORCE_SECURITY_TOKEN"))
//				.build();
//    	
//    	HttpResponse response = RestResource.post("http://localhost:8080/connections")
//				.acceptCharset(StandardCharsets.UTF_8)
//				.accept(MediaType.APPLICATION_JSON)
//				//.bearerAuthorization(accessToken)
//				.body(request)
//				.contentType(MediaType.APPLICATION_JSON)
//                .execute();
//    	
//    	
//    	System.out.println(response.getAsString());
    	
    	
//        given()
//          .when().post("/connections")
//          .then()
//             .statusCode(201)
//             .body(is("hello"));
    }

}