package com.shico.mnm.common.model;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.storage.client.StorageEvent;
import com.google.gwt.storage.client.StorageMap;
import com.shico.mnm.common.event.DataEventType;
import com.shico.mnm.common.event.DataLoadedEvent;
import com.shico.mnm.common.event.EventBus;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.FormItem;

public class SettingsValuesManager extends ValuesManager {

	private boolean useLocalStorage;
	private StorageEvent.Handler storageEventHandler;
	private Storage settingsStorage;
	private String space = "";
	
	public SettingsValuesManager(String appName) {
		super();
		this.space = appName+".";
	}

	public SettingsValuesManager(JavaScriptObject jsObj) {
		super(jsObj);
	}

	
	@Override
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
		if(dataSource.getClientOnly() != null){
			useLocalStorage = true;
			settingsStorage = Storage.getLocalStorageIfSupported();
			if(settingsStorage == null){
				throw new IllegalStateException("Local storage is not supported.");
			}
			if(storageEventHandler == null){
				Storage.addStorageEventHandler(new StorageEvent.Handler() {
					@Override
					public void onStorageChange(StorageEvent event) {
						// ???
					}
				});
			}			
		}
	}

	@Override
	public void fetchData(Criteria criteria) {
		if(useLocalStorage){
			fetchFromLocalStorage();
		}else{
			super.fetchData(criteria);
		}
	}

	@Override
	public void saveData() {
		if(useLocalStorage){
			insertIntoLocalStorage();
		}else{
			super.saveData();
		}
	}

	public void saveData(final Callback<Map, String> callback) {
			if(useLocalStorage){
				try{
					insertIntoLocalStorage();
				}catch(Exception e){
					callback.onFailure("Failed to save settings. Reason: "+e.getMessage());
					return;
				}
				callback.onSuccess(getDataSource().getCacheData()[0].toMap());
			}else{
				super.saveData(new DSCallback() {					
					@Override
					public void execute(DSResponse response, Object rawData, DSRequest request) {
						if(response.getStatus() == 0){
							callback.onSuccess(response.getData()[0].toMap());
						}else{
							callback.onFailure("Could not save settings on server. Error code = "+response.getStatus());
						}
					}
				});
			}
	}

	@Override
	public void saveData(DSCallback callback) {
		if(!useLocalStorage){
			super.saveData(callback);
		}else{
			throw new IllegalArgumentException("Cannot invoke DSCallback method when using Local Storage for settings.");
		}
	}

	private void fetchFromLocalStorage(){
		StorageMap sm = new StorageMap(settingsStorage);

		DataSource ds = getDataSource();
		Record[] cacheData = ds.getCacheData();
		Map data = null;
		if(cacheData != null && cacheData.length >= 1){
			data = ds.getCacheData()[0].toMap();
		}else{
			return;
		}

		DynamicForm[] members = getMembers();
		for (DynamicForm df : members) {
			FormItem[] fields = df.getFields();
			for (FormItem fi : fields) {
				String fieldName = fi.getName();
				if(ds.getField(fieldName) != null){
					String value = sm.get(space+fieldName);
					data.put(fieldName, value);
					fi.setValue(value);
				}
			}
		}

		data.put(ds.getPrimaryKeyFieldName(), data.get(ds.getPrimaryKeyFieldName()));
		ds.setCacheData(new Record(data));
	}
	
	private void insertIntoLocalStorage(){
		DataSource ds = getDataSource();
		Map oldData = ds.getCacheData()[0].toMap();

		Map newData = new HashMap();
		
		DynamicForm[] members = getMembers();
		for (DynamicForm df : members) {
			FormItem[] fields = df.getFields();
			for (FormItem fi : fields) {
				String fieldName = fi.getName();
				String value = (fi.getValue() == null ? "" : String.valueOf(fi.getValue()));
				if(ds.getField(fieldName) != null){
					newData.put(fieldName, value);
					settingsStorage.setItem(space+fieldName, value);
				}
			}
		}
		newData.put(ds.getPrimaryKeyFieldName(), oldData.get(ds.getPrimaryKeyFieldName()));
		ds.setCacheData(new Record(newData));
	}
}
