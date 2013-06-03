package com.shico.mnm.stats.client.charts.live;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.LegendPosition;
import com.google.gwt.visualization.client.Selection;
import com.google.gwt.visualization.client.events.SelectHandler;
import com.google.gwt.visualization.client.visualizations.Table;
import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.ColumnChart;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.google.gwt.visualization.client.visualizations.corechart.TextStyle;
import com.shico.mnm.common.chart.ChartWrapper;
import com.shico.mnm.common.chart.MultipleChartPortlet;
import com.shico.mnm.common.event.DataLoadedEvent;
import com.shico.mnm.common.event.EventBus;
import com.shico.mnm.stats.client.LiveStatsChartDataProvider;
import com.shico.mnm.stats.client.StatsChartDataProvider;
import com.smartgwt.client.widgets.events.ClickEvent;

public class LiveUsageTableAndColumnChartPortlet extends MultipleChartPortlet {
	private StatsChartDataProvider dataProvider;
	private ColumnChart columnChart;
	private Table table;
	
	public LiveUsageTableAndColumnChartPortlet(StatsChartDataProvider dataProvider, double widthRatio, double heightRatio) {
		super(widthRatio, heightRatio);
		this.dataProvider = dataProvider;
		
		setTitle("Channel View");
		
		EventBus.instance().addHandler(DataLoadedEvent.TYPE, this);		
	}

	protected AbstractDataTable getColumnChartDataTable(){
		return ((LiveStatsChartDataProvider)dataProvider).getLiveUsageColumnChartView();
	}
	
	protected AbstractDataTable getTableChartDataTable(){
		return ((LiveStatsChartDataProvider)dataProvider).getLiveUsageTableView();
	}
	
	@Override
	protected ChartWrapper[] getCharts() {
		AbstractDataTable data = getColumnChartDataTable();
		AbstractDataTable tableData = getTableChartDataTable();
		if(columnChart == null){
			columnChart = new ColumnChart(data, getColumnChartOptions());
		}
		if(table == null){
			table = new Table();
			
			columnChart.addSelectHandler(new SelectHandler() {			
				@Override
				public void onSelect(SelectEvent event) {
					JsArray<Selection> selections = columnChart.getSelections();
					try{
						table.setSelections(selections);
					}catch(Exception e){
//						 SC.say(e.getMessage());
					}
				}
			});
		}

		return new ChartWrapper[]{
				new ChartWrapper(columnChart, data, getColumnChartOptions(), 0.65, 0.8),
				new ChartWrapper(table, tableData, getTableOptions(), 0.3, 0.75)
		};
	}

	protected Options columnChartOpts;
	protected Options getColumnChartOptions() {
		if(columnChartOpts == null){
			columnChartOpts = Options.create();
			TextStyle ts = TextStyle.create();
			ts.setFontSize(8);

			columnChartOpts.setLegend(LegendPosition.NONE);
			AxisOptions vopts = AxisOptions.create();
			AxisOptions hopts = AxisOptions.create();
			hopts.setTextStyle(ts);
			vopts.setTextStyle(ts);
			vopts.set("title", "Hours");
			columnChartOpts.setVAxisOptions(vopts);
			columnChartOpts.setHAxisOptions(hopts);
		}
		return columnChartOpts;
	}	
	
	protected Table.Options tableOptions;
	protected Table.Options getTableOptions(){
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
		// nothing for now
	}

	@Override
	protected void handleHelp(ClickEvent event) {
		// TODO Auto-generated method stub
		
	}	
}
