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
	
	public void getRows(long from, long to) {
		logger.log(Level.INFO, "Requesting records from "+from+" to "+to);
		service.getViewPage(from, to, new MethodCallback<NestedList<Object>>() {
			@Override
			public void onFailure(Method method, Throwable exception) {
				callbackOnFailure(method, exception);
			}
			@Override
			public void onSuccess(Method method, NestedList<Object> response) {
				callbackOnSuccess(method, response);
			}
		});
	}

	public void getRows(String from, String to){
		logger.log(Level.INFO, "Requesting records from "+from+" to "+to);
		service.getViewPage(from, to, new MethodCallback<NestedList<Object>>() {
			@Override
			public void onFailure(Method method, Throwable exception) {
				callbackOnFailure(method, exception);
			}
			@Override
			public void onSuccess(Method method, NestedList<Object> response) {
				callbackOnSuccess(method, response);
			}
		});
	}
		
	// eventtype is our events (StatsChartDataProvider's statsEvents and subStatsEvents, while the type is the back-ends
	// stat events (only StatsChartDataProvider's statsEvents).
	public void getRows(final String eventtype, final long from, final long to) {
		final String type = eventtype.contains(".") ? eventtype.split("\\.")[0] : eventtype;
		logger.log(Level.INFO, "Requesting "+type+" records from "+from+" to "+to);
		service.getViewPage(type, from, to, new MethodCallback<NestedList<Object>>() {
			@Override
			public void onFailure(Method method, Throwable exception) {
				callbackOnFailure(method, exception);
			}
			@Override
			public void onSuccess(Method method, NestedList<Object> response) {
				callbackOnSuccess(method, response, eventtype);
			}
		});
	}

	public void getRows(final String eventtype, final String from, final String to) {
		final String type = eventtype.contains(".") ? eventtype.split("\\.")[0] : eventtype;
		logger.log(Level.INFO, "Requesting "+type+" records from "+from+" to "+to);
		service.getViewPage(type, from, to, new MethodCallback<NestedList<Object>>() {
			@Override
			public void onFailure(Method method, Throwable exception) {
				callbackOnFailure(method, exception);
			}
			@Override
			public void onSuccess(Method method, NestedList<Object> response) {
				callbackOnSuccess(method, response, eventtype);
			}
		});
	}

	public void getRows(final String eventtype, final String from, final String to, String options) {
		final String type = eventtype.contains(".") ? eventtype.split("\\.")[0] : eventtype;
		logger.log(Level.INFO, "Requesting "+type+" records from "+from+" to "+to);
		service.getViewPage(type, from, to, options, new MethodCallback<NestedList<Object>>() {
			@Override
			public void onFailure(Method method, Throwable exception) {
				callbackOnFailure(method, exception);
			}
			@Override
			public void onSuccess(Method method, NestedList<Object> response) {
				callbackOnSuccess(method, response, eventtype);
			}
		});
	}

	public void getRows(final String eventtype, final long from, final long to, String options) {
		final String type = eventtype.contains(".") ? eventtype.split("\\.")[0] : eventtype;
		logger.log(Level.INFO, "Requesting "+type+" records from "+from+" to "+to);
		service.getViewPage(type, from, to, options, new MethodCallback<NestedList<Object>>() {
			@Override
			public void onFailure(Method method, Throwable exception) {
				callbackOnFailure(method, exception);
			}
			@Override
			public void onSuccess(Method method, NestedList<Object> response) {
				callbackOnSuccess(method, response, eventtype);
			}
		});
	}

	// eventtype is our events (StatsChartDataProvider's statsEvents and subStatsEvents, while the type is the back-ends
	// stat events (only StatsChartDataProvider's statsEvents).
	public void getRowsInBatch(final String eventtype, final String from, final String to, String options) {
		final String type = eventtype.contains(".") ? eventtype.split("\\.")[0] : eventtype;
		logger.log(Level.INFO, "Batch requesting "+type+" records from "+from+" to "+to);
		service.getViewPageInBatch(type, from, to, options, new MethodCallback<ListResult<Object>>() {
			@Override
			public void onFailure(Method method, Throwable exception) {
				callbackOnFailure(method, exception);
			}
			@Override
			public void onSuccess(Method method,
					ListResult<Object> response) {
				try{
					// The response is actually a ListResult<NestedList<Object>>, but since the gwt compiler for some
					// reason cannot compile it, then we have to do the unmarshalling/decoding manually.
					List<Object> elements = response.getResult();
					if(elements.isEmpty()){
						String err = "No row returned from Statistics Rest Service.";
						logger.log(Level.WARNING, err);
						fireErrorEvent(DataEventType.FAILED_STATS_DATA_LOADED_EVENT, err);
						return;
					}
					
					DataTable data = getData(eventtype);
					for (Object el : elements) {
						// el is in fact a NestedList, which basically is a json-map containing "controlData" and "rows"
						// elements. The "rows" element is then a list of maps. Each map is in turn contains of one
						// single element "result" and a json-list.
						Map<String, Object> elem = (Map<String, Object>)el;
						List<Map<String, Object>> rows = (List<Map<String, Object>>)elem.get("rows");
						if(rows == null || rows.isEmpty()){
							continue;
						}
						
						int idx = data.getNumberOfRows();
						data.addRows(rows.size());
						for (Map<String, Object> row : rows) {
							int colIdx = 0;
							List<Object> record = (List<Object>)row.get("result");
							for (Object rec : record) {		
								if(data.getColumnType(colIdx) == ColumnType.STRING){
									data.setValue(idx, colIdx, rec == null ? null : rec.toString());
								}else{
									data.setValue(idx, colIdx, (Double)rec);
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
					postUpdate(data);
					fireDataLoadedEvent(DataEventType.STATS_DATA_LOADED_EVENT, eventtype);					
					
				}catch(Exception e){
					logger.log(Level.SEVERE, "Exception while updating data. "+e.getMessage());
				}
			}
		});
	}

	private void callbackOnFailure(Method method, Throwable exception) {
		String err = "Failed to get data rows. "+ exception.getMessage();
		logger.log(Level.SEVERE, err);
		fireErrorEvent(DataEventType.FAILED_STATS_DATA_LOADED_EVENT, err);
	}

	private void callbackOnSuccess(Method method, NestedList<Object> response) {
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

	private void callbackOnSuccess(Method method, NestedList<Object> response, final String type) {
		try{
			List<ListResult<Object>> page = response.getRows();
			if(page.isEmpty()){
				logger.log(Level.WARNING, "No row returned from Statistics Rest Service.");
				return;
			}
			
			DataTable data = getData(type);
			
			handleData(data, page);
			
			postUpdate(data);
			
			fireDataLoadedEvent(DataEventType.STATS_DATA_LOADED_EVENT, type);					
		}catch(Exception e){
			logger.log(Level.SEVERE, "Exception while updating data. "+e.getMessage());
		}		
	}
		
	private void handleData(DataTable data, List<ListResult<Object>> page){
		int idx = data.getNumberOfRows();
		data.addRows(page.size());
		for (ListResult<Object> row : page) {
			int colIdx = 0;
			List<Object> rec = row.getResult();
			for (Object elem : rec) {		
				if(data.getColumnType(colIdx) == ColumnType.STRING){					
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
				'column': @com.shico.mnm.stats.client.StatsChartDataProvider::durationIdx, 
				'aggregation': $wnd.google.visualization.data.sum, 
				'type': 'number',
				'label': 'viewedMillis'
			},
			{
				'column': @com.shico.mnm.stats.client.StatsChartDataProvider::viewersIdx, 
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
	
	@Deprecated
	public void getRowsExponatially(long from, long to){
		getRowsExponatially(null, from, to);
	}

	@Deprecated
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
	protected native AbstractDataTable getTopProgramsView(DataTable data)/*-{
		var view = new $wnd.google.visualization.DataView(data);
		view.setColumns([
			{sourceColumn:@com.shico.mnm.stats.client.StatsChartDataProvider::titleIdx, id: 'ID', type: 'string', label:'Program'}, 
			{calc:toHoursAndMinutes, type:'number', label:'Hours'}, 
			{sourceColumn: @com.shico.mnm.stats.client.StatsChartDataProvider::viewersIdx, type: 'number', label:'Viewers'},
			{sourceColumn:@com.shico.mnm.stats.client.StatsChartDataProvider::nameIdx, type: 'string', label:'Channel'},
			{calc:getWeightedPopularity, type:'number', label:'Popularity metric', id:'PopularityMetric'}
		]);
		
		function getWeightedPopularity(dataTable, rowNum){
			return dataTable.getNumberOfRows()-rowNum;
		}
		
		function toHoursAndMinutes(dataTable, rowNum){
			return Math.round(dataTable.getValue(rowNum, @com.shico.mnm.stats.client.StatsChartDataProvider::durationIdx) / 36000) / 100;
		}
		
		return view;
	}-*/;
	
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
				'column': @com.shico.mnm.stats.client.StatsChartDataProvider::durationIdx, 
				'aggregation': $wnd.google.visualization.data.sum, 
				'type': 'number',
				'label': 'viewedMillis'
			},
			{
				'column': @com.shico.mnm.stats.client.StatsChartDataProvider::viewersIdx, 
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
