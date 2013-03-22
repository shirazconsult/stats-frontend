package com.shico.mnm.amq.client;

import java.util.Map;

import com.shico.mnm.amq.model.BrokerInfoDS;
import com.shico.mnm.amq.model.MessageListDS;
import com.shico.mnm.amq.model.QueueListDS;
import com.shico.mnm.common.client.SettingsController;
import com.smartgwt.client.data.DataSource;

public interface AmqSettingsController extends SettingsController {
	DataSource getSettingsDS();
	BrokerInfoDS getBrokerInfoDS();
	void setBrokerInfoDS(BrokerInfoDS brokerInfoDS);
	QueueListDS getQueueListDS();
	void setQueueListDS(QueueListDS queueListDS);
	MessageListDS getMessageListDS();
	Object getSetting(String key);
	Map<String, Object> getSettingsMap();
}
