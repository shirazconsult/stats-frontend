package com.shico.mnm.amq.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.shico.mnm.amq.model.AmqLocalSettingsDS;
import com.shico.mnm.amq.model.AmqRemoteSettingsDS;
import com.shico.mnm.amq.model.BrokerInfoDS;
import com.shico.mnm.amq.model.MessageListDS;
import com.shico.mnm.amq.model.QueueListDS;
import com.shico.mnm.common.event.DataEventType;
import com.shico.mnm.common.event.DataLoadedEvent;
import com.shico.mnm.common.event.EventBus;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;

/**
 * My responsibilities are:
 * <ul>
 * <li> To retrieve and cache the settings from backend. 
 * <li> To listen to app-setting events and save data to the backend.
 * </ul>
 * 
 * @author farhad
 *
 */
public class AmqSettingsControllerImpl implements AmqSettingsController {
	final static boolean useLocalStorage = true;
	
	AmqRemoteSettingsDS adminSettingsDS;
	AmqLocalSettingsDS adminSettingsLocalStorageDS;
	BrokerInfoDS brokerInfoDS; 
	QueueListDS queueListDS;
	MessageListDS messageListDS;
	Map<String, Object> settingsMap;
	
	@Override
	public void loadSettings() {
		EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.AMQ_SETTINGS_LOADED_EVENT));
	}
	
	@Override
	public Object getSetting(String key) {
		if(settingsMap == null){
			return null;
		}
		return settingsMap.get(key);
	}


	@Override
	public DataSource getSettingsDS() {
		if(useLocalStorage){
			return adminSettingsLocalStorageDS;
		}
		return adminSettingsDS;
	}

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

	@Override
	public QueueListDS getQueueListDS(){
		return queueListDS;
	}

	@Override
	public void setQueueListDS(QueueListDS queueListDS) {
		if(this.queueListDS != null){
			this.queueListDS.destroy();
		}
		this.queueListDS = queueListDS;
	}
	
	@Override
	public MessageListDS getMessageListDS(){
		return messageListDS;
	}
	

	public void setMessageListDS(MessageListDS messageListDS) {
		this.messageListDS = messageListDS;
	}
	
	@Override
	public Map<String, Object> getSettingsMap() {
		return settingsMap;
	}

	public void setSettingsMap(Map<String, Object> settingsMap) {
		if(useLocalStorage){
			this.settingsMap = adminSettingsLocalStorageDS.getCacheData()[0].toMap();
		}
		this.settingsMap = settingsMap;
	}

	@Deprecated
	public String getAdminRest(){
//		String brokerUrl = getSettings().getAttributeAsString(AdminSettingsDS.BROKERURL);
//		return brokerUrl;
		return "http://127.0.0.1:9119/statistics/rest/admin/amq";
	}

	public void init() {
//		 /rest/admin/http://127.0.0.1:8888//settings
		if(useLocalStorage){
			adminSettingsLocalStorageDS = new AmqLocalSettingsDS("AmqAdminSettingsLocalStorageDS");
			Map<String, String> cacheData = new HashMap<String, String>();
			cacheData.put(AmqLocalSettingsDS.PRIMARY_KEY, "999");
//			cacheData.put("app", "mnm-amq");
//			cacheData.put("brokerUrl", "http://127.0.0.1:9119/statistics/rest/amq/admin");
//			cacheData.put("chartUrl", "http://127.0.0.1:9119/statistics/rest/monitor");
//			cacheData.put("brokerUser", "emil");
//			cacheData.put("brokerPwd", "ZZZZ");
//			cacheData.put("chartUser", "dehghani");
//			cacheData.put("chartPwd", "YYYY");
//			cacheData.put("chartRefreshInterval", "10000");
//			cacheData.put("chartWinSize", "180000");
			adminSettingsLocalStorageDS.addData(new Record(cacheData));
		}else{
			adminSettingsDS = new AmqRemoteSettingsDS("AmqAdminSettingsDS", GWT.getHostPageBaseURL()+AmqClientHandle.ADMIN_REST_URL+"settings");
			System.out.println(">>>>>>>>>>> Admin Rest URL: "+GWT.getHostPageBaseURL()+AmqClientHandle.ADMIN_REST_URL+"settings");
		}
	}	
}
