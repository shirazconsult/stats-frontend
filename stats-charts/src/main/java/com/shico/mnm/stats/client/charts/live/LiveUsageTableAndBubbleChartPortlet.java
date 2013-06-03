package com.shico.mnm.stats.client.charts.live;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.Selection;
import com.google.gwt.visualization.client.events.SelectHandler;
import com.google.gwt.visualization.client.visualizations.Table;
import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.google.gwt.visualization.client.visualizations.corechart.TextStyle;
import com.shico.mnm.common.chart.BubbleChart;
import com.shico.mnm.common.chart.ChartWrapper;
import com.shico.mnm.common.chart.MultipleChartPortlet;
import com.shico.mnm.common.event.DataLoadedEvent;
import com.shico.mnm.common.event.EventBus;
import com.shico.mnm.stats.client.LiveStatsChartDataProvider;
import com.shico.mnm.stats.client.StatsChartDataProvider;
import com.smartgwt.client.widgets.events.ClickEvent;

public class LiveUsageTableAndBubbleChartPortlet extends MultipleChartPortlet {

	protected StatsChartDataProvider dataProvider;
	protected BubbleChart bubbleChart;
	protected Table table;
	
	public LiveUsageTableAndBubbleChartPortlet(StatsChartDataProvider dataProvider, double widthRatio, double heightRatio) {
		super(widthRatio, heightRatio);
		this.dataProvider = dataProvider;
		
		bubbleChart = new BubbleChart();
		table = new Table();
		
		bubbleChart.addSelectHandler(new SelectHandler() {			
			@Override
			public void onSelect(SelectEvent event) {
				JsArray<Selection> selections = bubbleChart.getSelections();
				try{
					table.setSelections(selections);
				}catch(Exception e){
					// ignore
				}
			}
		});
		setTitle("Most Watched Programs");
		
		EventBus.instance().addHandler(DataLoadedEvent.TYPE, this);		
	}
	
	@Override
	protected ChartWrapper[] getCharts() {
		AbstractDataTable data = getDataTable();
		return new ChartWrapper[]{
				new ChartWrapper(bubbleChart, data, getBubbleChartOptions(), 0.65, 0.8),
				new ChartWrapper(table, data, getTableOptions(), 0.3, 0.7)
		};
	}

	protected AbstractDataTable getDataTable(){
		return ((LiveStatsChartDataProvider)dataProvider).getLiveUsageBubbleChartView();
	}
	
	private Options bubbleOptions;
	private Options getBubbleChartOptions() {
		if(bubbleOptions == null){
			bubbleOptions = Options.create();

			bubbleOptions.setTitle("Most Watched Programs");
			AxisOptions vopts = AxisOptions.create();
			AxisOptions hopts = AxisOptions.create();
			TextStyle ts = TextStyle.create();
			ts.setFontSize(8);
			hopts.setTextStyle(ts);
			vopts.setTextStyle(ts);
			bubbleOptions.setLegendTextStyle(ts);
//			bubbleOptions.set("bubble.textStyle", "{fontSize: 7}");
			bubbleOptions.setFontSize(8);
			vopts.set("title", "Viewers");
			hopts.set("title", "Hours");
			TextStyle tts = TextStyle.create();
			tts.setFontSize(14);
			vopts.setTitleTextStyle(tts);
			hopts.setTitleTextStyle(tts);
			bubbleOptions.setVAxisOptions(vopts);
			bubbleOptions.setHAxisOptions(hopts);

//			ChartArea ca = ChartArea.create();
//			ca.setLeft(20);
//			bubbleOptions.setChartArea(ca);
			
			bubbleOptions.set("sortBubblesBySize", true);
		}		
		return bubbleOptions;
	}	
	
	private Table.Options tableOptions;
	private Table.Options getTableOptions(){
		if(tableOptions == null){
			tableOptions = Table.Options.create();

			tableOptions.setAllowHtml(true);
//			tableOptions.setSortColumn();
		}
		return tableOptions;
	}
	
	@Override
	public void onDataLoaded(DataLoadedEvent event) {
		switch (event.eventType) {
		case STATS_DATA_LOADED_EVENT:
			if(event.source.equals("_LiveStatsChartDataProvider")){
				draw();
			}
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
