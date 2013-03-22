package com.shico.mnm.amq.model;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourcePasswordField;
import com.smartgwt.client.data.fields.DataSourceTextField;

public class AmqLocalSettingsDS extends DataSource {
	public final static String PRIMARY_KEY = "_adminSettingsLocalStorageDS";
	
	public AmqLocalSettingsDS(String datasourceID) {
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
				new DataSourceTextField(AmqRemoteSettingsDS.APP),
				new DataSourceTextField(AmqRemoteSettingsDS.BROKERURL),
				new DataSourceTextField(AmqRemoteSettingsDS.CHARTURL),
				new DataSourceTextField(AmqRemoteSettingsDS.BROKERUSER),
				new DataSourceTextField(AmqRemoteSettingsDS.CHARTUSER),
				new DataSourcePasswordField(AmqRemoteSettingsDS.BROKERPWD),
				new DataSourcePasswordField(AmqRemoteSettingsDS.CHARTPWD),
				new DataSourceIntegerField(AmqRemoteSettingsDS.CHARTREFRESHINTERVAL),
				new DataSourceIntegerField(AmqRemoteSettingsDS.CHARTWINSIZE)
		};
	}	
}
