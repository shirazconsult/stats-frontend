package com.shico.mnm.amq.client;

import com.shico.mnm.amq.model.BrokerInfoDS;
import com.shico.mnm.common.client.SettingsController;

public interface AmqSettingsController extends SettingsController {
	BrokerInfoDS getBrokerInfoDS();
	void setBrokerInfoDS(BrokerInfoDS brokerInfoDS);
}
