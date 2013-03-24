package com.shico.mnm.common.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.storage.client.Storage;
import com.google.gwt.storage.client.StorageMap;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;

public abstract class AbstractSettingsController implements SettingsController {

	Map<String, Object> settingsMap = new HashMap<String, Object>();
	
	protected abstract DefaultRestDS getRemoteDS();
	protected abstract DataSource getLocalDS();
	protected abstract String getAppName();
	
	@Override
	public Object getSetting(String key) {
		if(settingsMap == null){
			return null;
		}
		return settingsMap.get(key);
	}


	@Override
	public DataSource getSettingsDS() {
		if(useLocalStorage()){
			return getLocalDS();
		}
		return getRemoteDS();
	}

	@Override
	public Map<String, Object> getSettingsMap() {
		return settingsMap;
	}

	public void setSettingsMap(Map<String, Object> settingsMap) {
		if(useLocalStorage()){
			this.settingsMap = getLocalDS().getCacheData()[0].toMap();
		}
		this.settingsMap = settingsMap;
	}

	@Override
	public void setSettingsMapFromLocalStorage() {
		Storage settingsStorage = Storage.getLocalStorageIfSupported();
		if(settingsStorage == null){
			throw new IllegalStateException("Cannot read settings from local storage, because it is not supported. Please use a browser with HTML5.");
		}
		if(getLocalDS() == null || getLocalDS().getClientOnly() == null || !getLocalDS().getClientOnly().booleanValue()){
			throw new IllegalStateException("Cannot read local settings into DataSource, since the provided datasource is not client-only.");
		}
		
		StorageMap sm = new StorageMap(settingsStorage);
		Map<String, Object> data = new HashMap<String, Object>();
		if(sm != null && !sm.isEmpty()){
			Map<String, Object> settingsMap = new HashMap<String, Object>();
			for (Entry<String, String> entry : sm.entrySet()) {
				if(entry.getKey().startsWith(getAppName())){
					String key = entry.getKey().substring(getAppName().length()+1);
					settingsMap.put(key, entry.getValue());
					data.put(key, entry.getValue());
				}
			}			
			this.settingsMap = settingsMap; 
			
			data.put(getLocalDS().getPrimaryKeyFieldName(), data.get(getLocalDS().getPrimaryKeyFieldName()));
			getLocalDS().setCacheData(new Record(data));
		}
		

	}

}
