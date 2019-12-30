package com.nowellpoint.services.rest.model.sforce;

import java.time.LocalDate;
import java.util.List;

import com.nowellpoint.services.rest.model.sforce.annotation.Column;
import com.nowellpoint.services.rest.model.sforce.annotation.OneToMany;
import com.nowellpoint.services.rest.model.sforce.annotation.Entity;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;

@Getter
@Entity("Account")
@RegisterForReflection
public class Account extends SObject {
	@Column(value="Name") private String name;
	@Column(value="DateOfFirstOrder__c") private LocalDate customerSince;
	@Column(value="NetsuiteCustomerId__c") private String customerId;
	@Column(value="BillingAddress") private Address billingAddress;
	@Column(value="ShippingAddress") private Address shippingAddress;
	@OneToMany(value="Contracts") private List<Contract> contracts;
	@OneToMany(value="AccountTeamMembers") private List<TeamMember> accountTeamMembers;
	@OneToMany(value="Platform_Customers__r") private List<PlatformCustomer> platformCustomers;
}