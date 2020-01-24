package com.nowellpoint.services.rest.model.sforce;

public enum AnnualRecurringRevenueType {
	RENEWAL("Renewal"),
	NEW("New"),
	INCREMENTAL("Incremental"),
	CHRUN("Churn");
	
	private String annualRecurringRevenueType;
	
	AnnualRecurringRevenueType(String annualRecurringRevenueType) {
		this.annualRecurringRevenueType = annualRecurringRevenueType;
	}
	
	public String getAnnualRecurringReveueType() {
		return annualRecurringRevenueType;
	}
}