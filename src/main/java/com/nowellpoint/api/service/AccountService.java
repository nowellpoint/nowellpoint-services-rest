package com.nowellpoint.api.service;

import java.util.Optional;

import javax.enterprise.context.RequestScoped;

import com.nowellpoint.services.rest.model.Connection;
import com.nowellpoint.services.rest.model.sforce.Account;
import com.nowellpoint.services.rest.model.sforce.Datastore;

@RequestScoped
public class AccountService extends AbstractService {
	
	public Optional<Account> findById(Connection connection, String id) {
		Datastore datastore = createDatastore(connection);		
		Optional<Account> account = datastore.find(Account.class, id);
		return account;
	}
}