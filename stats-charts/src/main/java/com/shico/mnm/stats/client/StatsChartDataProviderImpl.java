package com.shico.mnm.stats.client;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.Resource;
import org.fusesource.restygwt.client.RestServiceProxy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.DataView;
import com.shico.mnm.common.event.DataEventType;
import com.shico.mnm.common.event.DataLoadedEvent;
import com.shico.mnm.common.event.DataLoadedEventHandler;
import com.shico.mnm.common.event.EventBus;
import com.shico.mnm.common.model.ListResult;
import com.shico.mnm.common.model.NestedList;

public class StatsChartDataProviderImpl implements StatsChartDataProvider, DataLoadedEventHandler {
	private static Logger logger = Logger.getLogger("StatsChartDataProviderImpl");
	
//	DataTable liveUsageData = null;
//	DataTable widgetShowData = null;
//	DataTable vodUsageMovieData = null;
//	DataTable vodUsageTrailerData = null;
//	DataTable dvrUsageData = null;
//	DataTable webtvLoginData = null;
//	DataTable startOverUsageData = null;
//	DataTable timeshiftUsageData = null;
//	DataTable movierentData = null;
//	DataTable shopLoadedData = null;
//	DataTable adadtionData = null;
	
	DataTable data;
	DataTable groupedData;
	DataView liveUsageView = null;
	SortedSet<String> channelSet = new TreeSet<String>();
	
	StatsRestService service;
	int fetchedRowIdx;
	int slidingWinSize = 10000; // 1 million rows
	int minSchedulePeriodInSec = 10; // don't schedule less than 10 sec.
	boolean updateView;

	public StatsChartDataProviderImpl() {
		super();
		
//		settingsController = AmqClientHandle.getAmqSettingsController();

		EventBus.instance().addHandler(DataLoadedEvent.TYPE, this);
	}

	
	@Override
	public void getColumnNames() {
		if(data == null){
			data = DataTable.create();
			service.getViewColumns(new MethodCallback<ListResult<String>>() {
				public void onFailure(Method method, Throwable exception) {
					logger.log(Level.SEVERE, "Failed to get column metadata."+ exception.getMessage());
					EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.FAILED_STATS_METADATA_LOADED_EVENT));
				}
				public void onSuccess(Method method, ListResult<String> response) {
					logger.log(Level.INFO, "Retrieving column metadata info. ");
					List<String> result = response.getResult();
					data.addColumn(ColumnType.STRING, result.get(typeIdx));
					data.addColumn(ColumnType.STRING, result.get(nameIdx));
					data.addColumn(ColumnType.STRING, result.get(titleIdx));
					data.addColumn(ColumnType.NUMBER, result.get(sumIdx));
					data.addColumn(ColumnType.NUMBER, result.get(minDurationIdx));
					data.addColumn(ColumnType.NUMBER, result.get(maxDurationIdx));
					data.addColumn(ColumnType.NUMBER, result.get(totalDurationIdx));
					data.addColumn(ColumnType.NUMBER, result.get(fromIdx));
					data.addColumn(ColumnType.NUMBER, result.get(toIdx));
					
					EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.STATS_METADATA_LOADED_EVENT));
				}
			});
		}		
	}

	@Override
	public void getLastRow() {
		throw new RuntimeException("Not Implemented. Use getLastRow instead.");
	}

	@Override
	public void getRows(long from, long to) {
		logger.log(Level.INFO, "Requesting records from "+from+" to "+to);
		service.getViewRows(from, to, new MethodCallback<NestedList<Object>>() {
			
			public void onFailure(Method method, Throwable exception) {
				logger.log(Level.SEVERE, "Failed to get data rows. "+ exception.getMessage());
				EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.FAILED_STATS_DATA_LOADED_EVENT));
			}

			public void onSuccess(Method method, NestedList<Object> response) {
				try{
					List<ListResult<Object>> page = response.getRows();
					if(page.isEmpty()){
						logger.log(Level.WARNING, "No row returned from Statistics Rest Service.");
						return;
					}
					int idx = data.getNumberOfRows();
					data.addRows(page.size());
					fetchedRowIdx += page.size();							
					for (ListResult<Object> row : page) {
						int colIdx = 0;
						List<Object> rec = row.getResult();
						for (Object elem : rec) {		
							if(colIdx <= 2){
								data.setValue(idx, colIdx, elem == null ? null : elem.toString());
							}else{
								data.setValue(idx, colIdx, (Double)elem);
							}	
							colIdx++;
						}
						idx++;
					}
					// If num. of rows are more than slidingWinSize, then shrink back to slidingWinSize 
					if(data.getNumberOfRows() >= slidingWinSize){
						data.removeRows(0, data.getNumberOfRows()-slidingWinSize);
						updateView = true;
					}
					
					groupedData = getGroupedData(data);
					EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.STATS_DATA_LOADED_EVENT));
				}catch(Exception e){
					logger.log(Level.SEVERE, "Exception while updating data. "+e.getMessage());
				}
			}
		});
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
		timer = new Timer() {
			long interval = Math.max(minSchedulePeriodInSec, schedulePeriodInSec)*1000;
			public void run() {
				getRows(myfrom, myto);
				myfrom = myto;
				myto += interval;
			}
		};
		slidingWinSize = maxDataRows;
		timer.scheduleRepeating(Math.max(minSchedulePeriodInSec, schedulePeriodInSec)*1000);
	}

	@Override
	public DataView getNativeLiveUsagePieChartView() {
		liveUsageView = getNativeLiveUsagePieChartView(DataView.create(groupedData), groupedData);
		return liveUsageView;
	}
	
	@Override
	public DataView getNativeLiveUsageColumnChartView() {
		liveUsageView = getNativeLiveUsageColumnChartView(groupedData);
		return liveUsageView;
	}

	private native DataTable getGroupedData(DataTable data)/*-{
		var channels = data.getDistinctValues(@com.shico.mnm.stats.client.StatsChartDataProvider::nameIdx);  // get array of channels in ascending order
		return $wnd.google.visualization.data.group(
			data, 
			[@com.shico.mnm.stats.client.StatsChartDataProvider::typeIdx,
			@com.shico.mnm.stats.client.StatsChartDataProvider::nameIdx],
			[{
				'column': @com.shico.mnm.stats.client.StatsChartDataProvider::totalDurationIdx, 
				'aggregation': $wnd.google.visualization.data.sum, 
				'type': 'number',
				'label': 'viewedMillis'
			}]
		);
	}-*/;

	// TODO: set the percentage on the graph
	private native DataView getNativeLiveUsageColumnChartView2(DataTable data)/*-{
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

	long myfrom  = 1367411036351L;
	long myto = myfrom + 10000;

	@Override
	public void onDataLoaded(DataLoadedEvent event) {
		switch (event.eventType) {
		case STATS_METADATA_LOADED_EVENT:
			// get rows for the last 20 seconds
//			long now = System.currentTimeMillis();
			getRows(myfrom, myto);
			schedule(10, slidingWinSize);
			break;
		case STATS_CHART_SETTINGS_CHANGED_EVENT:
//			String chartUrl = (String)event.info.get(AmqRemoteSettingsDS.CHARTURL);
//			if(chartUrl == null || chartUrl.trim().isEmpty()){
//				return;
//			}
//			
			Resource resource = new Resource("http://localhost:9119/statistics/rest/stats");
			service = GWT.create(StatsRestService.class);
			((RestServiceProxy)service).setResource(resource);

			// Get column metadata
			getColumnNames();

			// Set viewsize and Refresh interval
//			int refreshInterval = scheduleIntervalSec;
//			try{
//				refreshInterval = Integer.parseInt((String)settingsController.getSetting(AmqRemoteSettingsDS.CHARTREFRESHINTERVAL));
//			}catch(NumberFormatException nfe){
//				logger.log(Level.WARNING, nfe.getMessage());
//			}
//			try{
//				slidingWinTime = Integer.parseInt((String)settingsController.getSetting(AmqRemoteSettingsDS.CHARTWINSIZE));
//			}catch(NumberFormatException nfe){
//				logger.log(Level.WARNING, nfe.getMessage());
//			}
//			schedule(Math.max(refreshInterval, 10), Math.max(slidingWinTime,10));
			
//			schedule(minSchedulePeriodInSec, slidingWinSize);
//			schedule(60, slidingWinSize);
			break;
		}
	}
}
