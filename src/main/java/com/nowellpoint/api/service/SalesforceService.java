package com.nowellpoint.api.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.eclipse.microprofile.config.Config;

import com.nowellpoint.services.rest.model.ConnectionRequest;
import com.nowellpoint.services.rest.model.ConnectionResult;
import com.nowellpoint.services.rest.model.sforce.ApiError;
import com.nowellpoint.services.rest.model.sforce.Identity;
import com.nowellpoint.services.rest.model.sforce.Organization;
import com.nowellpoint.services.rest.model.sforce.SalesforceServiceException;
import com.nowellpoint.services.rest.model.sforce.Token;
import com.nowellpoint.services.rest.util.ConfigProperties;
import com.nowellpoint.services.rest.util.JsonbUtil;

@RequestScoped
public class SalesforceService {
	
	private static final String TOKEN = "https://login.salesforce.com/services/oauth2/token";
	
	private static final String ORGANIZATION_FIELDS = "Id,Division,Fax,DefaultLocaleSidKey,FiscalYearStartMonth,"
 			+ "InstanceName,IsSandbox,LanguageLocaleKey,Name,OrganizationType,Phone,PrimaryContact,"
 			+ "UsesStartDateAsFiscalYearName,Address";
	
	private static String CLIENT_ID = null;
	private static String CLIENT_SECRET = null;
	
	@Inject
	Config config;
	
	@PostConstruct
	public void init() {
		CLIENT_ID                   = config.getValue(ConfigProperties.SALESFORCE_CLIENT_ID, String.class);
		CLIENT_SECRET               = config.getValue(ConfigProperties.SALESFORCE_CLIENT_SECRET, String.class);
	}
	
	public ConnectionResult connect(ConnectionRequest request) {

		try {
			Token token = authenticate(request.getUsername(), request.getPassword());
			
			Identity identity = getIdentity(token.getAccessToken(), token.getId());

			Organization organization = getOrganization(token.getAccessToken(), identity.getOrganizationId(), identity.getUrls().getSobjects());
			
			return ConnectionResult.builder()
					.token(token)
					.identity(identity)
					.organization(organization)
					.build();
			
		} catch (URISyntaxException | IOException | SalesforceServiceException e) {
			return ConnectionResult.builder()
					.success(Boolean.FALSE)
					.errorMessage(e.getMessage())
					.build();
			
		}
	}
	
	private Token authenticate(String username, String password) throws URISyntaxException, ClientProtocolException, IOException {
    	
		URIBuilder builder = new URIBuilder(TOKEN);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("grant_type", "password"));
		params.add(new BasicNameValuePair("client_id", CLIENT_ID));
		params.add(new BasicNameValuePair("client_secret", CLIENT_SECRET));
	    params.add(new BasicNameValuePair("username", username));
	    params.add(new BasicNameValuePair("password", password));
	    
    	HttpPost request = new HttpPost(builder.build());
    	request.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED);
    	request.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
    	request.setEntity(new UrlEncodedFormEntity(params));
    	
    	final HttpResponse response = HttpClients.createDefault().execute(request);
    	
    	Token token = null;
    	
    	if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
    		token = JsonbUtil.fromJson(response.getEntity().getContent(), Token.class); 
    	} else { 
    		throw new SalesforceServiceException(response.getStatusLine().getStatusCode(), JsonbUtil.fromJson(response.getEntity().getContent(), ApiError.class));
    	}
    	
    	return token;
    }
    
    private Identity getIdentity(String accessToken, String id) throws URISyntaxException, ClientProtocolException, IOException {
    	
    	URIBuilder builder = new URIBuilder(id).addParameter("version", "latest");
		
		HttpGet request = new HttpGet(builder.build());
		request.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
		request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
    	
    	final HttpResponse response = HttpClients.createDefault().execute(request);
    	
    	Identity identity = null;
    	
    	if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
    		identity = JsonbUtil.fromJson(response.getEntity().getContent(), Identity.class); 
    	} else {
    		throw new SalesforceServiceException(response.getStatusLine().getStatusCode(), JsonbUtil.fromJson(response.getEntity().getContent(), ApiError.class));
    	}
    	
    	return identity;
    }
    
    private Organization getOrganization(String accessToken, String organizationId, String sobjectUrl) throws URISyntaxException, ClientProtocolException, IOException {
    	
    	URIBuilder builder = new URIBuilder(sobjectUrl + "Organization/" + organizationId)
				.addParameter("version", "latest")
				.addParameter("fields", ORGANIZATION_FIELDS);
		
		HttpGet request = new HttpGet(builder.build());
		request.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
		request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
    	
    	final HttpResponse response = HttpClients.createDefault().execute(request);
    	
    	Organization organization = null;
    	
    	if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
    		organization = JsonbUtil.fromJson(response.getEntity().getContent(), Organization.class); 
    	} else {
    		throw new SalesforceServiceException(response.getStatusLine().getStatusCode(), JsonbUtil.fromJson(response.getEntity().getContent(), ApiError.class));
    	}
    	
    	return organization;
    }
}