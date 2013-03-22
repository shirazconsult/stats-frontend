package com.shico.mnm.common.model;

public class MonitorSettingsEventObject {
	public static String EVENT_NAME = "MonitorSettingsEvent";
	
	String address;
	Integer refreshInterval;
	Integer viewSize;
	public MonitorSettingsEventObject(String address, Integer refreshInterval,
			Integer viewSize) {
		super();
		this.address = address;
		this.refreshInterval = refreshInterval;
		this.viewSize = viewSize;
	}
	public String getAddress() {
		return address;
	}
	public Integer getRefreshInterval() {
		return refreshInterval;
	}
	public Integer getViewSize() {
		return viewSize;
	}
}
