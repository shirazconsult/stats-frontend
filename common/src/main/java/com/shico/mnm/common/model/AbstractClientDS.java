package com.shico.mnm.common.model;

import java.util.HashMap;
import java.util.Map;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.fields.DataSourceTextField;

public abstract class AbstractClientDS implements ClientDatasource {
	protected Map<String, Object> backingBean;
	protected DataSource datasource;

	public abstract DataSourceField[] getFields();
	protected abstract String getDataSourceID();
	
	protected String getPrimaryKeyValue(){
		return "1";
	}
	
	@Override
	public void setData(Map<String, Object> backingBean) {
		this.backingBean = backingBean; 
		backingBean.put(PRIMARY_KEY, getPrimaryKeyValue());
		Record record = new Record(backingBean);
		getDataSource().addData(record);
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> getData() {
		if(datasource != null){			
			Record[] data = datasource.getTestData();
			if(data.length >= 1){
				Map map = data[0].toMap();
				map.remove(PRIMARY_KEY);
				return map;
			}
		}
		return new HashMap<String, Object>();
	}

	@Override
	public DataSource getDataSource() {
		if(datasource == null){
			datasource = new DataSource();
			datasource.setID(getDataSourceID());
			datasource.setClientOnly(true);
			
			DataSourceTextField primaryKey = new DataSourceTextField(PRIMARY_KEY);
			primaryKey.setPrimaryKey(true);
			primaryKey.setHidden(true);
			
			datasource.setFields(getFields());
			datasource.addField(primaryKey);
		}
		return datasource;
	}

	@Override
	public void destroy(){
		if(backingBean != null){
			backingBean.clear();
			backingBean = null;
		}
		if(datasource != null){
			datasource.destroy();
			datasource = null;
		}

	}
}
