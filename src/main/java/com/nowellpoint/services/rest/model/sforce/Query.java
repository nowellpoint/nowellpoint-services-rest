package com.nowellpoint.services.rest.model.sforce;

import java.util.List;

public interface Query<T> {
	public List<T> getResults();
}