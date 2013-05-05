package com.shico.mnm.common.event;

import java.util.EnumSet;

public enum DataEventType {
	// Aggregator statistics data events
	AGGREGATOR_METADATA_LOADED_EVENT,
	AGGREGATOR_DATA_LOADED_EVENT,
	FAILED_AGGREGATOR_METADATA_LOADED_EVENT,
	FAILED_AGGREGATOR_DATA_LOADED_EVENT,

	// Amq statistics data events
	BROKER_METADATA_LOADED_EVENT,
	BROKER_DATA_LOADED_EVENT,
	
	// Amq admin data events
	BROKER_INFO_LOADED_EVENT,
	QUEUELIST_INFO_LOADED_EVENT,
	QUEUELIST_METADATA_LOADED_EVENT,
	MSGLIST_LOADED_EVENT,
	
	// Stats data events
	STATS_METADATA_LOADED_EVENT,
	STATS_DATA_LOADED_EVENT,
	FAILED_STATS_METADATA_LOADED_EVENT,
	FAILED_STATS_DATA_LOADED_EVENT,
	
	// Message events
	MSG_EVENT,
	
	// Settings events
	SETTINGS_LOADED_EVENT,
	SETTINGS_CHANGED_EVENT,
	AMQ_SETTINGS_LOADED_EVENT,
	AMQ_ADMIN_SETTINGS_CHANGED_EVENT,
	AMQ_CHART_SETTINGS_CHANGED_EVENT,
	AGG_SETTINGS_LOADED_EVENT,
	AGG_ADMIN_SETTINGS_CHANGED_EVENT,
	AGG_CHART_SETTINGS_CHANGED_EVENT,
	STATS_SETTINGS_LOADED_EVENT,
	STATS_ADMIN_SETTINGS_CHANGED_EVENT,
	STATS_CHART_SETTINGS_CHANGED_EVENT,
	
	// Failed event type/subtypes
	FAILED_EVENT,
	FAILED_BROKER_METADATA_LOADED_EVENT,
	FAILED_BROKER_INFO_LOADED_EVENT, 
	FAILED_QUEUELIST_INFO_LOADED_EVENT,
	FAILED_MSGLIST_LOADED_EVENT,
	FAILED_MSG_ACTION_EVENT,
	FAILED_BROKER_DATA_LOADED_EVENT,
	
	// MSG_EVENT sub-types
	MOVE, BULK_MOVE, COPY, DELETE, LOAD, SELECT, PURGE, DEL_Q, ADD_Q,
	UNKNOWN;
	
	public final static EnumSet<DataEventType> AGG_EVENT_SUBTYPE = EnumSet.range(AGGREGATOR_METADATA_LOADED_EVENT, FAILED_AGGREGATOR_DATA_LOADED_EVENT);
	public final static EnumSet<DataEventType> AMQ_EVENT_SUBTYPE = EnumSet.range(BROKER_METADATA_LOADED_EVENT, MSG_EVENT);
	public final static EnumSet<DataEventType> MSG_ACTION_EVENTS = EnumSet.range(MOVE, ADD_Q);
	public final static EnumSet<DataEventType> AMQ_FAILED_EVENTS = EnumSet.range(FAILED_BROKER_METADATA_LOADED_EVENT, FAILED_BROKER_DATA_LOADED_EVENT);
	
}
