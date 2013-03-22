package com.shico.mnm.amq.client;

import com.shico.mnm.common.event.DataEventType;
import com.shico.mnm.common.event.DataLoadedEvent;
import com.shico.mnm.common.event.DataLoadedEventHandler;
import com.shico.mnm.common.event.EventBus;
import com.smartgwt.client.util.SC;

public class AmqErrorHandler implements DataLoadedEventHandler {

	public AmqErrorHandler() {
		super();

		EventBus.instance().addHandler(DataLoadedEvent.TYPE, this); 
	}

	@Override
	public void onDataLoaded(DataLoadedEvent event) {
		if(event.eventType == DataEventType.FAILED_EVENT){
			switch(event.getFailedEventSubType()){
			case FAILED_BROKER_DATA_LOADED_EVENT:
			case FAILED_BROKER_METADATA_LOADED_EVENT:
			case FAILED_BROKER_INFO_LOADED_EVENT:
			case FAILED_MSGLIST_LOADED_EVENT:
			case FAILED_QUEUELIST_INFO_LOADED_EVENT:				
				SC.clearPrompt();
				SC.warn(event.getError());
			}
		}
	}

	public void showError(String errorMsg){
		SC.clearPrompt();
		SC.warn(errorMsg);
	}
}
