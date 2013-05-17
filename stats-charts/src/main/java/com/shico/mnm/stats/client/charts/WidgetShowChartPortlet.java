package com.shico.mnm.stats.client.charts;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.google.gwt.visualization.client.visualizations.corechart.TextStyle;
import com.shico.mnm.common.chart.PieChartPortlet;
import com.shico.mnm.common.event.DataLoadedEvent;
import com.shico.mnm.common.event.EventBus;
import com.shico.mnm.stats.client.LiveStatsChartDataProvider;
import com.shico.mnm.stats.client.StatsChartDataProvider;
import com.smartgwt.client.widgets.events.ClickEvent;

public class WidgetShowChartPortlet extends PieChartPortlet {

	public WidgetShowChartPortlet(StatsChartDataProvider dataProvider, double widthRatio, double heightRatio) {
		super(dataProvider, widthRatio, heightRatio);
		setTitle("Most Popular Widgets");
		
		EventBus.instance().addHandler(DataLoadedEvent.TYPE, this);		
	}

	@Override
	protected AbstractDataTable getView() {
		return ((LiveStatsChartDataProvider)dataProvider).getMostPopularWidgetsPieChartView();
	}

	@Override
	protected Options getOptions() {
		Options opts = getChartOptions("Most Popular Widgets");
		
		AxisOptions vopts = AxisOptions.create();
		AxisOptions hopts = AxisOptions.create();
		TextStyle ts = TextStyle.create();
		ts.setFontSize(8);
		hopts.setTextStyle(ts);
		vopts.setTextStyle(ts);
//		vopts.set("title", "Hours");
		opts.setVAxisOptions(vopts);
		opts.setHAxisOptions(hopts);
		
		opts.set("is3D", true);
		
		return opts;
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
	
	private native JavaScriptObject getVAxisOptions()/*-{
		return {left:50, width:"80%"}
	}-*/;

}
