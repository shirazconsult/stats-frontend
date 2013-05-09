package com.shico.mnm.agg.model;

import com.shico.mnm.common.client.DefaultRestDS;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceTextField;

public class AggConfSettingsDS extends DefaultRestDS {
	public final static String restPathInfo = "settings";
	public final static String defaultDataSourceID = "AggConfSettingsDS";
	public final static String AGG_NAME = "aggregatorName";

	public final static String NAME = "name";
	public final static String VALUE = "value";
	public final static String READ_ONLY = "readonly";
	public final static String REQUIRE_RESTART = "requireRestart";
	
	public AggConfSettingsDS(String datasourceID, String restUrl) {
		super(datasourceID, restUrl);

		if(restUrl != null && !restUrl.trim().isEmpty()){
			if(restUrl.endsWith("/")){
				setDataURL(restUrl + restPathInfo);
			}else{
				setDataURL(restUrl + "/" + restPathInfo);
			}
		}
		
		DataSourceTextField nameField = new DataSourceTextField(NAME);
		nameField.setPrimaryKey(true);
		addField(nameField);
		
		DataSourceTextField valueField = new DataSourceTextField(VALUE);
		addField(valueField);
		DataSourceBooleanField readonlyField = new DataSourceBooleanField(READ_ONLY);
		addField(readonlyField);
		DataSourceBooleanField requireRestartField = new DataSourceBooleanField(REQUIRE_RESTART);
		addField(requireRestartField);
	}
	
	public AggConfSettingsDS(String restUrl) {
		this(defaultDataSourceID, restUrl);
	}

}