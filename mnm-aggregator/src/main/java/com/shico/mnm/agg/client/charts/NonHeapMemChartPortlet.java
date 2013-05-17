package com.shico.mnm.agg.client.charts;

import com.google.gwt.visualization.client.DataView;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.shico.mnm.agg.client.AggChartDataProvider;
import com.shico.mnm.common.chart.LineChartPortlet;
import com.shico.mnm.common.event.DataLoadedEvent;
import com.shico.mnm.common.event.EventBus;
import com.smartgwt.client.widgets.events.ClickEvent;

public class NonHeapMemChartPortlet extends LineChartPortlet {

	public NonHeapMemChartPortlet(AggChartDataProvider dataProvider, double widthRatio, double heightRatio) {
		super(dataProvider, widthRatio, heightRatio);
		setTitle("PermGen Space");
		
		EventBus.instance().addHandler(DataLoadedEvent.TYPE, this);
	}

	@Override
	protected DataView getView() {
		return ((AggChartDataProvider)dataProvider).getNonHeapMemView();
	}

	@Override
	protected Options getOptions() {
		return getOptions("Non-Heap Memory Usage", "Time in minutes", 
				"Memory in MB");
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
	protected void handleRefresh(ClickEvent event) {
		draw();
	}

	@Override
	protected void handleSettings(ClickEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void handleHelp(ClickEvent event) {
		// TODO Auto-generated method stub
		
	}

}
