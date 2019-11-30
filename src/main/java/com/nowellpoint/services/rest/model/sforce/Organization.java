package com.nowellpoint.services.rest.model.sforce;

import javax.json.bind.annotation.JsonbProperty;

import lombok.Getter;

@Getter
public class Organization {
	@JsonbProperty(value="Id") private String id;
	@JsonbProperty(value="Division") private String division;
	@JsonbProperty(value="Fax") private String fax;
	@JsonbProperty(value="DefaultLocaleSidKey") private String defaultLocaleSidKey;
	@JsonbProperty(value="FiscalYearStartMonth") private String fiscalYearStartMonth;
	@JsonbProperty(value="InstanceName") private String instanceName;
	@JsonbProperty(value="IsSandbox") private Boolean isSandbox;
	@JsonbProperty(value="LanguageLocaleKey") private String languageLocaleKey;
	@JsonbProperty(value="Name") private String name;
	@JsonbProperty(value="OrganizationType") private String organizationType;
	@JsonbProperty(value="Phone") private String phone;
	@JsonbProperty(value="PrimaryContact") private String primaryContact;
	@JsonbProperty(value="UsesStartDateAsFiscalYearName") private Boolean usesStartDateAsFiscalYearName;
	@JsonbProperty(value="Address") private Address address;
}