package com.nowellpoint.services.rest.model.sforce;

import java.time.LocalDate;

import javax.json.JsonObject;

import com.nowellpoint.services.rest.model.sforce.annotation.Column;
import com.nowellpoint.services.rest.model.sforce.annotation.Entity;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;

@Getter
@RegisterForReflection
@Entity("Contract")
public class Contract extends SObject {
	@Column(value="ContractNumber") private String contractNumber;
	@Column(value="Status") private String status;
	@Column(value="StartDate") private LocalDate startDate;
	@Column(value="EndDate") private LocalDate endDate;
	
	@Override
	public JsonObject asJsonObject() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}