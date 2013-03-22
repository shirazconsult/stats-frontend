package com.shico.mnm.amq.model;

import com.shico.mnm.common.client.DefaultRestDS;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.types.FieldType;

public class BrokerInfoDS extends DefaultRestDS {
	public final static String restPathInfo = "brokerInfo"; 
	public final static String defaultDataSourceID = "BrokerInfoDS";
	
	public final static String BROKER_NAME = "brokerName";
	public final static String BROKER_VER = "brokerVersion";
	public final static String IS_PERSISTENT = "isPersistent";
	public final static String IS_SLAVE = "isSlave";
	public final static String BROKER_ID = "brokerId";
	public final static String DATA_DIR = "dataDirectory";
	public final static String MEM_LIMIT = "memoryLimit";
	public final static String STORE_LIMIT = "storeLimit";
	public final static String TEMP_LIMIT = "tempLimit";

	public BrokerInfoDS(String datasourceID, String restUrl) {
		super(datasourceID);
		if(restUrl.endsWith("/")){
			setDataURL(restUrl + restPathInfo);
		}else{
			setDataURL(restUrl + "/" + restPathInfo);
		}
	}

	public BrokerInfoDS(String restUrl) {
		this(defaultDataSourceID, restUrl);
	}

	@Override
	public DataSourceField[] getFields() {		
		return new DataSourceField[]{
				new DataSourceField("brokerName", FieldType.TEXT, "Name"),
				new DataSourceField("brokerVersion", FieldType.TEXT, "Version"),
				new DataSourceField("isPersistent", FieldType.TEXT, "Persistent"),
				new DataSourceField("isSlave", FieldType.TEXT, "Slave"),
				new DataSourceField("brokerId", FieldType.TEXT, "Broker ID"),
				new DataSourceField("dataDirectory", FieldType.TEXT, "Data Directory"),
				new DataSourceField("memoryLimit", FieldType.TEXT, "Memory Limit"),
				new DataSourceField("storeLimit", FieldType.TEXT, "Store Limit"),
				new DataSourceField("tempLimit", FieldType.TEXT, "Temp Limit")
		};
	}
	
}
