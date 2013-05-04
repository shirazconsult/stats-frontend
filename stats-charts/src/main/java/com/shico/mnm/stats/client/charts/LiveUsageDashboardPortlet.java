package com.shico.mnm.stats.client.charts;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Window;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.ChartArea;
import com.google.gwt.visualization.client.visualizations.Table;
import com.google.gwt.visualization.client.visualizations.Visualization;
import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.google.gwt.visualization.client.visualizations.corechart.TextStyle;
import com.shico.mnm.common.chart.BubbleChart;
import com.shico.mnm.common.chart.DashboardPortlet;
import com.shico.mnm.common.event.DataLoadedEvent;
import com.shico.mnm.common.event.EventBus;
import com.shico.mnm.stats.client.StatsChartDataProvider;

public class LiveUsageDashboardPortlet extends DashboardPortlet {
	Table table;
	BubbleChart bubbleChart;
	
	public LiveUsageDashboardPortlet(StatsChartDataProvider dataProvider, double widthRatio, double heightRatio) {
		super(dataProvider, widthRatio, heightRatio);
		setTitle("Channel View");
		
		EventBus.instance().addHandler(DataLoadedEvent.TYPE, this);		
	}

	private BubbleChart getBubbleChart() {
		if(bubbleChart == null){
			Options opts = getChartOptions("Most Watched Programs");
			
			opts.setWidth((int)(Window.getClientWidth()*(widthRatio*0.6)));
			opts.setHeight((int)(Window.getClientHeight()*(heightRatio*0.8)));
				
			opts.setTitle("Most Watched Programs");
				
			opts.setChartArea((ChartArea) getBubbleChartArea());

			AxisOptions vopts = AxisOptions.create();
			AxisOptions hopts = AxisOptions.create();
			TextStyle ts = TextStyle.create();
			ts.setFontSize(8);
			hopts.setTextStyle(ts);
			vopts.setTextStyle(ts);
			opts.set("bubble.textStyle", "{fontSize: 9}");
			vopts.set("title", "Viewers");
			hopts.set("title", "Hours");
			opts.setVAxisOptions(vopts);
			opts.setHAxisOptions(hopts);

			bubbleChart = new BubbleChart(dataProvider.getDataTable(), opts);
		}
		
		return bubbleChart;
	}	

	private Table getTable(){
		if(table == null){
			
			com.google.gwt.visualization.client.visualizations.Table.Options options = com.google.gwt.visualization.client.visualizations.Table.Options.create();
			options.setWidth(String.valueOf((int)(Window.getClientWidth()*0.4)));
			options.setHeight(String.valueOf((int)(Window.getClientHeight()*0.8)));
		
			options.setAllowHtml(true);
			
			table = new Table(dataProvider.getDataTable(), options);
			table.setTitle("Most Watched Programs");
		}
		return table;
	}
	
	@Override
	protected Visualization[] getCharts() {
		return new Visualization[]{getBubbleChart(), getTable()};
	}


	@Override
	protected JavaScriptObject getControls() {
		return getAvgTimeSlider();
	}

	private native JavaScriptObject getAvgTimeSlider()/*-{
		return new $wnd.google.visualization.ControlWrapper({
    		'controlType': 'NumberRangeFilter',
    		'containerId': 'control1',
    		'options': {
      			'filterColumnLabel': 'avgViewTime',
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
