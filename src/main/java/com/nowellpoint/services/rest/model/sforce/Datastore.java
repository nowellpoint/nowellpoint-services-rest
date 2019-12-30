package com.nowellpoint.services.rest.model.sforce;

import java.util.Optional;

public interface Datastore {
	public String getAuthEndpoint();
	public String getInstanceUrl();
	public String getOrganizationId();
	public Token getToken();
	public Identity getIdentity();
	public Organization getOrganization();
	public String getApiUsage();
	
	/**
     * Returns a new query bound to the object
     *
     * @param collection The collection to query
     * @param <T>        the type of the query
     * @return the query
     */
	public <T> Query<T> createQuery(String queryString, Class<T> type);
	public <T> Optional<T> find(Class<T> type, String id);
	public <T> void delete(T object);
}