package com.nowellpoint.api.service;

import javax.inject.Inject;

public class UserService extends AbstractService {
	
	@Inject
	private IdentityProviderService identityProviderService;
	
	public void create() {
		
	}
	
	public void disable(String username) {
		identityProviderService.disableUser(username);
	}
	
	public void delete(String username) {
		identityProviderService.deleteUser(username);
	}
}