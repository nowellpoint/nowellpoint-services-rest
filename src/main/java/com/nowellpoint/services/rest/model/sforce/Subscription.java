package com.nowellpoint.services.rest.model.sforce;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.stream.JsonCollectors;

import com.nowellpoint.services.rest.model.sforce.annotation.Column;
import com.nowellpoint.services.rest.model.sforce.annotation.Entity;
import com.nowellpoint.services.rest.model.sforce.annotation.OneToMany;
import com.nowellpoint.services.rest.model.sforce.annotation.OneToOne;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;

@Getter
@Entity("SBQQ__Subscription__c")
@RegisterForReflection
public class Subscription extends SObject {
	@OneToOne(value="SBQQ__Account__r") private Account account;
	@OneToOne(value="SBQQ__Contract__r") private Contract contract;
	@Column(value="SBQQ__Quantity__c") private Double quantity;
	@Column(value="SBQQ__ListPrice__c") private Double listPrice;
	@Column(value="SBQQ__Discount__c") private Double discount;
	@Column(value="SBQQ__DistributorDiscount__c") private Double distributorDiscount;
	@Column(value="SBQQ__OptionDiscount__c") private Double optionDiscount;
	@Column(value="SBQQ__CustomerPrice__c") private Double customerPrice;
	@Column(value="SBQQ__RegularPrice__c") private Double regularPrice;
	@Column(value="SBQQ__StartDate__c") private LocalDate startDate;
	@Column(value="SBQQ__EndDate__c") private LocalDate endDate;
	@OneToOne(value="SBQQ__RevisedSubscription__r") private Subscription revisedSubscription;
	@OneToOne(value="SBQQ__Product__r") private Product product;
	@OneToOne(value="SBQQ__QuoteLine__r") private QuoteLine quoteLine;
	@OneToMany(value="SBQQ__Revisions__r") private List<Subscription> revisions;
	
	public final String ACTIVE = "Active";
	
	/**
	 * @return
	 */
	
	@Override
	public JsonObject asJsonObject() {
		return Json.createObjectBuilder()
				.add("id", 										getId())
				.add("currencyIsoCode",                         addCurrencyIsoCode())
				.add("createdDate", 							addCreatedDate())
				.add("lastModifiedDate", 						addLastModifiedDate())
				.add("quantity", 								quantity)
				.add("listPrice", 								listPrice)
				.add("discount", 								getOrDefault(discount))
				.add("distributorDiscount", 					getOrDefault(distributorDiscount))
				.add("optionDiscount", 							getOrDefault(optionDiscount))
				.add("customerPrice", 							customerPrice)
				.add("regularPrice", 							getOrDefault(regularPrice))
				.add("startDate", 								startDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
				.add("endDate", 								endDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
				.add("status", 									getStatus())
				.add("annualRecurringRevenueType",              getAnnualRecurringRevenueType())
				.add("annualRecurringRevenue", 					getAnnualRecurringRevenue())
				//.add("revisedSubscription", 					addRevisedSubscription())
				.add("contract",                                addContract())
				.add("product", 								addProduct())
				.add("quoteLine", 								addQuoteLine())
				.add("revisions", 								addRevisions())
				.build();
	}
	
	private JsonValue addContract() {
		return contract == null ? JsonValue.NULL : contract.asJsonObject();
	}
	
	private JsonValue addProduct() {
		return product == null ? JsonValue.NULL : product.asJsonObject();
	}
	
	private JsonValue addQuoteLine() {
		return quoteLine == null ? JsonValue.NULL : quoteLine.asJsonObject();
	}
	
	private JsonValue addRevisions() {
		return revisions == null ? JsonValue.NULL : revisions.stream()
				.map(Subscription::asJsonObject)
				.collect(JsonCollectors.toJsonArray());
	}
	
	public String getStatus() {
		var today = LocalDate.now();
		if (today.isBefore(getStartDate())) {
			return "DRAFT";
		} else if (today.isAfter(getEndDate())) {
			return "EXPIRED";
		} else {
			return ACTIVE;
		}
	}
	
	private String getAnnualRecurringRevenueType() {
		if (quoteLine == null) {
			return "N/A"; 
		} else if (revisedSubscription != null) {
			return AnnualRecurringRevenueType.INCREMENTAL.name();
		} else if (quoteLine.getRenewedSubscription() != null) {
			return AnnualRecurringRevenueType.RENEWAL.name();
		} else {
			return AnnualRecurringRevenueType.NEW.name();
		}
	}
	
	private Double getAnnualRecurringRevenue() {
		var days = getStartDate().until(getEndDate().plus(1, ChronoUnit.DAYS), ChronoUnit.DAYS);
		var date = getStartDate();
		
		while (date.isBefore(getEndDate()) || date.isEqual(getEndDate())) {
			if (date.getMonthValue() == 2 && date.getDayOfMonth() == 29) {
				days--;
			}
			date = date.plus(1, ChronoUnit.DAYS);
		}
		
		var dailyRate = getCustomerPrice() / days;
		var annualRecurringRevenue = new BigDecimal(dailyRate * (days < 365 ? days : 365)).setScale(2, RoundingMode.HALF_EVEN);
		
		return annualRecurringRevenue.doubleValue();
	}
}