package com.shico.mnm.agg.client;

import com.google.gwt.visualization.client.DataView;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.shico.mnm.common.chart.ColumnChartPanel;
import com.shico.mnm.common.event.DataLoadedEvent;
import com.shico.mnm.common.event.EventBus;

public class LoadViewPanel extends ColumnChartPanel {

	public LoadViewPanel(AggregatorChartDataProvider dataProvider, double widthRatio, double heightRatio) {
		super(dataProvider, widthRatio, heightRatio);

		EventBus.instance().addHandler(DataLoadedEvent.TYPE, this);
		
		initWidget(container);
	}

	@Override
	protected DataView getView() {
		return ((AggregatorChartDataProvider)dataProvider).getLoadView();
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

//	@Override
//	protected ChartPopup asPopupPanel() {
//		chartPopup = new ChartPopup(new LoadViewPanel(dataProvider, widthRatio, heightRatio));
//		return chartPopup;
//	}	
	
}
