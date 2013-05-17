package com.shico.mnm.stats.client;

import java.util.logging.Logger;

import com.google.gwt.event.shared.SimpleEventBus;
import com.google.web.bindery.event.shared.EventBus;
import com.shico.mnm.stats.client.comp.StatsTabPanel;

public class StatsClientHandle {
	private final static Logger logger = Logger.getLogger("StatsClientHandle");
	
	public final static String STATS_REST_PATH = "rest/stats/";
	public final static String APP_NAME = "mnm-stats";
	
	private static StatsChartDataProvider liveStatsChartDataProvider;
	private static StatsChartDataProvider deadStatsChartDataProvider;
	private static StatsSettingsController settingsController;

	private static EventBus eventBus;
	
	private static StatsTabPanel statsTabPanel;
		
	public static StatsChartDataProvider getLiveStatsChartDataProvider(){
		if(liveStatsChartDataProvider == null){
			liveStatsChartDataProvider = new LiveStatsChartDataProvider();
		}
		return liveStatsChartDataProvider;
	}

	public static StatsChartDataProvider getDeadStatsChartDataProvider(){
		if(deadStatsChartDataProvider == null){
			deadStatsChartDataProvider = new DeadStatsChartDataProvider();
		}
		return deadStatsChartDataProvider;
	}

	public static StatsTabPanel getStatsTabPanel(){
		if(statsTabPanel == null){
			statsTabPanel = new StatsTabPanel(getLiveStatsChartDataProvider(), getDeadStatsChartDataProvider(), getStatsSettingsController());
		}
		return statsTabPanel;
	}
		
	public static EventBus getEventBus() {
		if (eventBus == null){
			eventBus = new SimpleEventBus();
		}
		return eventBus;
	}
	
	public static StatsSettingsController getStatsSettingsController(){
		if(settingsController == null){
			settingsController = new StatsSettingsController();
		}
		return settingsController;
	}

}
