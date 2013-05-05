package com.shico.mnm.stats.model;

import com.shico.mnm.common.client.DefaultRestDS;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourcePasswordField;
import com.smartgwt.client.data.fields.DataSourceTextField;

public class StatsRemoteSettingsDS extends DefaultRestDS {
	public final static String APP = "app";
	public final static String CHARTURL = "chartUrl";
	public final static String CHARTUSER = "chartUser";
	public final static String CHARTPWD = "chartPwd";
	public final static String CHARTREFRESHINTERVAL = "chartRefreshInterval";
	public final static String CHARTWINSIZE = "chartWinSize";
	
	public StatsRemoteSettingsDS(String datasourceID, String restUrl) {
		super(datasourceID, restUrl);
	}

	@Override
	public DataSourceField[] getFields() {
		return new DataSourceField[]{
				new DataSourceTextField(APP),
				new DataSourceTextField(CHARTURL),
				new DataSourceTextField(CHARTUSER),
				new DataSourcePasswordField(CHARTPWD),
				new DataSourceIntegerField(CHARTREFRESHINTERVAL),
				new DataSourceIntegerField(CHARTWINSIZE)
		};
	}	
}
