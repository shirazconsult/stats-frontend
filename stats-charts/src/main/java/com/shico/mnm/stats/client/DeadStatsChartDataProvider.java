package com.shico.mnm.stats.client;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fusesource.restygwt.client.Resource;
import org.fusesource.restygwt.client.RestServiceProxy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.DataView;
import com.shico.mnm.common.event.DataEventType;
import com.shico.mnm.common.event.DataLoadedEvent;
import com.shico.mnm.common.event.DataLoadedEventHandler;
import com.shico.mnm.common.event.EventBus;
import com.shico.mnm.stats.model.StatsRemoteSettingsDS;

public class DeadStatsChartDataProvider extends AbstractStatsChartDataProvider implements StatsChartDataProvider, DataLoadedEventHandler {
	private static Logger logger = Logger.getLogger("StatsChartDataProviderImpl");
	
	Map<String, DataTable> dataMap;
	
	private StatsSettingsController settingsController;
	
	public DeadStatsChartDataProvider() {
		super();

		dataMap = new HashMap<String, DataTable>();		
	}

	@Override
	protected void setData(DataTable data) {
		for (String se : statsEvents) {
			dataMap.put(se, clone(data));
		}
	}

	private native DataTable clone(DataTable data)/*-{
		return data.clone();
	}-*/;
	
	@Override
	protected void postUpdate(DataTable data) {
		// nothing for now
	}


	@Override
	protected DataTable getData(String type) {
		DataTable dt = dataMap.get(type);
		if(dt != null){
			dt.removeRows(0, dt.getNumberOfRows());
		}
		return dt;
	}


	@Override
	protected void fireEvent(DataEventType eventtype, Map<String, Object> info) {
		switch(eventtype){
		case FAILED_STATS_METADATA_LOADED_EVENT:
		case FAILED_STATS_DATA_LOADED_EVENT:
		case STATS_METADATA_LOADED_EVENT:
		case STATS_DATA_LOADED_EVENT:
			DataLoadedEvent ev = new DataLoadedEvent(eventtype, info);
			ev.source = "_DeadStatsChartDataProvider";		
			EventBus.instance().fireEvent(ev);
			break;
		}	
	}

	@Override
	public void getLastRow() {
		throw new RuntimeException("Not Implemented. Use getLastRow instead.");
	}
	
	@Deprecated
	public void getRows(long from, long to, String statsEventType){
		DataTable data = dataMap.get(statsEventType);
		if(data == null){
			logger.log(Level.WARNING, statsEventType+" is not registered as a legal Statistics data type.");
			return;
		}
		if(!hasData(data, from, to)){
			getRowsExponatially(statsEventType, from, to);
		}else{
			fireDataLoadedEvent(DataEventType.STATS_DATA_LOADED_EVENT, statsEventType);
		}
	}

	private boolean hasData(DataTable data, long from, long to){
		if(data.getNumberOfRows() >= 1){			
			double first = data.getValueDouble(0, toIdx);
			double last = data.getValueDouble(data.getNumberOfRows()-1, toIdx);
			
			double myfrom = (double)from;
			double myto = (double)to;
			return first <= myfrom && last >= myto; 
		}
		return false;
	}
	
	public AbstractDataTable getLiveUsageBubbleChartView(long from, long to) {
		return getTopProgramsView(dataMap.get("LiveUsage"), from, to);
//		return getMostPopularProgramsView(dataMap.get("LiveUsage"), (double)from, (double)to);
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
			if(event.source.startsWith("_Dead")){
				// Load previous days data per default
//				long now = System.currentTimeMillis();
				final long end = 1368535898886L; // max toTS in db-table
				final long start = end-(12*3600000);
				getRows("LiveUsage", start, end, "viewers,top,10");
//				getRowsExponatially("LiveUsage", start, end);
			}
			break;
		case STATS_CHART_SETTINGS_CHANGED_EVENT:
			if(event.source.startsWith("_Dead")){
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
}
