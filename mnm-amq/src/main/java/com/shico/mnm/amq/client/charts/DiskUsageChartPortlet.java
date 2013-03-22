package com.shico.mnm.amq.client.charts;

import com.google.gwt.visualization.client.DataView;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.shico.mnm.amq.client.AmqChartDataProvider;
import com.shico.mnm.common.chart.LineChartPortlet;
import com.shico.mnm.common.event.DataLoadedEvent;
import com.shico.mnm.common.event.EventBus;

public class DiskUsageChartPortlet extends LineChartPortlet {

	public DiskUsageChartPortlet(AmqChartDataProvider dataProvider, double widthRatio, double heightRatio) {
		super(dataProvider, widthRatio, heightRatio);
		setTitle("Disk Usage");
		
		EventBus.instance().addHandler(DataLoadedEvent.TYPE, this);		
	}

	@Override
	protected DataView getView() {
		return ((AmqChartDataProvider)dataProvider).getDiskUsageView();
	}

	@Override
	protected Options getOptions() {
		return getOptions("Disk Usage", "Time in minutes", "Disk space in MB");
	}	
		
	@Override
	public void onDataLoaded(DataLoadedEvent event) {
		switch (event.eventType) {
		case BROKER_DATA_LOADED_EVENT:
			draw();
			break;
		}
	}

	@Override
	protected void handleRefresh() {
		draw();
	}

	@Override
	protected void handleSettings() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void handleHelp() {
		// TODO Auto-generated method stub
		
	}

}
