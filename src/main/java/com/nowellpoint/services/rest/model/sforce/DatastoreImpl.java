package com.nowellpoint.services.rest.model.sforce;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.jboss.logging.Logger;

import com.nowellpoint.services.rest.model.sforce.annotation.Column;
import com.nowellpoint.services.rest.model.sforce.annotation.Entity;
import com.nowellpoint.services.rest.model.sforce.annotation.Id;
import com.nowellpoint.services.rest.model.sforce.annotation.OneToMany;
import com.nowellpoint.services.rest.model.sforce.annotation.OneToOne;
import com.nowellpoint.services.rest.util.JsonbUtil;

public class DatastoreImpl implements Datastore {
	
	private static final Logger LOG = Logger.getLogger(DatastoreImpl.class);
	
	private static final String ORGANIZATION_FIELDS = "Id,Division,Fax,DefaultLocaleSidKey,FiscalYearStartMonth,"
 			+ "InstanceName,IsSandbox,LanguageLocaleKey,Name,OrganizationType,Phone,PrimaryContact,"
 			+ "UsesStartDateAsFiscalYearName,Address";
	
	private String authEndpoint;
	private Token token;
	private Identity identity;
	private String apiUsage;
	private Organization organization;
	
	public DatastoreImpl(AuthenticationRequest authenticationRequest) {
		this.authEndpoint = authenticationRequest.getAuthEndpoint();
		this.token = authenticate(authenticationRequest);
		this.identity = getIdentity(token.getAccessToken(), token.getId());
	}
	
	@Override
	public String getAuthEndpoint() {
		return authEndpoint;
	}
	
	@Override
	public String getInstanceUrl() {
		return token.getInstanceUrl();
	}
	
	@Override
	public String getOrganizationId() {
		return identity.getOrganizationId();
	}
		
	@Override
	public Identity getIdentity() {
		return identity;
	}
	
	@Override
	public String getApiUsage() {
		return apiUsage;
	}
	
	@Override
	public <T> Optional<T> find(Class<T> type, String id) {
		return new QueryImpl<T>(this, type, id, null).getResults().stream().findFirst();
	}
	
	@Override
	public <T> Query<T> createQuery(String queryString, Class<T> type) {
		return new QueryImpl<T>(this, type, queryString);
	}
	
	@Override
	public Organization getOrganization() {
		if (organization == null) {
			try {
				URIBuilder builder = new URIBuilder(identity.getUrls().getSobjects() + "Organization/" + identity.getOrganizationId())
						.addParameter("version", "latest")
						.addParameter("fields", ORGANIZATION_FIELDS);
				
				HttpGet request = new HttpGet(builder.build());
	    		request.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
	    		request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token.getAccessToken());
	        	
	        	final HttpResponse response = HttpClients.createDefault().execute(request);
	        	
	        	if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	        		organization = JsonbUtil.fromJson(response.getEntity().getContent(), Organization.class); 
	        	} else {
	        		throw new SalesforceServiceException(response.getStatusLine().getStatusCode(), JsonbUtil.fromJson(response.getEntity().getContent(), ApiError.class));
	        	}

			} catch (URISyntaxException | IOException e) {
				LOG.error(e);
			}
		}
		
		return organization;
	}
	
	@Override
	public <T> void delete(T entity) {
		try {
			URIBuilder builder = new URIBuilder(identity.getUrls().getSobjects() + resolveEntity(entity) + "/" + resolveId(entity));
			
			HttpDelete request = new HttpDelete(builder.build());
    		request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token.getAccessToken());
        	
        	final HttpResponse response = HttpClients.createDefault().execute(request);
        	
        	if (response.getStatusLine().getStatusCode() != HttpStatus.SC_NO_CONTENT) {
        		throw new SalesforceServiceException(response.getStatusLine().getStatusCode(), JsonbUtil.fromJson(response.getEntity().getContent(), ApiError.class));
        	}

		} catch (URISyntaxException | IOException | IllegalAccessException e) {
			LOG.error(e);
		}
	}
	
	protected void setApiUsage(HttpResponse response) {
		this.apiUsage = response.getFirstHeader("Sforce-Limit-Info")
				.getValue()
				.replaceFirst("api-usage=", "")
				.trim();
	}
	
	@Override
	public Token getToken() {
		return token;
	}
	
	protected List<Field> getAllFields(Class<?> type) {
		return FieldUtils.getAllFieldsList(type).stream().filter(f -> {
			if (f.getType().isAssignableFrom(Attributes.class)) {
				return false;
			} else {
				return true;
			}
		}).collect(Collectors.toList());
	}
	
	protected <T> String buildQueryString(Class<T> type) {
		List<Field> fields = getAllFields(type);
		StringBuilder queryString = new StringBuilder("Select ").append(fields.stream()
				.map(f -> resolveField(f))
				.collect(Collectors.joining(", ")))
				.append(" From ")
				.append(type.isAnnotationPresent(Entity.class) ? type.getAnnotation(Entity.class).value() : type.getSimpleName());
		
		return queryString.toString();
	}
	
	private Token authenticate(AuthenticationRequest authenticationRequest) {
		Token token = null;
		try {
			URIBuilder builder = new URIBuilder(authenticationRequest.getAuthEndpoint());
    		
    		List<NameValuePair> params = new ArrayList<NameValuePair>();
    		params.add(new BasicNameValuePair("grant_type", "password"));
    		params.add(new BasicNameValuePair("client_id", authenticationRequest.getClientId()));
    		params.add(new BasicNameValuePair("client_secret", authenticationRequest.getClientSecret()));
    	    params.add(new BasicNameValuePair("username", authenticationRequest.getUsername()));
    	    params.add(new BasicNameValuePair("password", authenticationRequest.getPassword()));
    	    
        	HttpPost request = new HttpPost(builder.build());
        	request.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED);
        	request.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
        	request.setEntity(new UrlEncodedFormEntity(params));
        	
        	final HttpResponse response = HttpClients.createDefault().execute(request);
        	
        	if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
        		token = JsonbUtil.fromJson(response.getEntity().getContent(), Token.class); 
        	} else { 
        		throw new SalesforceServiceException(response.getStatusLine().getStatusCode(), JsonbUtil.fromJson(response.getEntity().getContent(), ApiError.class));
        	}
        	
		} catch (URISyntaxException | IOException e) {
			LOG.error(e);
		}
		
    	return token;
	}
    
    private Identity getIdentity(String accessToken, String id) {
    	Identity identity = null;
    	try {
    		URIBuilder builder = new URIBuilder(id).addParameter("version", "latest");
    		
    		HttpGet request = new HttpGet(builder.build());
    		request.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
    		request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        	
        	final HttpResponse response = HttpClients.createDefault().execute(request);
        	
        	if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
        		identity = JsonbUtil.fromJson(response.getEntity().getContent(), Identity.class); 
        	} else {
        		throw new SalesforceServiceException(response.getStatusLine().getStatusCode(), JsonbUtil.fromJson(response.getEntity().getContent(), ApiError.class));
        	}
        	
    	} catch (URISyntaxException | IOException e) {
    		LOG.error(e);
		}
    	
    	return identity;
    }
    
    private <T> String resolveEntity(T entity) {
		return entity.getClass().isAnnotationPresent(Entity.class) ? 
				entity.getClass().getAnnotation(Entity.class).value() : 
					entity.getClass().getSimpleName();
	}
    
    private <T> String resolveId(T entity) throws IllegalAccessException {
    	List<Field> fields = FieldUtils.getFieldsListWithAnnotation(entity.getClass(), Id.class);
    	return String.valueOf(FieldUtils.readField(fields.stream().findFirst().get(), entity));
    }
    
    private String resolveField(Field field) {
		if (field.isAnnotationPresent(Id.class)) {
			return "Id";
		} else if (field.isAnnotationPresent(Column.class)) {
			return field.getAnnotation(Column.class).value();
		} else if (field.isAnnotationPresent(OneToOne.class)) {
			List<Field> fields = getAllFields(field.getType());
			return new StringBuilder().append(fields.stream()
					.map(f -> field.getAnnotation(OneToOne.class).value() + "." + resolveField(f))
					.collect(Collectors.joining(", ")))
					.toString();
		} else if (field.isAnnotationPresent(OneToMany.class)) {
			ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
			Class<?> type = (Class<?>) parameterizedType.getActualTypeArguments()[0];	
			List<Field> fields = getAllFields(type);
			return new StringBuilder("(Select ").append(fields.stream()
					.map(f -> resolveField(f))
					.collect(Collectors.joining(", ")))
					.append(" From ")
					.append(field.isAnnotationPresent(OneToMany.class) ? field.getAnnotation(OneToMany.class).value() : field.getType().getSimpleName())
					.append(")")
					.toString();
		} else {
			return field.getName();
		}
	}
}