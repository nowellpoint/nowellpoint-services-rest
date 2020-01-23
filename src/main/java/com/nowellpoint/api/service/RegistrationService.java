package com.nowellpoint.api.service;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.nowellpoint.services.rest.OrganizationResource;
import com.nowellpoint.services.rest.UserResource;
import com.nowellpoint.services.rest.model.Organization;
import com.nowellpoint.services.rest.model.OrganizationRequest;
import com.nowellpoint.services.rest.model.Reference;
import com.nowellpoint.services.rest.model.Registration;
import com.nowellpoint.services.rest.model.RegistrationRequest;
import com.nowellpoint.services.rest.model.User;
import com.nowellpoint.services.rest.model.UserRequest;

@RequestScoped
public class RegistrationService {
	
	@Inject
	protected UserService userService;
	
	@Inject
	protected OrganizationService organizationService;
	
	@Inject
	protected EmailService emailService;
	
	public Registration register(RegistrationRequest registrationRequest) {
		
		UserRequest userRequest = UserRequest.builder()
				.email(registrationRequest.getEmail())
				.firstName(registrationRequest.getGivenName())
				.lastName(registrationRequest.getFamilyName())
				.locale(registrationRequest.getLocale())
				.phone(registrationRequest.getPhone())
				.timeZone(registrationRequest.getTimeZone())
				.build();
		
		var user = userService.create(userRequest);

		OrganizationRequest organizationRequest = OrganizationRequest.builder()
				.countryCode(registrationRequest.getCountryCode())
				.name(registrationRequest.getCompanyName())
				.build();
		
		var organization = organizationService.create(organizationRequest);
		
		userService.addToOrganization(user.getId(), organization.getId());
		
		Registration registration = Registration.builder()
				.companyName(organization.getName())
				.address(organization.getAddress())
				.email(user.getEmail())
				.username(user.getUsername())
				.organizationId(organization.getId())
				.userId(user.getId())
				.locale(user.getLocale())
				.timeZone(user.getTimeZone())
				.user(Reference.of(UserResource.class, User.class, user.getId()))
				.organization(Reference.of(OrganizationResource.class, Organization.class, organization.getId()))
				.build();
		
		emailService.sendEmailVerificationMessage(user, "slkooeijeijkdncije");
		
		return registration;
	}
}