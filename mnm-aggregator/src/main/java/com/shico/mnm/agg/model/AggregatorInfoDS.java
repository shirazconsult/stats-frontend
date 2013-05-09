package com.shico.mnm.agg.model;

import com.shico.mnm.common.client.DefaultRestDS;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.types.FieldType;

public class AggregatorInfoDS extends DefaultRestDS {	
	public final static String restPathInfo = "info"; 
	public final static String defaultDataSourceID = "AggregatorInfoDS";
	
	public final static String AGG_NAME = "aggregatorName";
	public final static String AGG_VER = "version";
	public final static String PROTO_VER = "protocolVersion";
	public final static String HOME_DIR = "homeDir";
	public final static String UPTIME = "Uptime";
	public final static String LAST_EXCH_COMPL_TS = "LastExchangeCompletedTimestamp";
	public final static String EXCH_TOTAL = "ExchangesTotal";
	public final static String EXCH_COMPL = "ExchangesCompleted";

	public AggregatorInfoDS(String datasourceID, String restUrl) {
		super(datasourceID);
		if(restUrl != null && !restUrl.trim().isEmpty()){
			if(restUrl.endsWith("/")){
				setDataURL(restUrl + restPathInfo);
			}else{
				setDataURL(restUrl + "/" + restPathInfo);
			}
		}
	}

	public AggregatorInfoDS(String restUrl) {
		this(defaultDataSourceID, restUrl);
	}

	@Override
	public DataSourceField[] getFields() {		
		return new DataSourceField[]{
				new DataSourceField(AGG_NAME, FieldType.TEXT, "Name"),
				new DataSourceField(AGG_VER, FieldType.TEXT, "Version"),
				new DataSourceField(PROTO_VER, FieldType.TEXT, "Protocol Version"),
				new DataSourceField(HOME_DIR, FieldType.TEXT, "Home Directory"),
				new DataSourceField(UPTIME, FieldType.TEXT, "Uptime"),
				new DataSourceField(LAST_EXCH_COMPL_TS, FieldType.TEXT, "Last Completed Exchange"),
				new DataSourceField(EXCH_TOTAL, FieldType.TEXT, "Total Exchanges"),
				new DataSourceField(EXCH_COMPL, FieldType.TEXT, "Completed Exchanges"),
		};
	}
	
}
