package com.shico.mnm.amq.model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.shico.mnm.common.model.AbstractClientDS;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.FieldType;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;

public class Message extends AbstractClientDS {
	static final DateTimeFormat fmt = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss:SSS v");

	MessageColumns colNames;
	List<Object> msg;
	
	public Message(List<Object> msgAttributes, MessageColumns columns) {
		this.msg = msgAttributes;
		this.colNames = columns;		
	}

	public Message(Map<String, Object> msg, MessageColumns columns) {
		this.colNames = columns;		
		setFields(msg);
		setData(msg);
	}

	private DataSourceField[] fields;
	private void setFields(Map<String, Object> msg2) {
		this.fields = new DataSourceField[msg2.size()];
		int i=0;
		for (Entry<String, Object> entry : msg2.entrySet()) {
			String key = entry.getKey();
			if(entry.getValue() instanceof Double){
				fields[i] = new DataSourceField(key, FieldType.FLOAT);
			}else if(entry.getValue() instanceof Boolean){
				fields[i] = new DataSourceField(key, FieldType.BOOLEAN);
			}else{
				fields[i] = new DataSourceField(key, FieldType.TEXT);
			}
			i++;
		}
	}

	private String formatTimestamp(Double timestamp) {
		Date date = new Date(Math.round(timestamp));
		return fmt.format(date);
	}

	public String getId() {
		if(backingBean != null){
			return (String) backingBean.get(colNames.getId());
		}
		return (String) msg.get(MessageColumns.mIdIdx);
	}

	public String getTimestamp() {
		Double ts = (Double) msg.get(MessageColumns.mTimestampIdx);
		Date date = new Date(Math.round(ts));
		return fmt.format(date);
	}

	public String getType() {
		return (String) msg.get(MessageColumns.mTypeIdx);
	}

	public String getDesination() {
		return (String) msg.get(MessageColumns.mDestIdx);
	}

	public String getCorrelationId() {
		return (String) msg.get(MessageColumns.mCorIdIdx);
	}

	public String getDeliveryMode() {
		return (String)msg.get(MessageColumns.mDelModeIdx);
	}

	public String getExpiration() {
		Double ts = (Double) msg.get(MessageColumns.mExpIdx);
		Date date = new Date(Math.round(ts));
		return fmt.format(date);
	}

	public int getPriority() {
		return ((Double) msg.get(MessageColumns.mPrioIdx)).intValue();
	}

	public boolean getRedelivered() {
		return (Boolean) msg.get(MessageColumns.mRedelIdx);
	}

	public String getReplyTo() {
		return (String) msg.get(MessageColumns.mReplyToIdx);
	}

	@Override
	public DataSourceField[] getFields() {
		return this.fields;		
	}

	@Override
	protected String getDataSourceID() {
		return getPrimaryKeyValue()+"_DS";
	}

	@Override
	protected String getPrimaryKeyValue() {
		String id = (String)backingBean.get("JMSMessageID");
		String pid = id.replace(":", "_").replace(".", "_").replace("-", "_");
		return pid;
	}
	
	public void cleanup(){
		if(msg != null){
			msg.clear();
			msg = null;
		}
		if(fields != null){
			fields = null;
		}
		super.destroy();
	}
}
