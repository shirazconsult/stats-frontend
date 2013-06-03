package com.shico.mnm.stats.client;

import com.shico.mnm.common.client.ChartDataProvider;

public interface StatsChartDataProvider extends ChartDataProvider{
	public static String[] viewColumns = {
		"type", "name", "title", "viewers", "duration", "from", "to"};
	public static String[] topViewColumns = {
		"type", "name", "title", "viewers", "duration", "time"};
	
	public static final String STATS_EVENT_TYPE = "stats_event_type";
	public static String[] statsEvents = {
		"adAdtion", "DvrUsage", "LiveUsage", "TVProgram", "movieRent", "SelfCareSUBSCRIBE", "shopLoaded", 
		"STARTOVERUsage", "TIMESHIFTUsage", "VodUsageMOVIE", "VodUsageTRAILER", 
		"WebTVLogin", "widgetShow" 
		};
	// childStatEvents don't correspond to any stats-events in the backend, but denote the event at hand more specificaly
	public static String[] subStatEvents = {
		"LiveUsage.Program"
	};
	public final static String PROGRAM_EVENT = "LiveUsage.Program";
	
	// data index constants
	public final static int typeIdx = 0; 
	public final static int nameIdx = 1; 
	public final static int titleIdx = 2;
	public final static int viewersIdx = 3;
	public final static int durationIdx = 4;
	public final static int fromIdx = 5;
	public final static int timeIdx = 5;
	public final static int toIdx = 6;
	
	void getRows(final String type, final long from, final long to);
	void getRows(final String type, final String from, final String to);
	void getRows(final String type, final long from, final long to, final String options);
	void getRows(final String type, final String from, final String to, final String options);
	void getRowsInBatch(final String type, final String from, final String to, String options);
	
//	AbstractDataTable getLiveUsagePieChartView();
//	AbstractDataTable getLiveUsageColumnChartView();
//	AbstractDataTable getLiveUsageBubbleChartView();
//	AbstractDataTable getMostPopularMovieRentals();
//	AbstractDataTable getMostPopularWidgetsPieChartView();
//	AbstractDataTable getLiveUsageTableView();
}
