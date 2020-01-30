package com.nowellpoint.services.rest.model.sforce;

public class TeamMemberRoles {
	
	private String accountExecutive = "Account Executive (AE)";
	private String customerSuccessManager = "Customer Success Manager (CSM)";
	
	public String getAccountExecutiveRole() {
		return accountExecutive;
	}

	public String getCustomerSuccessManagerRole() {
		return customerSuccessManager;
	}
}