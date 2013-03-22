package com.shico.mnm.common.model;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.storage.client.StorageEvent;
import com.google.gwt.storage.client.StorageMap;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.FormItem;

public class SettingsValuesManager extends ValuesManager {

	private boolean useLocalStorage;
	private StorageEvent.Handler storageEventHandler;
	private Storage settingsStorage;
	
	public SettingsValuesManager() {
		super();
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
//			DataSource ds = getDataSource();
//			Map data = ds.getCacheData()[0].toMap();
//			
//			DynamicForm[] members = getMembers();
//			for (DynamicForm df : members) {
//				FormItem[] fields = df.getFields();
//				for (FormItem fi : fields) {
//					if(ds.getField(fi.getName()) != null){
//						fi.setValue(data.get(fi.getName()));
//					}
//				}
//			}
		}else{
			super.fetchData(criteria);
		}
	}

	@Override
	public void saveData() {
		if(useLocalStorage){
			insertIntoLocalStorage();
//			DataSource ds = getDataSource();
//			Map oldData = ds.getCacheData()[0].toMap();
//
//			Map newData = new HashMap();
//			
//			DynamicForm[] members = getMembers();
//			for (DynamicForm df : members) {
//				FormItem[] fields = df.getFields();
//				for (FormItem fi : fields) {
//					if(ds.getField(fi.getName()) != null){
//						newData.put(fi.getName(), fi.getValue());
//					}
//				}
//			}
//			newData.put(ds.getPrimaryKeyFieldName(), oldData.get(ds.getPrimaryKeyFieldName()));
//			ds.setCacheData(new Record(newData));
		}else{
			super.saveData();
		}
	}
	
	private void fetchFromLocalStorage(){
		StorageMap sm = new StorageMap(settingsStorage);

		DataSource ds = getDataSource();
		Map data = ds.getCacheData()[0].toMap();

		DynamicForm[] members = getMembers();
		for (DynamicForm df : members) {
			FormItem[] fields = df.getFields();
			for (FormItem fi : fields) {
				String fieldName = fi.getName();
				if(ds.getField(fieldName) != null){
					data.put(fieldName, sm.get(fieldName));
					fi.setValue(sm.get(fieldName));
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
				if(ds.getField(fieldName) != null){
					newData.put(fieldName, fi.getValue());
					settingsStorage.setItem(fieldName,String.valueOf(fi.getValue()));
				}
			}
		}
		newData.put(ds.getPrimaryKeyFieldName(), oldData.get(ds.getPrimaryKeyFieldName()));
		ds.setCacheData(new Record(newData));
	}
	
	private void printMap(Map map){
		if(map == null){
			return;
		}
		for (Object key : map.keySet()) {
			System.out.println(":::: "+key+"="+map.get(key));
		}
	}
}
