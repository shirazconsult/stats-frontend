package com.shico.mnm.common.event;

import java.util.Map;

import com.google.gwt.event.shared.GwtEvent;

public class DataLoadedEvent extends GwtEvent<DataLoadedEventHandler> {
	public static final GwtEvent.Type<DataLoadedEventHandler> TYPE = new GwtEvent.Type<DataLoadedEventHandler>();

	public final static String ERROR_KEY = "_error";
	public final static String EXCEPTION_KEY = "_exception";
	public final static String SUBTYPE_KEY = "_subtype";
	public final static String DATA_KEY = "_data";
	
	@Override
	public GwtEvent.Type<DataLoadedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DataLoadedEventHandler handler) {
		handler.onDataLoaded(this);
	}
	
	public Map<String, Object> info; 
	public DataEventType eventType;
	public Class source = DataLoadedEvent.class;  // default value to avoid null pointer exceptions
	
	public DataLoadedEvent(DataEventType type) {
		this.eventType = type;		
	}

	public DataLoadedEvent(DataEventType type, Class source) {
		this.eventType = type;
		this.source = source;
	}

	public DataLoadedEvent(DataEventType type, Map<String, Object> info) {
		this.eventType = type;		
		this.info = info;
	}


	public DataLoadedEvent(DataEventType type, Map<String, Object> info, Class source) {
		this.eventType = type;		
		this.info = info;
		this.source = source;
	}

	public String getError(){
		if(info != null && info.containsKey(ERROR_KEY)){
			return (String)info.get(ERROR_KEY);
		}
		return null;
	}
	
	public Object getData(){
		if(info != null){
			return info.get(DATA_KEY);
		}
		return null;
	}
	
	public DataEventType getMsgActionType(){
		if(info != null && info.get(SUBTYPE_KEY) != null &&
				DataEventType.MSG_ACTION_EVENTS.contains(info.get(SUBTYPE_KEY))){
			return (DataEventType)info.get(SUBTYPE_KEY);
		}
		return DataEventType.UNKNOWN;
	}

	public DataEventType getFailedEventSubType(){
		if(info != null && info.get(SUBTYPE_KEY) != null &&
				DataEventType.AMQ_FAILED_EVENTS.contains(info.get(SUBTYPE_KEY))){
			return (DataEventType)info.get(SUBTYPE_KEY);
		}
		return DataEventType.UNKNOWN;
	}

	public Exception getException(){
		if(info != null && info.get(EXCEPTION_KEY) != null){
			return ((Exception)info.get(EXCEPTION_KEY));
		}
		return null;
	}
}
