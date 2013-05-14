package com.shico.mnm.stats.client;

import com.google.gwt.visualization.client.AbstractDataTable;
import com.shico.mnm.common.client.ChartDataProvider;

public interface StatsChartDataProvider extends ChartDataProvider{
	public static String[] viewColumns = {
		"type", "name", "title", "sum", "minDuration", "maxDuration", "totalDuration", "from", "to"};
	
	// events
	//		"liveusage", "widgetshow", "vodusagemovie", "vodusagetrailer", "dvrusage", 
//		"webtvlogin", "startoverusage", "timeshiftusage", "movierent", "shoploaded", 
//		"adadtion"};
	
	
	// data index constants
	public final static int typeIdx = 0; 
	public final static int nameIdx = 1; 
	public final static int titleIdx = 2;
	public final static int sumIdx = 3;
	public final static int minDurationIdx = 4; 
	public final static int maxDurationIdx = 5; 
	public final static int totalDurationIdx = 6;
	public final static int fromIdx = 7;
	public final static int toIdx = 8;
	
	AbstractDataTable getLiveUsagePieChartView();
	AbstractDataTable getLiveUsageColumnChartView();
	AbstractDataTable getLiveUsageBubbleChartView();
	AbstractDataTable getMostPopularMovieRentals();
	AbstractDataTable getMostPopularWidgetsPieChartView();
	AbstractDataTable getLiveUsageTableView();
}
