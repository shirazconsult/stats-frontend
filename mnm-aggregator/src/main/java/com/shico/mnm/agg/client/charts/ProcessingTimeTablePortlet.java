package com.shico.mnm.agg.client.charts;

import com.google.gwt.visualization.client.DataView;
import com.google.gwt.visualization.client.visualizations.Table.Options;
import com.shico.mnm.agg.client.AggChartDataProvider;
import com.shico.mnm.common.chart.TableViewPortlet;
import com.shico.mnm.common.event.DataLoadedEvent;
import com.shico.mnm.common.event.EventBus;
import com.smartgwt.client.widgets.events.ClickEvent;

public class ProcessingTimeTablePortlet extends TableViewPortlet {

	public ProcessingTimeTablePortlet(AggChartDataProvider dataProvider,
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
		return ((AggChartDataProvider)dataProvider).getProcessingTimeView();
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
