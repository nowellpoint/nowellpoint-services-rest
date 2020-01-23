package com.nowellpoint.api.service;

import java.time.Instant;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;

import org.bson.Document;
import org.jboss.logging.Logger;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.nowellpoint.services.rest.model.Activity;
import com.nowellpoint.services.rest.model.EmailEvent;
import com.nowellpoint.services.rest.model.LoggedInEvent;
import com.nowellpoint.services.rest.model.User;
import com.nowellpoint.services.rest.model.UserRequest;

@RequestScoped
public class UserService extends AbstractService {
	
	@Inject
	IdentityProviderService identityProviderService;
	
	@Inject
	Logger logger;
	
	public User findById(String id) {
		return getCollection().find(new Document("_id", id)).first();
	}
	
	public User create(UserRequest request) {
		
		String subject = identityProviderService.createUser(request);
		
		User user = User.builder()
				.id(subject)
				.email(request.getEmail())
				.emailVerified(Boolean.FALSE)
				.firstName(request.getFirstName())
				.lastName(request.getLastName())
				.phone(request.getPhone())
				.timeZone(request.getTimeZone())
				.createdOn(Instant.now())
				.updatedOn(Instant.now())
				.username(request.getEmail())
				.locale(request.getLocale())
				.build();
		
		createOrUpdate(user);
		
		return user;
	}
	
	public User addToOrganization(String id, String organizationId) {
		User instance = findById(id);
		User user = instance.toBuilder()
				.organizationId(organizationId)
				.updatedOn(Instant.now())
				.build();
		
		createOrUpdate(user);
		
		return user;
	}
	
	public User create(String organizationId, UserRequest request) {
		
		String subject = identityProviderService.createUser(request);
		
		User user = User.builder()
				.id(subject)
				.email(request.getEmail())
				.emailVerified(Boolean.FALSE)
				.firstName(request.getFirstName())
				.lastName(request.getLastName())
				.organizationId(organizationId)
				.phone(request.getPhone())
				.timeZone(request.getTimeZone())
				.createdOn(Instant.now())
				.updatedOn(Instant.now())
				.username(request.getEmail())
				.locale(request.getLocale())
				.build();
		
		createOrUpdate(user);
		
		return user;
	}
	
	public User update(String id, UserRequest request) {
		
		User instance = findById(id);
		
		User user = instance.toBuilder()
				.email(request.getEmail())
				.firstName(request.getFirstName())
				.lastName(request.getLastName())
				.phone(request.getPhone())
				.timeZone(request.getTimeZone())
				.updatedOn(Instant.now())
				.locale(request.getLocale())
				.username(request.getEmail())
				.build();
		
		createOrUpdate(user);
		
		return user;
	}
	
	public void disable(String username) {
		identityProviderService.disableUser(username);
	}
	
	public void delete(String username) {
		identityProviderService.deleteUser(username);
	}
	
	public void authenticationEventListener(@ObservesAsync LoggedInEvent event) {
		logger.debug("******* observed authentication event: " + event.getSubject() + " *******");
		User user = findById(event.getSubject());
		createOrUpdate(user.toBuilder()
				.lastLoggedIn(Instant.now())
				.build());
	}
	
	public void emailEventListener(@ObservesAsync EmailEvent event) {
		User user = findById(event.getToId());
		
		Activity activity = Activity.builder()
				.createdOn(Instant.now())
				.description(event.getBody())
				.eventDate(event.getSentDate())
				.status(event.getStatus())
				.subject(event.getSubject())
				.type(Activity.ActivityType.EMAIL.name())
				.build();
		
		createOrUpdate(user.toBuilder()
				.activityHistory(activity)
				.build());
	}
	
	private void createOrUpdate(User user) {
		getCollection().findOneAndReplace(new Document("_id", user.getId()), user, new FindOneAndReplaceOptions().upsert(Boolean.TRUE));
	}
	
	private MongoCollection<User> getCollection() {
        return getDatabase().getCollection("users", User.class);
    }
}