package com.nowellpoint.api.service;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.mongodb.client.MongoCollection;
import com.nowellpoint.api.model.CreateUserRequest;
import com.nowellpoint.api.model.User;

@RequestScoped
public class UserService extends AbstractService {
	
	@Inject
	IdentityProviderService identityProviderService;
	
	public User create(CreateUserRequest request) {
		
		String subject = identityProviderService.createUser(request);
		
		User user = User.builder()
				.id(subject)
				.email(request.getEmail())
				.firstName(request.getFirstName())
				.lastName(request.getLastName())
				.phone(request.getPhone())
				.timeZone(request.getTimeZone())
				.build();
		
		insert(user);
		
		return user;
	}
	
	public void disable(String username) {
		identityProviderService.disableUser(username);
	}
	
	public void delete(String username) {
		identityProviderService.deleteUser(username);
	}
	
	private void insert(User document){        
        getCollection().insertOne(document);
    }
	
	private MongoCollection<User> getCollection() {
        return getDatabase().getCollection("users", User.class);
    }
}