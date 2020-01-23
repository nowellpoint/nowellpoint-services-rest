package com.nowellpoint.api.service;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;

import com.nowellpoint.services.rest.model.Connection;
import com.nowellpoint.services.rest.model.sforce.Account;
import com.nowellpoint.services.rest.model.sforce.Datastore;

@RequestScoped
public class AccountService extends AbstractService {
	
	private static final String QUERY_STRING = "Select Id, "
			+ "Name, "
			+ "BillingAddress, "
			+ "ShippingAddress, "
			+ "SBQQ__TaxExempt__c, "
			+ "(Select Id, "
			+ "TeamMemberRole, "
			+ "User.Id, "
			+ "User.FirstName, "
			+ "User.LastName, "
			+ "User.Name, "
			+ "User.Email, "
			+ "User.Phone "
			+ "From AccountTeamMembers), "
			+ "(Select Id, "
			+ "CreatedDate, "
			+ "LastModifiedDate, "
			+ "SBQQ__Account__r.Id, "
			+ "SBQQ__Product__r.Id, "
			+ "SBQQ__Product__r.ProductCode, "
			+ "SBQQ__Product__r.QuantityUnitOfMeasure, "
			+ "SBQQ__Product__r.Description, "
			+ "SBQQ__Quantity__c, "
			+ "SBQQ__ListPrice__c, "
			+ "SBQQ__CustomerPrice__c, "
			+ "SBQQ__StartDate__c, "
			+ "SBQQ__EndDate__c, "
			+ "SBQQ__RevisedSubscription__c, "
			+ "SBQQ__QuoteLine__r.Id, "
			+ "SBQQ__QuoteLine__r.CreatedDate, "
			+ "SBQQ__QuoteLine__r.LastModifiedDate, "
			+ "SBQQ__QuoteLine__r.SBQQ__UpgradedSubscription__r.Id, "
			+ "SBQQ__QuoteLine__r.SBQQ__UpgradedSubscription__r.SBQQ__Product__r.Id, "
			+ "SBQQ__QuoteLine__r.SBQQ__UpgradedSubscription__r.SBQQ__Product__r.ProductCode, "
			+ "SBQQ__QuoteLine__r.SBQQ__UpgradedSubscription__r.SBQQ__Product__r.QuantityUnitOfMeasure, "
			+ "SBQQ__QuoteLine__r.SBQQ__UpgradedSubscription__r.SBQQ__Product__r.Description, "
			+ "SBQQ__QuoteLine__r.SBQQ__UpgradedSubscription__r.SBQQ__Quantity__c, "
			+ "SBQQ__QuoteLine__r.SBQQ__UpgradedSubscription__r.SBQQ__ListPrice__c, "
			+ "SBQQ__QuoteLine__r.SBQQ__UpgradedSubscription__r.SBQQ__CustomerPrice__c, "
			+ "SBQQ__QuoteLine__r.SBQQ__UpgradedSubscription__r.SBQQ__StartDate__c, "
			+ "SBQQ__QuoteLine__r.SBQQ__UpgradedSubscription__r.SBQQ__EndDate__c, "
			+ "SBQQ__QuoteLine__r.SBQQ__RenewedSubscription__r.Id, "
			+ "SBQQ__QuoteLine__r.SBQQ__RenewedSubscription__r.SBQQ__Product__r.Id, "
			+ "SBQQ__QuoteLine__r.SBQQ__RenewedSubscription__r.SBQQ__Product__r.ProductCode, "
			+ "SBQQ__QuoteLine__r.SBQQ__RenewedSubscription__r.SBQQ__Product__r.QuantityUnitOfMeasure, "
			+ "SBQQ__QuoteLine__r.SBQQ__RenewedSubscription__r.SBQQ__Product__r.Description, "
			+ "SBQQ__QuoteLine__r.SBQQ__RenewedSubscription__r.SBQQ__Quantity__c, "
			+ "SBQQ__QuoteLine__r.SBQQ__RenewedSubscription__r.SBQQ__ListPrice__c, "
			+ "SBQQ__QuoteLine__r.SBQQ__RenewedSubscription__r.SBQQ__CustomerPrice__c, "
			+ "SBQQ__QuoteLine__r.SBQQ__RenewedSubscription__r.SBQQ__StartDate__c, "
			+ "SBQQ__QuoteLine__r.SBQQ__RenewedSubscription__r.SBQQ__EndDate__c "
			+ "From SBQQ__Subscriptions__r) "
			+ "From Account";
	
	public Optional<Account> findById(Connection connection, String id) {
		Datastore datastore = createDatastore(connection);		
		List<Account> subscriptionList = datastore.createQuery(QUERY_STRING
				.concat(" Where Id = '")
				.concat(id)
				.concat("'"), Account.class)
				.getResults();
		return subscriptionList.stream().findFirst();
	}
}