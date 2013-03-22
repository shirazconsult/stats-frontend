package com.shico.mnm.common.event;

import com.google.gwt.event.shared.HandlerManager;

public class EventBus {
	private EventBus() {}
	private static final HandlerManager instance = new HandlerManager(null);
	
	public static HandlerManager instance(){
		return instance;
	}
}
