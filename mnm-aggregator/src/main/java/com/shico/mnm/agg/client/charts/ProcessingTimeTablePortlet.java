package com.shico.mnm.agg.client.charts;

import com.google.gwt.visualization.client.DataView;
import com.google.gwt.visualization.client.visualizations.Table.Options;
import com.shico.mnm.agg.client.AggregatorChartDataProvider;
import com.shico.mnm.common.chart.TableViewPortlet;
import com.shico.mnm.common.event.DataLoadedEvent;
import com.shico.mnm.common.event.EventBus;

public class ProcessingTimeTablePortlet extends TableViewPortlet {

	public ProcessingTimeTablePortlet(AggregatorChartDataProvider dataProvider,
			double widthRatio, double heightRatio) {
		super(dataProvider, widthRatio, heightRatio);
		setTitle("Exchange Processing Metrics");
		
		EventBus.instance().addHandler(DataLoadedEvent.TYPE, this);
	}

	@Override
	protected Options getOptions() {
		return getOptions(getPanelTitle());
	}

	@Override
	protected DataView getView() {
		return ((AggregatorChartDataProvider)dataProvider).getProcessingTimeView();
	}

	@Override
	protected String getPanelTitle() {
		return "Exchange Processing Metrics";
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
