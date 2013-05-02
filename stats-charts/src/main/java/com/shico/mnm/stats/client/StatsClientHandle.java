package com.shico.mnm.stats.client;

import java.util.logging.Logger;

import com.google.gwt.event.shared.SimpleEventBus;
import com.google.web.bindery.event.shared.EventBus;
import com.shico.mnm.stats.client.comp.StatsTabPanel;

public class StatsClientHandle {
	private final static Logger logger = Logger.getLogger("StatsClientHandle");
	
	public final static String Stats_REST_PATH = "rest/stats/";
	public final static String APP_NAME = "mnm-stats";
	
	private static StatsChartDataProvider statsChartDataProvider;
//	private static AggSettingsController settingsController;

	private static EventBus eventBus;
	
	private static StatsTabPanel statsTabPanel;
		
	public static StatsChartDataProvider getChartDataProvider(){
		if(statsChartDataProvider == null){
			statsChartDataProvider = new StatsChartDataProviderImpl();
		}
		return statsChartDataProvider;
	}
	
	public static StatsTabPanel getStatsTabPanel(){
		if(statsTabPanel == null){
			statsTabPanel = new StatsTabPanel(getChartDataProvider()); //, getAggSettingsController());
		}
		return statsTabPanel;
	}
		
	public static EventBus getEventBus() {
		if (eventBus == null){
			eventBus = new SimpleEventBus();
		}
		return eventBus;
	}
	
//	public static AggSettingsController getAggSettingsController(){
//		if(settingsController == null){
//			settingsController = new AggSettingsController();
//		}
//		return settingsController;
//	}

}
