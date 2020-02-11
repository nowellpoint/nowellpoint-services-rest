package com.nowellpoint.services.rest.model.sforce;

import java.time.LocalDate;

import javax.json.Json;
import javax.json.JsonObject;

import com.nowellpoint.services.rest.model.sforce.annotation.Column;
import com.nowellpoint.services.rest.model.sforce.annotation.Entity;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;

@Getter
@Entity("Contract")
@RegisterForReflection
public class Contract extends SObject {
	@Column(value="ContractNumber") private String contractNumber;
	@Column(value="Status") private String status;
	@Column(value="StartDate") private LocalDate startDate;
	@Column(value="EndDate") private LocalDate endDate;
	
	@Override
	public JsonObject asJsonObject() {
		return Json.createObjectBuilder()
				.add("id", getId())
				.add("contractNumber", contractNumber)
				.build();
	}
}