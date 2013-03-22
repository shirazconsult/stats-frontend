package com.shico.mnm.amq.client;

import com.shico.mnm.amq.model.BrokerInfoDS;
import com.shico.mnm.amq.model.MessageListDS;
import com.shico.mnm.amq.model.QueueListDS;
import com.shico.mnm.common.client.SettingsController;
import com.smartgwt.client.data.DataSource;

public interface AmqSettingsController extends SettingsController {
	DataSource getSettings();
	BrokerInfoDS getBrokerInfoDS();
	QueueListDS getQueueListDS();
	MessageListDS getMessageListDS();
	Object getSetting(String key);
}
