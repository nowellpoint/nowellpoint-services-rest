package com.nowellpoint.services.rest.model.sforce;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.jboss.logging.Logger;

import com.nowellpoint.services.rest.util.JsonbUtil;

public class QueryImpl<T> implements Query<T> {
	
	private static final Logger LOG = Logger.getLogger(QueryImpl.class);
	
	private DatastoreImpl datastore;
	private Class<T> type;
	private String queryString;
	
	public QueryImpl(DatastoreImpl datastore, Class<T> type, String queryString) {
		this.datastore = datastore;
		this.type = type;
		this.queryString = queryString;
	}
	
	public QueryImpl(DatastoreImpl datastore, Class<T> type, String id, String query) {
		this.datastore = datastore;
		this.type = type;
		this.queryString = datastore.buildQueryString(type)
				.concat(" Where Id = '")
				.concat(id)
				.concat("'");
	}
	
	@Override
	public List<T> getResults() {
		
		List<T> results = Collections.emptyList();
		
		try {
			URIBuilder builder = new URIBuilder(datastore.getIdentity().getUrls().getQuery()).addParameter("q", queryString);
			
			HttpGet request = new HttpGet(builder.build());
			request.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
			request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + datastore.getToken().getAccessToken());
			
			final HttpResponse response = HttpClients.createDefault().execute(request);
			
			datastore.setApiUsage(response);
			
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				QueryResultImpl queryResult = new QueryResultImpl();
				results = queryResult.getRecords(type, response.getEntity().getContent());
			} else {
				throw new SalesforceServiceException(response.getStatusLine().getStatusCode(), JsonbUtil.fromJson(response.getEntity().getContent(), ApiError.class));
			}
			
		} catch (URISyntaxException | IOException e) {
			LOG.error(e);
		}
		
		return results;
	}
}