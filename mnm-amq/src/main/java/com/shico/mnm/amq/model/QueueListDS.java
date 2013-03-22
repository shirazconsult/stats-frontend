package com.shico.mnm.amq.model;

import com.shico.mnm.common.client.DefaultRestDS;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;

public class QueueListDS extends DefaultRestDS {
	public final static String restPathInfo = "queue";
	public final static String defaultDataSourceID = "QueueListDS";
	
	public final static String NAME = "name";
	public final static String CONSUMERCOUNT = "consumerCount";
	public final static String PRODUCERCOUNT = "producerCount";
	public final static String ENQUEUECOUNT = "enqueueCount";
	public final static String DEQUEUECOUNT = "dequeueCount";
	public final static String EXPIREDCOUNT = "expiredCount";
	public final static String QUEUESIZE = "queueSize";
	public final static String BROKERNAME = "brokerName";
	
	public QueueListDS(String datasourceID, String restUrl) {
		super(datasourceID);
		if(restUrl.endsWith("/")){
			setDataURL(restUrl + restPathInfo);
		}else{
			setDataURL(restUrl + "/" + restPathInfo);
		}

		DataSourceTextField pk = new DataSourceTextField(NAME);
		pk.setPrimaryKey(true);
		addField(pk);
		addField(new DataSourceIntegerField(CONSUMERCOUNT));
		addField(new DataSourceIntegerField(PRODUCERCOUNT));
		addField(new DataSourceIntegerField(ENQUEUECOUNT));
		addField(new DataSourceIntegerField(DEQUEUECOUNT));
		addField(new DataSourceIntegerField(EXPIREDCOUNT));
		addField(new DataSourceIntegerField(QUEUESIZE));
		addField(new DataSourceTextField(BROKERNAME));
	}
	
	public QueueListDS(String restUrl) {
		this(defaultDataSourceID, restUrl);
	}
}
