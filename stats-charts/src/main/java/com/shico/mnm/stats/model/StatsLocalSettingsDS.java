package com.shico.mnm.stats.model;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourcePasswordField;
import com.smartgwt.client.data.fields.DataSourceTextField;

public class StatsLocalSettingsDS extends DataSource {
	public final static String PRIMARY_KEY = "_statsSettingsLocalStorageDS";
	
	public StatsLocalSettingsDS(String datasourceID) {
		super(datasourceID);
		setClientOnly(true);
		
		DataSourceTextField primaryKey = new DataSourceTextField(PRIMARY_KEY);
		primaryKey.setPrimaryKey(true);
		primaryKey.setHidden(true);
		
		setFields(getFields());
		addField(primaryKey);

	}

	public DataSourceField[] getFields() {
		return new DataSourceField[]{
				new DataSourceTextField(StatsRemoteSettingsDS.APP),
				new DataSourceTextField(StatsRemoteSettingsDS.CHARTURL),
				new DataSourceTextField(StatsRemoteSettingsDS.CHARTUSER),
				new DataSourcePasswordField(StatsRemoteSettingsDS.CHARTPWD),
				new DataSourceIntegerField(StatsRemoteSettingsDS.CHARTREFRESHINTERVAL),
				new DataSourceIntegerField(StatsRemoteSettingsDS.CHARTWINSIZE)
		};
	}	
}
