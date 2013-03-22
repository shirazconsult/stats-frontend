package com.shico.mnm.agg.client;

import com.google.gwt.visualization.client.DataView;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.shico.mnm.common.chart.LineChartPanel;
import com.shico.mnm.common.chart.LineChartPopup;
import com.shico.mnm.common.event.DataLoadedEvent;
import com.shico.mnm.common.event.EventBus;

public class HeapMemChartPanel extends LineChartPanel {

	public HeapMemChartPanel(AggregatorChartDataProvider dataProvider, double widthRatio, double heightRatio) {
		super(dataProvider, widthRatio, heightRatio);

		EventBus.instance().addHandler(DataLoadedEvent.TYPE, this);
		
		initWidget(container);
	}

	@Override
	protected DataView getView() {
		return ((AggregatorChartDataProvider)dataProvider).getHeapMemView();
	}

	@Override
	protected Options getOptions() {
		return getOptions("Heap Memory Usage", "Time in minutes", 
				"Memory in MB");
	}	
	
	@Override
	protected LineChartPopup asPopupPanel() {
		chartPopup = new LineChartPopup(new HeapMemChartPanel((AggregatorChartDataProvider)dataProvider, widthRatio, heightRatio));
		return chartPopup;
	}	
	
	@Override
	public void onDataLoaded(DataLoadedEvent event) {
		switch (event.eventType) {
		case AGGREGATOR_DATA_LOADED_EVENT:
			draw();
			break;
		}
	}

}
