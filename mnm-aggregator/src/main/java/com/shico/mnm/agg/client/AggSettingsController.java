package com.shico.mnm.agg.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.shico.mnm.agg.model.AggLocalSettingsDS;
import com.shico.mnm.agg.model.AggRemoteSettingsDS;
import com.shico.mnm.common.client.AbstractSettingsController;
import com.shico.mnm.common.client.DefaultRestDS;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;

public class AggSettingsController extends AbstractSettingsController {
	final static boolean useLocalStorage = true;
	
	AggRemoteSettingsDS adminSettingsRemoteDS;
	AggLocalSettingsDS adminSettingsLocalStorageDS;
//	BrokerInfoDS brokerInfoDS; 
		
//	public BrokerInfoDS getBrokerInfoDS() {
//		return brokerInfoDS;
//	}

//	public void setBrokerInfoDS(BrokerInfoDS brokerInfoDS) {
//		if(this.brokerInfoDS != null){
//			this.brokerInfoDS.destroy();
//		}
//		this.brokerInfoDS = brokerInfoDS;
//	}
	
	@Override
	public boolean useLocalStorage() {
		return true;
	}


	@Override
	protected DefaultRestDS getRemoteDS() {
		if(adminSettingsRemoteDS == null){
			adminSettingsRemoteDS = new AggRemoteSettingsDS("AggSettingsDS", GWT.getHostPageBaseURL()+AggClientHandle.AGGREGATOR_REST_PATH+"settings");			
		}
		return adminSettingsRemoteDS;
	}


	@Override
	protected DataSource getLocalDS() {
		if(adminSettingsLocalStorageDS == null){
			adminSettingsLocalStorageDS = new AggLocalSettingsDS("AggSettingsLocalStorageDS");
			Map<String, String> cacheData = new HashMap<String, String>();
			cacheData.put(AggLocalSettingsDS.PRIMARY_KEY, "888");
			adminSettingsLocalStorageDS.addData(new Record(cacheData));			
		}
		return adminSettingsLocalStorageDS;
	}


	@Override
	protected String getAppName() {
		return AggClientHandle.APP_NAME;
	}	
}
