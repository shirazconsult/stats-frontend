package com.shico.mnm.amq.model;

import com.shico.mnm.common.client.DefaultRestDS;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;

public class MessageListDS extends DefaultRestDS {
	public final static String MESSAGEID = "messageID";
	public final static String TIMESTAMP = "timestamp";
	public final static String TYPE = "type";
	public final static String DESTINATION = "destination";
	public final static String CORRELATIONID = "correlationID";
	public final static String DELIVERYMODE = "deliveryMode";
	public final static String EXPIRATION = "expiration";
	public final static String PRIORITY = "priority";
	public final static String REDELIVERED = "redelivered";
	public final static String REPLYTO = "replyTo";

	public final static String QUEUE = "queue";
	public final static String BROKER = "brokerName";
	public final static String SELECTOR = "selector";
	// CMD is used for other operations other than CRUD like move and copy. 
	// The datasource should still use update operation.
	public final static String CMD = "command";
	// To indicate number of messages an operation should be applied to. -1 means no limit.
	public final static String MAX_MSGS = "maxMsgs";
	// Used to specify the target queue for copy and move opertation 
	public final static String TO_QUEUE = "toQueue";

	public final static String MOVE_CMD = "move";
	public final static String COPY_CMD = "copy";
	
	public MessageListDS(String datasourceID, String restUrl) {
		super(datasourceID, restUrl);
		
		DataSourceTextField msgIdField = new DataSourceTextField(MESSAGEID);
		msgIdField.setPrimaryKey(true);
		addField(msgIdField);
		DataSourceTextField queueField = new DataSourceTextField(QUEUE);
		queueField.setPrimaryKey(true);
		addField(queueField);
		DataSourceTextField brokerField = new DataSourceTextField(BROKER);
		brokerField.setPrimaryKey(true);
		addField(brokerField);

		addField(new DataSourceTextField(TIMESTAMP));
		addField(new DataSourceTextField(TYPE));
		addField(new DataSourceTextField(DESTINATION));
		addField(new DataSourceIntegerField(CORRELATIONID));
		addField(new DataSourceTextField(DELIVERYMODE));
		addField(new DataSourceIntegerField(EXPIRATION));
		addField(new DataSourceIntegerField(PRIORITY));
		addField(new DataSourceBooleanField(REDELIVERED));
		addField(new DataSourceTextField(REPLYTO));
		
		// control fields. not visible in views
		addField(new DataSourceTextField(SELECTOR));
		addField(new DataSourceTextField(CMD));
	}
}
