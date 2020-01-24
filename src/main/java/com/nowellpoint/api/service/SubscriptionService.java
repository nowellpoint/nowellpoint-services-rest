package com.nowellpoint.api.service;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;

import com.nowellpoint.services.rest.model.Connection;
import com.nowellpoint.services.rest.model.sforce.Datastore;
import com.nowellpoint.services.rest.model.sforce.Subscription;

@RequestScoped
public class SubscriptionService extends AbstractService {
	
	private static final String QUERY_STRING = "Select Id, " +
			"CreatedDate, " +
			"LastModifiedDate, " + 
			"CurrencyIsoCode, " +
			"SBQQ__Account__r.Id, " + 
			"SBQQ__Account__r.Name, " + 
			"SBQQ__Account__r.CreatedDate, " + 
			"SBQQ__Account__r.LastModifiedDate, " + 
			"SBQQ__Account__r.BillingAddress, " + 
			"SBQQ__Account__r.ShippingAddress, " + 
			"SBQQ__Product__r.Id, " + 
			"SBQQ__Product__r.ProductCode, " + 
			"SBQQ__Product__r.Family, " + 
			"SBQQ__Product__r.QuantityUnitOfMeasure, " + 
			"SBQQ__Product__r.Description, " + 
			"SBQQ__Quantity__c, " + 
			"SBQQ__ListPrice__c, " + 
			"SBQQ__CustomerPrice__c, " + 
			"SBQQ__StartDate__c, " + 
			"SBQQ__EndDate__c, " + 
			"SBQQ__RevisedSubscription__r.Id, " + 
			"SBQQ__RevisedSubscription__r.CurrencyIsoCode, " + 
			"SBQQ__RevisedSubscription__r.CreatedDate, " + 
			"SBQQ__RevisedSubscription__r.LastModifiedDate, " + 
			"SBQQ__RevisedSubscription__r.SBQQ__Product__r.Id, " + 
			"SBQQ__RevisedSubscription__r.SBQQ__Product__r.ProductCode, " +
			"SBQQ__RevisedSubscription__r.SBQQ__Product__r.Family, " + 
			"SBQQ__RevisedSubscription__r.SBQQ__Product__r.QuantityUnitOfMeasure, " + 
			"SBQQ__RevisedSubscription__r.SBQQ__Product__r.Description, " + 
			"SBQQ__RevisedSubscription__r.SBQQ__Quantity__c, " + 
			"SBQQ__RevisedSubscription__r.SBQQ__ListPrice__c, " + 
			"SBQQ__RevisedSubscription__r.SBQQ__CustomerPrice__c, " + 
			"SBQQ__RevisedSubscription__r.SBQQ__StartDate__c, " + 
			"SBQQ__RevisedSubscription__r.SBQQ__EndDate__c, " + 
			"SBQQ__QuoteLine__r.Id, " +
			"SBQQ__QuoteLine__r.CreatedDate, " +
			"SBQQ__QuoteLine__r.LastModifiedDate, " +
//			"SBQQ__QuoteLine__r.SBQQ__UpgradedSubscription__r.Id, " + 
//			"SBQQ__QuoteLine__r.SBQQ__UpgradedSubscription__r.CreatedDate, " + 
//			"SBQQ__QuoteLine__r.SBQQ__UpgradedSubscription__r.LastModifiedDate, " + 
//			"SBQQ__QuoteLine__r.SBQQ__UpgradedSubscription__r.CurrencyIsoCode, " + 
//			"SBQQ__QuoteLine__r.SBQQ__UpgradedSubscription__r.SBQQ__Product__r.Id, " + 
//			"SBQQ__QuoteLine__r.SBQQ__UpgradedSubscription__r.SBQQ__Product__r.ProductCode, " + 
//			"SBQQ__QuoteLine__r.SBQQ__UpgradedSubscription__r.SBQQ__Product__r.Family, " +  
//			"SBQQ__QuoteLine__r.SBQQ__UpgradedSubscription__r.SBQQ__Product__r.QuantityUnitOfMeasure, " + 
//			"SBQQ__QuoteLine__r.SBQQ__UpgradedSubscription__r.SBQQ__Product__r.Description, " + 
//			"SBQQ__QuoteLine__r.SBQQ__UpgradedSubscription__r.SBQQ__Quantity__c, " + 
//			"SBQQ__QuoteLine__r.SBQQ__UpgradedSubscription__r.SBQQ__ListPrice__c, " + 
//			"SBQQ__QuoteLine__r.SBQQ__UpgradedSubscription__r.SBQQ__CustomerPrice__c, " + 
//			"SBQQ__QuoteLine__r.SBQQ__UpgradedSubscription__r.SBQQ__StartDate__c, " + 
//			"SBQQ__QuoteLine__r.SBQQ__UpgradedSubscription__r.SBQQ__EndDate__c, " + 
			"SBQQ__QuoteLine__r.SBQQ__RenewedSubscription__r.Id, " + 
			"SBQQ__QuoteLine__r.SBQQ__RenewedSubscription__r.CreatedDate, " + 
			"SBQQ__QuoteLine__r.SBQQ__RenewedSubscription__r.LastModifiedDate, " + 
			"SBQQ__QuoteLine__r.SBQQ__RenewedSubscription__r.CurrencyIsoCode, " + 
			"SBQQ__QuoteLine__r.SBQQ__RenewedSubscription__r.SBQQ__Product__r.Id, " + 
			"SBQQ__QuoteLine__r.SBQQ__RenewedSubscription__r.SBQQ__Product__r.ProductCode, " + 
			"SBQQ__QuoteLine__r.SBQQ__RenewedSubscription__r.SBQQ__Product__r.Family, " + 
			"SBQQ__QuoteLine__r.SBQQ__RenewedSubscription__r.SBQQ__Product__r.QuantityUnitOfMeasure, " + 
			"SBQQ__QuoteLine__r.SBQQ__RenewedSubscription__r.SBQQ__Product__r.Description, " + 
			"SBQQ__QuoteLine__r.SBQQ__RenewedSubscription__r.SBQQ__Quantity__c, " + 
			"SBQQ__QuoteLine__r.SBQQ__RenewedSubscription__r.SBQQ__ListPrice__c, " + 
			"SBQQ__QuoteLine__r.SBQQ__RenewedSubscription__r.SBQQ__CustomerPrice__c, " + 
			"SBQQ__QuoteLine__r.SBQQ__RenewedSubscription__r.SBQQ__StartDate__c, " + 
			"SBQQ__QuoteLine__r.SBQQ__RenewedSubscription__r.SBQQ__EndDate__c, " + 
			"(Select Id, " + 
			"CurrencyIsoCode, " +
			"CreatedDate, " +
			"LastModifiedDate, " + 
			"SBQQ__Product__r.Id, " + 
			"SBQQ__Product__r.ProductCode, " + 
			"SBQQ__Product__r.Family, " + 
			"SBQQ__Product__r.QuantityUnitOfMeasure, " + 
			"SBQQ__Product__r.Description, " + 
			"SBQQ__Quantity__c, " + 
			"SBQQ__ListPrice__c, " + 
			"SBQQ__CustomerPrice__c, " + 
			"SBQQ__StartDate__c, " + 
			"SBQQ__EndDate__c, " + 
			"SBQQ__RevisedSubscription__r.Id, " + 
			"SBQQ__RevisedSubscription__r.CurrencyIsoCode, " + 
			"SBQQ__RevisedSubscription__r.CreatedDate, " + 
			"SBQQ__RevisedSubscription__r.LastModifiedDate, " + 
			"SBQQ__RevisedSubscription__r.SBQQ__Product__r.Id, " + 
			"SBQQ__RevisedSubscription__r.SBQQ__Product__r.ProductCode, " +
			"SBQQ__RevisedSubscription__r.SBQQ__Product__r.Family, " + 
			"SBQQ__RevisedSubscription__r.SBQQ__Product__r.QuantityUnitOfMeasure, " + 
			"SBQQ__RevisedSubscription__r.SBQQ__Product__r.Description, " + 
			"SBQQ__RevisedSubscription__r.SBQQ__Quantity__c, " + 
			"SBQQ__RevisedSubscription__r.SBQQ__ListPrice__c, " + 
			"SBQQ__RevisedSubscription__r.SBQQ__CustomerPrice__c, " + 
			"SBQQ__RevisedSubscription__r.SBQQ__StartDate__c, " + 
			"SBQQ__RevisedSubscription__r.SBQQ__EndDate__c, " + 
			"SBQQ__QuoteLine__r.Id, " +
			"SBQQ__QuoteLine__r.CreatedDate, " +
			"SBQQ__QuoteLine__r.LastModifiedDate " +
//			"SBQQ__QuoteLine__r.SBQQ__UpgradedSubscription__r.Id, " + 
//			"SBQQ__QuoteLine__r.SBQQ__UpgradedSubscription__r.CurrencyIsoCode, " + 
//			"SBQQ__QuoteLine__r.SBQQ__UpgradedSubscription__r.CreatedDate, " + 
//			"SBQQ__QuoteLine__r.SBQQ__UpgradedSubscription__r.LastModifiedDate, " + 
//			"SBQQ__QuoteLine__r.SBQQ__UpgradedSubscription__r.SBQQ__Product__r.Id, " + 
//			"SBQQ__QuoteLine__r.SBQQ__UpgradedSubscription__r.SBQQ__Product__r.ProductCode, " +
//			"SBQQ__QuoteLine__r.SBQQ__UpgradedSubscription__r.SBQQ__Product__r.Family, " + 
//			"SBQQ__QuoteLine__r.SBQQ__UpgradedSubscription__r.SBQQ__Product__r.QuantityUnitOfMeasure, " + 
//			"SBQQ__QuoteLine__r.SBQQ__UpgradedSubscription__r.SBQQ__Product__r.Description, " + 
//			"SBQQ__QuoteLine__r.SBQQ__UpgradedSubscription__r.SBQQ__Quantity__c, " + 
//			"SBQQ__QuoteLine__r.SBQQ__UpgradedSubscription__r.SBQQ__ListPrice__c, " + 
//			"SBQQ__QuoteLine__r.SBQQ__UpgradedSubscription__r.SBQQ__CustomerPrice__c, " + 
//			"SBQQ__QuoteLine__r.SBQQ__UpgradedSubscription__r.SBQQ__StartDate__c, " + 
//			"SBQQ__QuoteLine__r.SBQQ__UpgradedSubscription__r.SBQQ__EndDate__c " + 
//			"SBQQ__QuoteLine__r.SBQQ__RenewedSubscription__r.Id, " + 
//			"SBQQ__QuoteLine__r.SBQQ__RenewedSubscription__r.CurrencyIsoCode, " + 
//			"SBQQ__QuoteLine__r.SBQQ__RenewedSubscription__r.CreatedDate, " + 
//			"SBQQ__QuoteLine__r.SBQQ__RenewedSubscription__r.LastModifiedDate, " + 
//			"SBQQ__QuoteLine__r.SBQQ__RenewedSubscription__r.SBQQ__Product__r.Id, " + 
//			"SBQQ__QuoteLine__r.SBQQ__RenewedSubscription__r.SBQQ__Product__r.ProductCode, " + 
//			"SBQQ__QuoteLine__r.SBQQ__RenewedSubscription__r.SBQQ__Product__r.Family, " + 
//			"SBQQ__QuoteLine__r.SBQQ__RenewedSubscription__r.SBQQ__Product__r.QuantityUnitOfMeasure, " + 
//			"SBQQ__QuoteLine__r.SBQQ__RenewedSubscription__r.SBQQ__Product__r.Description, " + 
//			"SBQQ__QuoteLine__r.SBQQ__RenewedSubscription__r.SBQQ__Quantity__c, " + 
//			"SBQQ__QuoteLine__r.SBQQ__RenewedSubscription__r.SBQQ__ListPrice__c, " + 
//			"SBQQ__QuoteLine__r.SBQQ__RenewedSubscription__r.SBQQ__CustomerPrice__c, " + 
//			"SBQQ__QuoteLine__r.SBQQ__RenewedSubscription__r.SBQQ__StartDate__c, " + 
//			"SBQQ__QuoteLine__r.SBQQ__RenewedSubscription__r.SBQQ__EndDate__c " + 
			"From SBQQ__Revisions__r) " + 
			"From SBQQ__Subscription__c ";

	/**
	 * 
	 * @param connection
	 * @param id
	 * @return
	 */
	
	public Optional<Subscription> findById(Connection connection, String id) {
		Datastore datastore = createDatastore(connection);		
		List<Subscription> subscriptionList = datastore.createQuery(QUERY_STRING
				.concat(" Where Id = '")
				.concat(id)
				.concat("'"), Subscription.class)
				.getResults();
		return subscriptionList.stream().findFirst();
	}
}