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
	
	// CMD is used for other operations other than CRUD like move and copy. 
	// The datasource should still use update operation.
	public final static String CMD = "command";
	public final static String MOVE_CMD = "move";
	public final static String PURGE_CMD = "purge";
	public final static String SELECTOR = "selector";
	// To indicate number of messages an operation should be applied to. -1 means no limit.
	public final static String MAX_MSGS = "maxMsgs";
	// Used to specify the target queue for copy and move opertation 
	public final static String TO_QUEUE = "toQueue";

	public QueueListDS(String datasourceID, String restUrl) {
		super(datasourceID);
		if(restUrl != null && !restUrl.trim().isEmpty()){
			if(restUrl.endsWith("/")){
				setDataURL(restUrl + restPathInfo);
			}else{
				setDataURL(restUrl + "/" + restPathInfo);
			}
		}

		DataSourceTextField nameField = new DataSourceTextField(NAME);
		nameField.setPrimaryKey(true);
		addField(nameField);
		addField(new DataSourceIntegerField(CONSUMERCOUNT));
		addField(new DataSourceIntegerField(PRODUCERCOUNT));
		addField(new DataSourceIntegerField(ENQUEUECOUNT));
		addField(new DataSourceIntegerField(DEQUEUECOUNT));
//		addField(new DataSourceIntegerField(EXPIREDCOUNT));
		addField(new DataSourceIntegerField(QUEUESIZE));
		addField(new DataSourceTextField(BROKERNAME));
		
		// control fields. not visible in views
		addField(new DataSourceTextField(SELECTOR));
		addField(new DataSourceTextField(CMD));
	}
	
	public QueueListDS(String restUrl) {
		this(defaultDataSourceID, restUrl);
	}
}
