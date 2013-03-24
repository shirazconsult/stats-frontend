package com.shico.mnm.agg.model;

import com.shico.mnm.common.client.DefaultRestDS;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourcePasswordField;
import com.smartgwt.client.data.fields.DataSourceTextField;

public class AggRemoteSettingsDS extends DefaultRestDS {
	public final static String APP = "app";
	public final static String AGGREGATORURL = "aggregatorUrl";
	public final static String CHARTURL = "chartUrl";
	public final static String AGGREGATORUSER = "aggregatorUser";
	public final static String AGGREGATORPWD = "aggregatorPwd";
	public final static String CHARTUSER = "chartUser";
	public final static String CHARTPWD = "chartPwd";
	public final static String CHARTREFRESHINTERVAL = "chartRefreshInterval";
	public final static String CHARTWINSIZE = "chartWinSize";
	
	public AggRemoteSettingsDS(String datasourceID, String restUrl) {
		super(datasourceID, restUrl);
	}

	@Override
	public DataSourceField[] getFields() {
		return new DataSourceField[]{
				new DataSourceTextField(APP),
				new DataSourceTextField(AGGREGATORURL),
				new DataSourceTextField(CHARTURL),
				new DataSourceTextField(AGGREGATORUSER),
				new DataSourceTextField(CHARTUSER),
				new DataSourcePasswordField(AGGREGATORPWD),
				new DataSourcePasswordField(CHARTPWD),
				new DataSourceIntegerField(CHARTREFRESHINTERVAL),
				new DataSourceIntegerField(CHARTWINSIZE)
		};
	}	
}
