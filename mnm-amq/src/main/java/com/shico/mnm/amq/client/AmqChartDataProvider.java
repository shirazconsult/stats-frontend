package com.shico.mnm.amq.client;

import com.google.gwt.visualization.client.DataView;
import com.shico.mnm.common.client.ChartDataProvider;

public interface AmqChartDataProvider extends ChartDataProvider{
	// data index constants
	public final static int memUsageIdx = 0;
	public final static int storeUsageIdx = 2;
	public final static int consCnt = 6;
	public final static int prodCnt = 7; 
	public final static int enqIdx = 8;
	public final static int deqIdx = 9;
	public final static int inflIdx = 11;
	public final static int timeIdx = 18;
	public final static int utimeIdx = 19;
	
	DataView getEnqDeqInflView();
	DataView getAvgEnqTimeView();
	DataView getMemUsageView();
	DataView getDiskUsageView();
	
	int getConsumerCount();
	int getProducerCount();
}
