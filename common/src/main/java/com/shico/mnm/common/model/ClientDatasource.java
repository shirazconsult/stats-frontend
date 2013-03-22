package com.shico.mnm.common.model;

import java.util.Map;

import com.smartgwt.client.data.DataSource;

public interface ClientDatasource {
	public final static String PRIMARY_KEY = "_key";
	
	void setData(Map<String, Object> backingBean);
	Map<String, Object> getData();
	DataSource getDataSource();
	void destroy();
}
