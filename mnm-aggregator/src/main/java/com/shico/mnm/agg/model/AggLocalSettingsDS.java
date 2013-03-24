package com.shico.mnm.agg.model;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourcePasswordField;
import com.smartgwt.client.data.fields.DataSourceTextField;

public class AggLocalSettingsDS extends DataSource {
	public final static String PRIMARY_KEY = "_aggSettingsLocalStorageDS";
	
	public AggLocalSettingsDS(String datasourceID) {
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
				new DataSourceTextField(AggRemoteSettingsDS.APP),
				new DataSourceTextField(AggRemoteSettingsDS.AGGREGATORURL),
				new DataSourceTextField(AggRemoteSettingsDS.CHARTURL),
				new DataSourceTextField(AggRemoteSettingsDS.AGGREGATORUSER),
				new DataSourceTextField(AggRemoteSettingsDS.CHARTUSER),
				new DataSourcePasswordField(AggRemoteSettingsDS.AGGREGATORPWD),
				new DataSourcePasswordField(AggRemoteSettingsDS.CHARTPWD),
				new DataSourceIntegerField(AggRemoteSettingsDS.CHARTREFRESHINTERVAL),
				new DataSourceIntegerField(AggRemoteSettingsDS.CHARTWINSIZE)
		};
	}	
}
