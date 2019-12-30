package com.nowellpoint.services.rest.model.sforce;

import java.io.InputStream;
import java.util.List;

public interface QueryResult {
	public Integer getTotalSize();
	public <T> List<T> getRecords(Class<T> type, InputStream stream);
}