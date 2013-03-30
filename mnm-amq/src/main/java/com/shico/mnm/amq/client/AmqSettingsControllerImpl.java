package com.shico.mnm.amq.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.shico.mnm.amq.model.AmqLocalSettingsDS;
import com.shico.mnm.amq.model.AmqRemoteSettingsDS;
import com.shico.mnm.amq.model.BrokerInfoDS;
import com.shico.mnm.amq.model.MessageListDS;
import com.shico.mnm.amq.model.QueueListDS;
import com.shico.mnm.common.client.AbstractSettingsController;
import com.shico.mnm.common.client.DefaultRestDS;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;

public class AmqSettingsControllerImpl extends AbstractSettingsController implements AmqSettingsController {
	
	AmqRemoteSettingsDS adminSettingsRemoteDS;
	AmqLocalSettingsDS adminSettingsLocalStorageDS;
	BrokerInfoDS brokerInfoDS; 
	QueueListDS queueListDS;
	MessageListDS messageListDS;
	
	@Override
	public BrokerInfoDS getBrokerInfoDS() {
		return brokerInfoDS;
	}

	@Override
	public void setBrokerInfoDS(BrokerInfoDS brokerInfoDS) {
		if(this.brokerInfoDS != null){
			this.brokerInfoDS.destroy();
		}
		this.brokerInfoDS = brokerInfoDS;
	}

	public void setMessageListDS(MessageListDS messageListDS) {
		this.messageListDS = messageListDS;
	}
	
	@Override
	public boolean useLocalStorage() {
		return true;
	}

	@Override
	protected DefaultRestDS getRemoteDS() {
		if(adminSettingsRemoteDS == null){
			adminSettingsRemoteDS = new AmqRemoteSettingsDS("AmqAdminSettingsDS", GWT.getHostPageBaseURL()+AmqClientHandle.ADMIN_REST_URL+"settings");			
		}
		return adminSettingsRemoteDS;
	}

	@Override
	protected DataSource getLocalDS() {
		if(adminSettingsLocalStorageDS == null){
			adminSettingsLocalStorageDS = new AmqLocalSettingsDS("AmqAdminSettingsLocalStorageDS");
			Map<String, String> cacheData = new HashMap<String, String>();
			cacheData.put(AmqLocalSettingsDS.PRIMARY_KEY, "999");
			adminSettingsLocalStorageDS.addData(new Record(cacheData));			
		}
		return adminSettingsLocalStorageDS;
	}

	@Override
	protected String getAppName() {
		return AmqClientHandle.APP_NAME;
	}	
}
