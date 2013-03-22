package com.shico.mnm.agg.client;

import com.google.gwt.visualization.client.DataView;
import com.google.gwt.visualization.client.visualizations.Table.Options;
import com.shico.mnm.common.component.TableViewPanel;
import com.shico.mnm.common.event.DataLoadedEvent;
import com.shico.mnm.common.event.EventBus;

public class ProcessingTimeTablePanel extends TableViewPanel {

	public ProcessingTimeTablePanel(AggregatorChartDataProvider dataProvider,
			double widthRatio, double heightRatio) {
		super(dataProvider, widthRatio, heightRatio);

		EventBus.instance().addHandler(DataLoadedEvent.TYPE, this);
		
		initWidget(container);
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

}
