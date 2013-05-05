package com.shico.mnm.stats.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.shico.mnm.common.client.AbstractSettingsController;
import com.shico.mnm.common.client.DefaultRestDS;
import com.shico.mnm.stats.model.StatsLocalSettingsDS;
import com.shico.mnm.stats.model.StatsRemoteSettingsDS;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;

public class StatsSettingsController extends AbstractSettingsController {
	final static boolean useLocalStorage = true;
	
	StatsRemoteSettingsDS adminSettingsRemoteDS;
	StatsLocalSettingsDS adminSettingsLocalStorageDS;
	
	@Override
	public boolean useLocalStorage() {
		return true;
	}


	@Override
	protected DefaultRestDS getRemoteDS() {
		if(adminSettingsRemoteDS == null){
			adminSettingsRemoteDS = new StatsRemoteSettingsDS("StatsSettingsDS", GWT.getHostPageBaseURL()+StatsClientHandle.STATS_REST_PATH+"settings");			
		}
		return adminSettingsRemoteDS;
	}


	@Override
	protected DataSource getLocalDS() {
		if(adminSettingsLocalStorageDS == null){
			adminSettingsLocalStorageDS = new StatsLocalSettingsDS("AggSettingsLocalStorageDS");
			Map<String, String> cacheData = new HashMap<String, String>();
			cacheData.put(StatsLocalSettingsDS.PRIMARY_KEY, "888");
			adminSettingsLocalStorageDS.addData(new Record(cacheData));			
		}
		return adminSettingsLocalStorageDS;
	}


	@Override
	protected String getAppName() {
		return StatsClientHandle.APP_NAME;
	}	
}
