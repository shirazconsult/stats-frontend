package com.shico.mnm.common.client;

import java.util.Map;

import com.smartgwt.client.data.DataSource;


public interface SettingsController {
	DataSource getSettingsDS();
	Object getSetting(String key);
	Map<String, Object> getSettingsMap();
	void setSettingsMapFromLocalStorage();
	boolean useLocalStorage();
}
