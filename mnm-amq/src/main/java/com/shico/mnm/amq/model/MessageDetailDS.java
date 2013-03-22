package com.shico.mnm.amq.model;

import com.smartgwt.client.data.fields.DataSourceTextField;


public class MessageDetailDS extends MessageListDS {
	public final static String BODY = "body";

	public MessageDetailDS(String datasourceID, String restUrl) {
		super(datasourceID, restUrl);
		
		addField(new DataSourceTextField(BODY));
	}
}
