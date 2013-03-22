package com.shico.statistics.client.component;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.shico.mnm.agg.client.AggregatorDataClient;
import com.shico.mnm.agg.client.HeapMemChartPanel;
import com.shico.mnm.agg.client.LoadViewPanel;
import com.shico.mnm.agg.client.NonHeapMemChartPanel;
import com.shico.mnm.agg.client.ProcessingTimeTablePanel;
import com.shico.mnm.common.client.ChartDataProvider;

public class AggMonitorTabPanelDeprecated extends TabPanel {
	
	AggregatorDataClient dataClient;

	VerticalPanel vp = new VerticalPanel();

	public AggMonitorTabPanelDeprecated(ChartDataProvider chartDataProvider) {
		super();
		
		dataClient = (AggregatorDataClient)chartDataProvider;

		setup();
	}

	void setup(){
		setAnimationEnabled(true);
		addStyleName("shico-Level2TabBarItem");
		
		Grid grid = new Grid(2,2);
		grid.setStyleName("shico-Grid");
		grid.setSize("100%", "100%");
		
		grid.setWidget(0, 0, new HeapMemChartPanel(dataClient, .45, 0.45));
		grid.setWidget(0, 1, new NonHeapMemChartPanel(dataClient, 0.45, 0.45));
		grid.setWidget(1, 0, new LoadViewPanel(dataClient, 0.45, 0.45));
		grid.setWidget(1, 1, new ProcessingTimeTablePanel(dataClient, 0.45, 0.45));
		
		add(grid, "Monitor", false);
		
		vp.add(grid);

		add(vp, "Monitor", false);

	}
	
}
