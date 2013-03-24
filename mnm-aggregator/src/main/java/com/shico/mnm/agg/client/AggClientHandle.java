package com.shico.mnm.agg.client;

import java.util.logging.Logger;

import com.google.gwt.event.shared.SimpleEventBus;
import com.google.web.bindery.event.shared.EventBus;
import com.shico.mnm.agg.components.AggTabPanel;

public class AggClientHandle {
	private final static Logger logger = Logger.getLogger("AggClientHandle");
	
	public final static String AGGREGATOR_REST_PATH = "rest/agg/";
	public final static String APP_NAME = "mnm-agg";
	
	private static AggChartDataProvider aggChartDataProvider;
	private static AggSettingsController settingsController;

	private static EventBus eventBus;
	
	private static AggTabPanel aggTabPanel;
		
	public static AggChartDataProvider getChartDataProvider(){
		if(aggChartDataProvider == null){
			aggChartDataProvider = new AggChartDataProviderImpl();
		}
		return aggChartDataProvider;
	}
	
	public static AggTabPanel getAggTabPanel(){
		if(aggTabPanel == null){
			aggTabPanel = new AggTabPanel(getChartDataProvider(), getAggSettingsController());
		}
		return aggTabPanel;
	}
		
	public static EventBus getEventBus() {
		if (eventBus == null){
			eventBus = new SimpleEventBus();
		}
		return eventBus;
	}
	
	public static AggSettingsController getAggSettingsController(){
		if(settingsController == null){
			settingsController = new AggSettingsController();
		}
		return settingsController;
	}

}
