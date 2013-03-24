package com.shico.mnm.agg.client;

import com.google.gwt.visualization.client.DataView;
import com.shico.mnm.common.client.ChartDataProvider;


public interface AggChartDataProvider extends ChartDataProvider{
	// data index constants
	public final static int numOfCompletedExchangesIdx = 0;
	public final static int numOfFailedExchangesIdx = 1;
	public final static int numOfTotalExchangesIdx = 2;

	public final static int totalProcessingTimeIdx = 3;
	public final static int lastProcessingTimeIdx = 4;
	public final static int maxProcessingTimeIdx = 5;
	public final static int minProcessingTimeIdx = 6;
	public final static int meanProcessingTimeIdx = 7;

	public final static int lastMinuteLoadIdx = 8;
	public final static int last5MinuteLoadIdx = 9;
	public final static int last15MinuteLoadIdx = 10;
	
	public final static int vmHeapMemIdx = 11;
	public final static int vmNonHeapMemIdx = 12;
	public final static int vmThreadCountIdx = 13;
	public final static int vmSysLoadAvgIdx = 14;

	public final static int timeIdx = 15;
	public final static int utimeIdx = 16;
	
	DataView getProcessingTimeView();
	DataView getLoadView();
	
	DataView getHeapMemView();
	DataView getNonHeapMemView();
	DataView getSysLoadAvgView();
	DataView getLiveThreadView();
}
