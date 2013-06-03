package com.shico.mnm.stats.client.charts.periodic;

import java.util.Map;

import com.google.gwt.core.client.Callback;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.google.gwt.visualization.client.visualizations.corechart.TextStyle;
import com.shico.mnm.common.chart.ColumnChartPortlet;
import com.shico.mnm.common.event.DataLoadedEvent;
import com.shico.mnm.common.event.EventBus;
import com.shico.mnm.stats.client.DeadStatsChartDataProvider;
import com.shico.mnm.stats.client.StatsChartDataProvider;
import com.shico.mnm.stats.client.comp.ChartFormWin;
import com.smartgwt.client.types.AnimationEffect;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.ClickEvent;

public class LiveUsageColumnChartPortlet extends ColumnChartPortlet {
	private DeadStatsChartDataProvider dataProvider;
	
	public LiveUsageColumnChartPortlet(StatsChartDataProvider dataProvider, double widthRatio, double heightRatio) {
		super(dataProvider, widthRatio, heightRatio);
		this.dataProvider = (DeadStatsChartDataProvider)dataProvider;
		
		EventBus.instance().addHandler(DataLoadedEvent.TYPE, this);		
		
		setTitle("Watched Channels");
		
		// initial load.
		// TODO: from and to dates must be replaced by the current day
		dataProvider.getRowsInBatch("LiveUsage", "2013-02-14", "2013-02-15", "viewers,top,10");
	}

	@Override
	public void onDataLoaded(DataLoadedEvent event) {
		switch (event.eventType) {
		case STATS_DATA_LOADED_EVENT:
			if(event.source.equals("_DeadStatsChartDataProvider")){
				Map<String, Object> info = event.info;
				String type = (String)info.get(StatsChartDataProvider.STATS_EVENT_TYPE);
				if(type != null && type.equals("LiveUsage")){
					draw();
				}
			}
			break;
		}
	}

	@Override
	protected void handleRefresh(ClickEvent event) {
		draw();
	}
	
	private ChartFormWin chartFormWin;
	long currentTo = 1368535898886L; // max toTS in db-table
	long currentFrom = currentTo-(12*3600000);
	@Override
	protected void handleSettings(ClickEvent event) {
		if(chartFormWin == null){
			chartFormWin = new ChartFormWin(new Callback<Map<String,Object>, String>() {
				
				@Override
				public void onSuccess(Map<String, Object> result) {
					String type = (String)result.get("type");
					String from = (String)result.get("from");
					String to = (String)result.get("to");
					
					DataTable lud = dataProvider.getData("LiveUsage");
					if(lud != null){
						lud.removeRows(0, lud.getNumberOfRows());
					}
					
					dataProvider.getRows(type, from, to, "viewers,top,10");
				}
				
				@Override
				public void onFailure(String reason) {
					SC.say("Error", reason);
				}
			});
		}
		chartFormWin.setTop(getAbsoluteTop()+20);
		chartFormWin.setLeft(getAbsoluteLeft()+getWidth()-310);
		chartFormWin.animateShow(AnimationEffect.SLIDE);
	}
	
	private Options columnChartOpts;
	@Override
	protected Options getOptions() {
		if(columnChartOpts == null){
			columnChartOpts = Options.create();

			columnChartOpts.set("animation.easing", "out");
			columnChartOpts.set("animation.duration", 2000d);
			AxisOptions vopts = AxisOptions.create();
			AxisOptions hopts = AxisOptions.create();
			TextStyle ts = TextStyle.create();
			ts.setFontSize(8);
			hopts.setTextStyle(ts);
			hopts.set("slantedText", true);
			vopts.setTextStyle(ts);
			vopts.set("title", "Viewers");
			columnChartOpts.setVAxisOptions(vopts);
			columnChartOpts.setHAxisOptions(hopts);
		}
		return columnChartOpts;
	}

	@Override
	protected AbstractDataTable getView() {
		return dataProvider.getLiveUsageColumnChartView(StatsChartDataProvider.viewersIdx);
	}

	@Override
	protected void handleHelp(ClickEvent event) {
		// TODO Auto-generated method stub
		
	}	
	
}
