package com.shico.mnm.stats.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import com.google.gwt.user.client.Timer;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.shico.mnm.common.event.DataEventType;
import com.shico.mnm.common.event.DataLoadedEvent;
import com.shico.mnm.common.event.DataLoadedEventHandler;
import com.shico.mnm.common.event.EventBus;
import com.shico.mnm.common.model.ListResult;
import com.shico.mnm.common.model.NestedList;

public abstract class AbstractStatsChartDataProvider implements DataLoadedEventHandler {
	private static Logger logger = Logger.getLogger("AbstractStatsChartDataProvider");
	
	protected abstract void setData(DataTable data);
	protected abstract void postUpdate(DataTable data);
	protected abstract DataTable getData(String type);
	protected abstract void fireEvent(DataEventType eventtype, Map<String, Object> eventInfo);
	
	StatsRestService service;
	protected int slidingWinSize = 1000000; // number of rows to keep/draw
	protected StatsSettingsController settingsController;
	
	public AbstractStatsChartDataProvider() {
		super();		
		
		settingsController = StatsClientHandle.getStatsSettingsController();
		
		EventBus.instance().addHandler(DataLoadedEvent.TYPE, this);
	}
	
	public void getColumnNames() {
		service.getViewColumns(new MethodCallback<ListResult<String>>() {
			public void onFailure(Method method, Throwable exception) {
				String err = "Failed to get column metadata."+ exception.getMessage();
				logger.log(Level.SEVERE, err);
				fireErrorEvent(DataEventType.FAILED_STATS_METADATA_LOADED_EVENT, err);
			}
			public void onSuccess(Method method, ListResult<String> response) {
				DataTable data = DataTable.create();
				logger.log(Level.INFO, "Retrieving column metadata info. ");
				List<String> result = response.getResult();
				data.addColumn(ColumnType.STRING, result.get(StatsChartDataProvider.typeIdx));
				data.addColumn(ColumnType.STRING, result.get(StatsChartDataProvider.nameIdx));
				data.addColumn(ColumnType.STRING, result.get(StatsChartDataProvider.titleIdx));
				data.addColumn(ColumnType.NUMBER, result.get(StatsChartDataProvider.sumIdx));
				data.addColumn(ColumnType.NUMBER, result.get(StatsChartDataProvider.minDurationIdx));
				data.addColumn(ColumnType.NUMBER, result.get(StatsChartDataProvider.maxDurationIdx));
				data.addColumn(ColumnType.NUMBER, result.get(StatsChartDataProvider.totalDurationIdx));
				data.addColumn(ColumnType.NUMBER, result.get(StatsChartDataProvider.fromIdx));
				data.addColumn(ColumnType.NUMBER, result.get(StatsChartDataProvider.toIdx));

				setData(data);
				
				fireEvent(DataEventType.STATS_METADATA_LOADED_EVENT, null);
			}
		});
	}

	public void getRows(long from, long to) {
		logger.log(Level.INFO, "Requesting records from "+from+" to "+to);
		service.getViewRows(from, to, new MethodCallback<NestedList<Object>>() {
			
			public void onFailure(Method method, Throwable exception) {
				String err = "Failed to get data rows. "+ exception.getMessage();
				logger.log(Level.SEVERE, err);
				fireErrorEvent(DataEventType.FAILED_STATS_DATA_LOADED_EVENT, err);
			}

			public void onSuccess(Method method, NestedList<Object> response) {
				try{
					List<ListResult<Object>> page = response.getRows();
					if(page.isEmpty()){
						String err = "No row returned from Statistics Rest Service.";
						logger.log(Level.WARNING, err);
						fireErrorEvent(DataEventType.FAILED_STATS_DATA_LOADED_EVENT, err);
						return;
					}
					
					DataTable data = getData(getStatsEventType(page));
					
					handleData(data, page);
					
					postUpdate(data);
					
					fireEvent(DataEventType.STATS_DATA_LOADED_EVENT, null);					
				}catch(Exception e){
					logger.log(Level.SEVERE, "Exception while updating data. "+e.getMessage());
				}
			}
		});
	}

	public void getRows(final String type, final long from, final long to) {
		logger.log(Level.INFO, "Requesting "+type+" records from "+from+" to "+to);
		service.getViewRows(type, from, to, new MethodCallback<NestedList<Object>>() {
			
			public void onFailure(Method method, Throwable exception) {
				String err = "Failed to get data rows. "+ exception.getMessage();
				logger.log(Level.SEVERE, err);
				fireErrorEvent(DataEventType.FAILED_STATS_DATA_LOADED_EVENT, err);
			}

			public void onSuccess(Method method, NestedList<Object> response) {
				try{
					List<ListResult<Object>> page = response.getRows();
					if(page.isEmpty()){
						logger.log(Level.WARNING, "No row returned from Statistics Rest Service.");
						return;
					}
					
					String fetchedType = getStatsEventType(page);
					if(!type.equals(fetchedType)){
						String err = "Expected data for "+type+", but received data for "+fetchedType;
						fireErrorEvent(DataEventType.FAILED_STATS_DATA_LOADED_EVENT, err);
					}
					DataTable data = getData(type);
					
					handleData(data, page);
					
					postUpdate(data);
					
					fireDataLoadedEvent(DataEventType.STATS_DATA_LOADED_EVENT, type);					
				}catch(Exception e){
					logger.log(Level.SEVERE, "Exception while updating data. "+e.getMessage());
				}
			}
		});
	}

	private void handleData(DataTable data, List<ListResult<Object>> page){
		int idx = data.getNumberOfRows();
		data.addRows(page.size());
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
		if(data.getNumberOfRows() > slidingWinSize){
			data.removeRows(0, data.getNumberOfRows()-slidingWinSize);
		}
	}
	
	private String getStatsEventType(List<ListResult<Object>> rows){
		List<Object> rec = rows.get(0).getResult();
		return (String)rec.get(StatsChartDataProvider.typeIdx);
	}
	
	protected native DataTable getGroupedData(DataTable data)/*-{
		return $wnd.google.visualization.data.group(
			data, 
			[@com.shico.mnm.stats.client.StatsChartDataProvider::typeIdx,
			@com.shico.mnm.stats.client.StatsChartDataProvider::nameIdx],
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
				'label': 'sum'
			}]
		);
	}-*/;
	
	public void schedule(int millis, int viewWindowMinutes) {
	}
	
	public AbstractDataTable getDataTable() {
		return null;
	}

	protected void fireDataLoadedEvent(DataEventType type, String statsType){
		Map<String, Object> info = new HashMap<String, Object>();
		info.put(StatsChartDataProvider.STATS_EVENT_TYPE, statsType);
		fireEvent(type, info);		
	}
	
	protected void fireErrorEvent(DataEventType type, String err){
		Map<String, Object> info = new HashMap<String, Object>();
		info.put(DataLoadedEvent.ERROR_KEY, err);
		fireEvent(type, info);
	}
	
	public void getRowsExponatially(long from, long to){
		getRowsExponatially(null, from, to);
	}

	public void getRowsExponatially(String eventType, long from, long to){
		if(jobDealer != null){
			jobDealer.cancel();
		}
		jobDealer = new JobDealer(from, to, eventType);
		jobDealer.scheduleRepeating(150);		
	}

	private JobDealer jobDealer;
	class JobDealer extends Timer{
		final long initialChunckSizeInMillis = 3600000;
		final long maxChunkSizeInMillis = 8*3600000;
		long chunkSizeInMillis = initialChunckSizeInMillis;
		long lastFrom, lastTo;
		final long start, end;
		String eventType;

		public JobDealer(final long start, final long end) {
			super();
			this.end = end;
			this.start = start;
			lastFrom = start;
			lastTo = lastFrom+initialChunckSizeInMillis;
		}

		public JobDealer(final long start, final long end, final String eventType) {
			this(start, end);
			this.eventType = eventType;
		}

		@Override
		public void run() {
			if(lastFrom < end && lastTo > lastFrom){
				if(eventType != null){
					getRows(eventType, lastFrom, lastTo);
				}else{
					getRows(lastFrom, lastTo);
				}
				lastFrom = lastTo;
				chunkSizeInMillis = chunkSizeInMillis*2;
				lastTo = lastTo+Math.min(chunkSizeInMillis, maxChunkSizeInMillis);
			}else{
				logger.log(Level.INFO, "No more data to fetch. Cancelling JobDealer.");
				this.cancel();
				return;
			}
		}
		
	}

	// ========================= native tables/views ========================= 
	// =======================================================================
	
	protected native AbstractDataTable getMostPopularProgramsView(DataTable data, double from, double to)/*-{
		var view = new $wnd.google.visualization.DataView(data);
		if(from <= 0 && to <= 0){
			var rowIdxs = data.getFilteredRows([{column: @com.shico.mnm.stats.client.StatsChartDataProvider::typeIdx, value: 'LiveUsage'}]);
		}else{
			var rowIdxs = data.getFilteredRows(
				[
				{column: @com.shico.mnm.stats.client.StatsChartDataProvider::typeIdx, value: 'LiveUsage'},
				{column: @com.shico.mnm.stats.client.StatsChartDataProvider::fromIdx, minValue: from},
				{column: @com.shico.mnm.stats.client.StatsChartDataProvider::toIdx, maxValue: to}
				]);
		}
		
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
 
}
