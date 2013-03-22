package com.shico.mnm.amq.client.charts;

import com.google.gwt.visualization.client.DataView;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.shico.mnm.amq.client.AmqChartDataProvider;
import com.shico.mnm.common.chart.LineChartPanel;
import com.shico.mnm.common.chart.LineChartPopup;
import com.shico.mnm.common.event.DataLoadedEvent;
import com.shico.mnm.common.event.EventBus;

public class DiskUsageChartPanel extends LineChartPanel {

	public DiskUsageChartPanel(AmqChartDataProvider dataProvider, double widthRatio, double heightRatio) {
		super(dataProvider, widthRatio, heightRatio);

		EventBus.instance().addHandler(DataLoadedEvent.TYPE, this);
		
		initWidget(container);
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
	protected LineChartPopup asPopupPanel() {
		chartPopup = new LineChartPopup(new DiskUsageChartPanel((AmqChartDataProvider)dataProvider, widthRatio, heightRatio));
		return chartPopup;
	}	
	
	@Override
	public void onDataLoaded(DataLoadedEvent event) {
		switch (event.eventType) {
		case BROKER_DATA_LOADED_EVENT:
			draw();
			break;
		}
	}

}
