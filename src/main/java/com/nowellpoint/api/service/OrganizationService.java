package com.nowellpoint.api.service;

import java.time.Instant;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.bson.Document;
import org.eclipse.microprofile.config.Config;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.nowellpoint.services.rest.model.Address;
import com.nowellpoint.services.rest.model.Organization;
import com.nowellpoint.services.rest.model.OrganizationRequest;
import com.nowellpoint.services.rest.model.Subscription;

@RequestScoped
public class OrganizationService extends AbstractService {
	
	@Inject
	Config config;
	
	@Inject
	SalesforceService salesforceService;
	
	public OrganizationService() {
		
	}
	
	public Organization findById(String id) {
		return getCollection().find(new Document("_id", id)).first();
	}
	
	public Organization create(OrganizationRequest organizationRequest) {
		
		Address address = Address.builder()
				.countryCode(organizationRequest.getCountryCode())
				.build();
		
		Subscription subscription = Subscription.builder()
				.name("Free")
				.planId("FREE")
				.price(Double.valueOf(0.00))
				.build();
		
		Organization organization = Organization.builder()
				.organizationType("Developer Edition")
    			.id(UUID.randomUUID().toString())
    			.address(address)
    			.subscription(subscription)
    			.name(organizationRequest.getName())
    			.createdOn(Instant.now())
    			.updatedOn(Instant.now())
    			.build();
		
		createOrUpdate(organization);
		
		return organization;
	}
	
	public Organization update(String id, OrganizationRequest organizationRequest) {
		
		Organization instance = findById(id);
		
		Address address = instance.getAddress().toBuilder()
				.countryCode(organizationRequest.getCountryCode())
				.locality(organizationRequest.getLocality())
				.postalCode(organizationRequest.getPostalCode())
				.region(organizationRequest.getRegion())
				.street(organizationRequest.getStreet())
				.build();
		
		Organization organization = instance.toBuilder()
				.address(address)
				.name(organizationRequest.getName())
				.updatedOn(Instant.now())
				.build();
		
		createOrUpdate(organization);
		
		return organization;
	}

	private void createOrUpdate(Organization organization) {
		getCollection().findOneAndReplace(new Document("_id", organization.getId()), organization, new FindOneAndReplaceOptions().upsert(Boolean.TRUE));
	}

    private MongoCollection<Organization> getCollection() {
        return getDatabase().getCollection("organizations", Organization.class);
    }
}