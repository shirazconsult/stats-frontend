package com.shico.mnm.agg.client.charts;

import com.google.gwt.visualization.client.DataView;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.shico.mnm.agg.client.AggChartDataProvider;
import com.shico.mnm.common.chart.ColumnChartPortlet;
import com.shico.mnm.common.event.DataLoadedEvent;
import com.shico.mnm.common.event.EventBus;

public class LoadViewPortlet extends ColumnChartPortlet {

	public LoadViewPortlet(AggChartDataProvider dataProvider, double widthRatio, double heightRatio) {
		super(dataProvider, widthRatio, heightRatio);
		setTitle("Load View");
		
		EventBus.instance().addHandler(DataLoadedEvent.TYPE, this);		
	}

	@Override
	protected DataView getView() {
		return ((AggChartDataProvider)dataProvider).getLoadView();
	}

	@Override
	protected Options getOptions() {
		return getChartOptions("Load Metrics");
	}	
	
	@Override
	public void onDataLoaded(DataLoadedEvent event) {
		switch (event.eventType) {
		case AGGREGATOR_DATA_LOADED_EVENT:
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
