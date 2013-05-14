package com.shico.mnm.stats.client;

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

	long startTS = 1367411036351L;
	long lastFrom = startTS;  
	long lastTo = lastFrom + 10000;

	private StatsSettingsController settingsController;
	
	public LiveStatsChartDataProvider() {
		super();
		
		settingsController = StatsClientHandle.getStatsSettingsController();

		EventBus.instance().addHandler(DataLoadedEvent.TYPE, this);
	}

	@Override
	protected void setData(DataTable data) {
		this.data = data;
	}


	@Override
	protected void setGroupedData(DataTable data) {
		this.groupedData = data;
	}


	@Override
	protected DataTable getData() {
		return this.data;
	}


	@Override
	protected void fireEvent(DataEventType eventtype) {
		switch(eventtype){
		case FAILED_STATS_METADATA_LOADED_EVENT:
		case FAILED_STATS_DATA_LOADED_EVENT:
		case STATS_METADATA_LOADED_EVENT:
		case STATS_DATA_LOADED_EVENT:
			DataLoadedEvent ev = new DataLoadedEvent(eventtype);
			ev.source = LiveStatsChartDataProvider.class;		
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
				lastTo += schPeriodInMillis;
			}
		};
		timer.schedule(schPeriodInMillis);
//		timer.scheduleRepeating(schPeriodInMillis);
	}

	@Override
	public DataView getLiveUsagePieChartView() {
		return getNativeLiveUsagePieChartView(DataView.create(groupedData), groupedData);
	}
	
	@Override
	public DataView getLiveUsageColumnChartView() {
		return getNativeLiveUsageColumnChartView(groupedData);
	}

	@Override
	public DataView getLiveUsageTableView() {
		return getNativeLiveUsageTableView(groupedData);
	}

	@Override
	public AbstractDataTable getLiveUsageBubbleChartView() {
		return getMostPopularProgramsView(data);
	}

	@Override
	public AbstractDataTable getMostPopularMovieRentals() {
		return getMostPopularMovieRentals(groupedData);
	}
	
	@Override
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

	
	private native AbstractDataTable getMostPopularProgramsView(DataTable data)/*-{
		var view = new $wnd.google.visualization.DataView(data);
		var rowIdxs = data.getFilteredRows([{column: @com.shico.mnm.stats.client.StatsChartDataProvider::typeIdx, value: 'LiveUsage'}]);
		view.setRows(rowIdxs);
		var progdata = $wnd.google.visualization.data.group(
			view, 
			[@com.shico.mnm.stats.client.StatsChartDataProvider::typeIdx,
			@com.shico.mnm.stats.client.StatsChartDataProvider::nameIdx,
			@com.shico.mnm.stats.client.StatsChartDataProvider::titleIdx],
			[{
				'column': @com.shico.mnm.stats.client.StatsChartDataProvider::totalDurationIdx, 
				'aggregation': $wnd.google.visualization.data.sum, 
				'type': 'number',
				'label': 'viewedMillis'
			},
			{
				'column': @com.shico.mnm.stats.client.StatsChartDataProvider::sumIdx, 
				'aggregation': $wnd.google.visualization.data.sum, 
				'type': 'number',
				'label': 'viewers'
			}]
		);
		
		var view2 = new $wnd.google.visualization.DataView(progdata);
		view2.setColumns([
			{sourceColumn:2, id: 'ID', type: 'string', label:'Program'}, 
			{calc:toHoursAndMinutes, type:'number', label:'Hours'}, 
			{sourceColumn: 4, type: 'number', label:'Viewers'},
			{sourceColumn:1, type: 'string', label:'Channel'},
			{calc:getWeightedPopularity, type:'number', label:'Popularity metric', id:'PopularityMetric'}
		]);
		
		// Sort on avgViewTime column and pick only the top ten programs.
		var topTenIdxs = new Array();
		var sorted = view2.getSortedRows(4);
		var numOfRows = sorted.length;
		for(var i=numOfRows-1; i>=Math.max(0, numOfRows-10); i--){
			topTenIdxs.push(sorted[i]); 
		}
		view2.setRows(topTenIdxs);
		
		function toHoursAndMinutes(dataTable, rowNum){
			return Math.round(dataTable.getValue(rowNum, 3) / 36000) / 100;
		}

		function getWeightedPopularity(dataTable, rowNum){
			var duration = dataTable.getValue(rowNum, 3);
			var viewers = dataTable.getValue(rowNum, 4);
			var maxduration = dataTable.getColumnRange(3).max;
//			var maxviewers =  dataTable.getColumnRange(4).max;
//			var durationInHour = Math.round(duration / 36000) / 100;
			var viewersWeight = viewers * 0.7;
			var durationWeight = duration * 0.3;
			var totalWeight = viewersWeight * durationWeight;
			var maxweight = (maxduration / viewers) * totalWeight;
			var weightedPopularity = (duration / viewers) * totalWeight;
			
			var popMetric = (weightedPopularity * 100) / maxweight;
			return popMetric.toFixed(0) / 10;
		}

		return view2;
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
			getRows(lastFrom, lastTo);
			lastFrom = lastTo;
			lastTo += 5000;
			getRows(lastFrom, lastTo);
			break;
		case STATS_CHART_SETTINGS_CHANGED_EVENT:
			String chartUrl = (String)event.info.get(StatsRemoteSettingsDS.CHARTURL);
			if(chartUrl == null || chartUrl.trim().isEmpty()){
				return;
			}
			
			Resource resource = new Resource(chartUrl);
			service = GWT.create(StatsRestService.class);
			((RestServiceProxy)service).setResource(resource);

			// Get column metadata
			getColumnNames();

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
			
			break;
		}
	}
}
