package com.shico.mnm.stats.client.charts.periodic;

import java.util.Map;

import com.google.gwt.core.client.Callback;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.DataTable;
import com.shico.mnm.common.event.DataLoadedEvent;
import com.shico.mnm.stats.client.DeadStatsChartDataProvider;
import com.shico.mnm.stats.client.StatsChartDataProvider;
import com.shico.mnm.stats.client.charts.live.LiveUsageTableAndBubbleChartPortlet;
import com.shico.mnm.stats.client.comp.ChartFormWin;
import com.smartgwt.client.types.AnimationEffect;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.ClickEvent;

public class LiveUsageTableAndBubbleChartPortlet2 extends LiveUsageTableAndBubbleChartPortlet {
	
	public LiveUsageTableAndBubbleChartPortlet2(StatsChartDataProvider dataProvider, double widthRatio, double heightRatio) {
		super(dataProvider, widthRatio, heightRatio);
		
		// load initial data
		// TODO: start and end must be replaced by the current day
		final long end = 1368535898886L; // max toTS in db-table
		final long start = end-(12*3600000);
		((DeadStatsChartDataProvider)dataProvider).getRows(StatsChartDataProvider.PROGRAM_EVENT, start, end, "title,viewers,top,10");
	}
	
	@Override
	protected AbstractDataTable getDataTable(){
		return ((DeadStatsChartDataProvider)dataProvider).getLiveUsageBubbleChartView();
	}

	@Override
	public void onDataLoaded(DataLoadedEvent event) {
		switch (event.eventType) {
		case STATS_DATA_LOADED_EVENT:
			if(event.source.equals("_DeadStatsChartDataProvider")){
				Map<String, Object> info = event.info;
				String type = (String)info.get(StatsChartDataProvider.STATS_EVENT_TYPE);
				if(type != null && type.equals(StatsChartDataProvider.PROGRAM_EVENT)){
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
//					Date from = (Date)result.get("from");
//					Date to = (Date)result.get("to");
//					
//					currentFrom = from.getTime();
//					currentTo = to.getTime();
					DataTable lud = ((DeadStatsChartDataProvider)dataProvider).getData(StatsChartDataProvider.PROGRAM_EVENT);
					if(lud != null){
						lud.removeRows(0, lud.getNumberOfRows());
					}
					dataProvider.getRows(StatsChartDataProvider.PROGRAM_EVENT, from, to, "title,viewers,top,10");
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
}
