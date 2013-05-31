package com.shico.mnm.stats.client;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fusesource.restygwt.client.Resource;
import org.fusesource.restygwt.client.RestServiceProxy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.DataView;
import com.shico.mnm.common.event.DataEventType;
import com.shico.mnm.common.event.DataLoadedEvent;
import com.shico.mnm.common.event.DataLoadedEventHandler;
import com.shico.mnm.common.event.EventBus;
import com.shico.mnm.stats.model.StatsRemoteSettingsDS;

public class LiveStatsChartDataProvider extends AbstractStatsChartDataProvider implements StatsChartDataProvider, DataLoadedEventHandler {
	private static Logger logger = Logger.getLogger("StatsChartDataProviderImpl");
	
	DataTable data;
	DataTable groupedData;
	
	int minSchedulePeriodInSec = 10; // don't schedule less than 10 sec.

	long startTS = 1359729385133L;
	long lastFrom = startTS;  
	long lastTo = 1359988604573L;

	public LiveStatsChartDataProvider() {
		super();		
	}

	@Override
	protected void setData(DataTable data) {
		this.data = data;
	}


	@Override
	protected void postUpdate(DataTable data) {
		groupedData = getGroupedData(data);
	}


	@Override
	protected DataTable getData(String type) {
		return this.data;
	}


	@Override
	protected void fireEvent(DataEventType eventtype, Map<String, Object> info) {
		switch(eventtype){
		case FAILED_STATS_METADATA_LOADED_EVENT:
		case FAILED_STATS_DATA_LOADED_EVENT:
		case STATS_METADATA_LOADED_EVENT:
		case STATS_DATA_LOADED_EVENT:
			DataLoadedEvent ev = new DataLoadedEvent(eventtype, info);
			ev.source = "_LiveStatsChartDataProvider";		
			EventBus.instance().fireEvent(ev);
			break;
		}	
	}

	@Override
	public void getLastRow() {
		throw new RuntimeException("Not Implemented. Use getLastRow instead.");
	}

	@Override
	public AbstractDataTable getDataTable() {
		return groupedData;
	}

	public AbstractDataTable getLiveUsagePieChartView() {
		return getNativeLiveUsagePieChartView(DataView.create(groupedData), groupedData);
	}
	
	public AbstractDataTable getLiveUsageColumnChartView() {
		return getNativeLiveUsageColumnChartView(groupedData);
	}

	public AbstractDataTable getLiveUsageTableView() {
		return getNativeLiveUsageTableView(groupedData);
	}

	public AbstractDataTable getLiveUsageBubbleChartView() {
		return getMostPopularProgramsView(data, 0, 0);
	}

	public AbstractDataTable getMostPopularMovieRentals() {
		return getMostPopularMovieRentals(groupedData);
	}
	
	public AbstractDataTable getMostPopularWidgetsPieChartView(){
		return getMostPopularWidgetsPieChartView(groupedData);
	}
	
	private native DataTable getMostPopularMovieRentals(DataTable data)/*-{
		var rowIdxs = data.getFilteredRows([{column: 0, value: 'movieRent'}]);
		
		view = new $wnd.google.visualization.DataView(data);
		view.setRows(rowIdxs);
		var topTenIdxs = new Array();
		var sortedIdxs = view.getSortedRows(3);
		var numOfRows = sortedIdxs.length;
		for(var i=numOfRows-1; i>=Math.max(0, numOfRows-10); i--){
			topTenIdxs.push(sortedIdxs[i]); 
		}
		view.setRows(topTenIdxs);

		var row = new Array();
		var lut = new $wnd.google.visualization.DataTable();
		lut.addColumn('string', '');
		row[0] = '';
		var sortByName = view.getSortedRows(1);
		for(var i=0; i<view.getNumberOfRows(); i++){
			var col = view.getValue(sortByName[i], 1);
			lut.addColumn('number', col);
			row[i+1] = view.getValue(sortByName[i], 3);
		}
		lut.addRow(row);
				
		return lut;
	}-*/;
	
	private native AbstractDataTable getMostPopularWidgetsPieChartView(DataTable data)/*-{
		var rowIdxs = data.getFilteredRows([{column: 0, value: 'widgetShow'}]);

		view = new $wnd.google.visualization.DataView(data);
		view.setRows(rowIdxs);
		
		var dt = new $wnd.google.visualization.DataTable();
		dt.addColumn('string', 'Widget');
		dt.addColumn('number', 'Used');
		var row = new Array();
		// add top 10 widgets to dt.
		var sortedIdxs = view.getSortedRows(3);		
		for(var i=sortedIdxs.length-1; i>=Math.max(0, sortedIdxs.length-10); i--){
			row[i,0] = view.getValue(i, 1);
			row[i, 1] = view.getValue(i, 3);
			dt.addRow(row);
		}
		// sort by widget name
		dt.sort(0);

		return dt;		
	}-*/;

	private native DataView getNativeLiveUsageChartWrapperView(DataTable data)/*-{
		var rowIdxs = data.getFilteredRows([{column: @com.shico.mnm.stats.client.StatsChartDataProvider::typeIdx, value: 'LiveUsage'}]);

		var row = new Array();
		var lut = new $wnd.google.visualization.DataTable();
		lut.addColumn('string', '');
		row[0] = '';
		for(var i=0; i<rowIdxs.length; i++){
			var col = data.getValue(rowIdxs[i], 1);
			lut.addColumn('number', col);
			row[i+1] = Math.round(data.getValue(rowIdxs[i], 2) / 36000) / 100;
		}
		lut.addRow(row);
		
		return new $wnd.google.visualization.DataView(lut);
	}-*/;

	private native DataView getNativeLiveUsageColumnChartView(DataTable data)/*-{
		var view = new $wnd.google.visualization.DataView(data);
		view.setColumns(
		[
		{sourceColumn: @com.shico.mnm.stats.client.StatsChartDataProvider::nameIdx, type:'string', label:'Channel'}, 
		{calc:toHoursAndMinutes, type:'number', label:'Hours'}
		]);
		var rowIdxs = data.getFilteredRows([{column: @com.shico.mnm.stats.client.StatsChartDataProvider::typeIdx, value: 'LiveUsage'}]);
    	view.setRows(rowIdxs);	    

		function toHoursAndMinutes(dataTable, rowNum){
			return Math.round(dataTable.getValue(rowNum, 2) / 36000) / 100;
		}

		return view;		
	}-*/;

	private native DataView getNativeLiveUsageTableView(DataTable data)/*-{
		var view = new $wnd.google.visualization.DataView(data);
		view.setColumns(
		[
		{sourceColumn: @com.shico.mnm.stats.client.StatsChartDataProvider::nameIdx, type:'string', label:'Channel'}, 
		{sourceColumn:3, type:'number', label:'Viewers'},
		{calc:toHoursAndMinutes, type:'number', label:'Hours'}
		]);
		var rowIdxs = data.getFilteredRows([{column: @com.shico.mnm.stats.client.StatsChartDataProvider::typeIdx, value: 'LiveUsage'}]);
		view.setRows(rowIdxs);	    
	
		function toHoursAndMinutes(dataTable, rowNum){
			return Math.round(dataTable.getValue(rowNum, 2) / 36000) / 100;
		}
	
		return view;		
	}-*/;

	private native DataView getNativeLiveUsagePieChartView(DataView view, DataTable data)/*-{
		view.setColumns(
		[
		@com.shico.mnm.stats.client.StatsChartDataProvider::nameIdx, 
		{calc:toHoursAndMinutes, type:'number', title:'hours'}
		]);
		var rowIdxs = data.getFilteredRows([{column: @com.shico.mnm.stats.client.StatsChartDataProvider::typeIdx, value: 'LiveUsage'}]);
	    view.setRows(rowIdxs);	    
	
		function toHoursAndMinutes(dataTable, rowNum){
			return Math.round(dataTable.getValue(rowNum, 2) / 36000) / 100;
		}

		return view;		
	}-*/;

	@Override
	public void onDataLoaded(DataLoadedEvent event) {
		switch (event.eventType) {
		case STATS_METADATA_LOADED_EVENT:
			// get rows for the last 20 seconds
//			long now = System.currentTimeMillis();
			if(event.source.startsWith("_Live")){
				getRows(lastFrom, lastTo);
//				getRowsExponatially(lastFrom, lastTo);
				schedule();
			}
			break;
		case STATS_CHART_SETTINGS_CHANGED_EVENT:
			if(event.source.startsWith("_Live")){
				String chartUrl = (String)event.info.get(StatsRemoteSettingsDS.CHARTURL);
				if(chartUrl == null || chartUrl.trim().isEmpty()){
					return;
				}

				Resource resource = new Resource(chartUrl);
				service = GWT.create(StatsRestService.class);
				((RestServiceProxy)service).setResource(resource);

				// Get column metadata
				getColumnNames();
			}			
			break;
		}
	}
	
	private void schedule(){
		// Set viewsize and Refresh interval
		int refreshInterval = minSchedulePeriodInSec;
		try{
			refreshInterval = Integer.parseInt((String)settingsController.getSetting(StatsRemoteSettingsDS.CHARTREFRESHINTERVAL));
		}catch(NumberFormatException nfe){
			logger.log(Level.WARNING, nfe.getMessage());
		}
		int slidingSize = slidingWinSize;
		try{
			slidingSize = Integer.parseInt((String)settingsController.getSetting(StatsRemoteSettingsDS.CHARTWINSIZE));
		}catch(NumberFormatException nfe){
			logger.log(Level.WARNING, nfe.getMessage());
		}
		slidingWinSize = Math.min(slidingSize, slidingWinSize);
		
		schedule(Math.max(refreshInterval, minSchedulePeriodInSec), slidingWinSize);	
	}
	
	Timer timer;
	@Override
	public void schedule(final int schedulePeriodInSec, int maxDataRows) {
		if(timer != null){
			timer.cancel();
		}
		final int schPeriodInMillis = schedulePeriodInSec*1000;
		timer = new Timer() {
			public void run() {
				getRows(lastFrom, lastTo);
				lastFrom = lastTo;
//				lastTo += schPeriodInMillis;
				lastTo = lastTo+3600000;
			}
		};
//		timer.schedule(schPeriodInMillis);
		timer.scheduleRepeating(schPeriodInMillis);
	}
	
}
