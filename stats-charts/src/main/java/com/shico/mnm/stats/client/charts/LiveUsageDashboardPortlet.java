package com.shico.mnm.stats.client.charts;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.visualizations.Table;
import com.shico.mnm.common.chart.BubbleChart;
import com.shico.mnm.common.chart.DashboardPortlet;
import com.shico.mnm.common.event.DataLoadedEvent;
import com.shico.mnm.common.event.EventBus;
import com.shico.mnm.stats.client.StatsChartDataProvider;
import com.smartgwt.client.widgets.HTMLFlow;

public class LiveUsageDashboardPortlet extends DashboardPortlet {
	Table table;
	BubbleChart bubbleChart;
	
	public LiveUsageDashboardPortlet(StatsChartDataProvider dataProvider, double widthRatio, double heightRatio) {
		super(dataProvider, widthRatio, heightRatio);
		setTitle("Channel View");
				
		EventBus.instance().addHandler(DataLoadedEvent.TYPE, this);		
	}

	private native JavaScriptObject getBubbleChartWrapper(String containerId, int height, int width) /*-{
		return new $wnd.google.visualization.ChartWrapper({
          'chartType': 'BubbleChart',
          'containerId': containerId,
          'options': {
            'width': width,
            'height': height,
            'title': 'Most Watched Programs',
            'legend': 'right',
            'bubble': {textStyle: {fontSize: 9}},
            'hAxis': {title: 'Watched Hours'},
          	'vAxis': {title: 'Viewers'},
          }
        });
	}-*/;
	
	private native JavaScriptObject getTableChartWrapper(String containerId, int height, int width) /*-{
		return new $wnd.google.visualization.ChartWrapper({
          'chartType': 'Table',
          'containerId': containerId,
          'options': {
            'width': width,
            'height': height,
            'title': 'Most Watched Programs',
          }
        });        
	}-*/;
	
	@Override
	protected JsArray<JavaScriptObject> getCharts() {
		return JsArrayUtils.readOnlyJsArray(new JavaScriptObject[]{
				getBubbleChartWrapper("bchart_div", 
						(int)(Window.getClientWidth()*(widthRatio*0.6)), (int)(int)(Window.getClientHeight()*(widthRatio*0.8))), 
				getTableChartWrapper("chart_div", 
						(int)(Window.getClientWidth()*(widthRatio*0.4)), (int)(int)(Window.getClientHeight()*(widthRatio*0.8)))
		});
	}

	@Override
	protected JavaScriptObject getControls() {
		return getAvgTimeSlider();
	}

	private native JavaScriptObject getAvgTimeSlider()/*-{
		return new $wnd.google.visualization.ControlWrapper({
    		'controlType': 'NumberRangeFilter',
    		'containerId': 'control_div',
    		'options': {
      			'filterColumnLabel': 'Popularity metric',
    			'ui': {'labelStacking': 'vertical'}
    		}
  		});
	}-*/;

	@Override
	protected String getPanelTitle() {
		return "Most Watched Programs";
	}


	@Override
	protected AbstractDataTable getData() {
		return ((StatsChartDataProvider)dataProvider).getLiveUsageBubbleChartView();
	}
	
	@Override
	public void onDataLoaded(DataLoadedEvent event) {
		switch (event.eventType) {
		case STATS_DATA_LOADED_EVENT:
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
	
	private native JavaScriptObject getBubbleChartArea()/*-{
		return {left:50, width:"60%"}
	}-*/;

}
