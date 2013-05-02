package com.shico.mnm.agg.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.google.gwt.visualization.client.formatters.ArrowFormat;
import com.google.gwt.visualization.client.formatters.ArrowFormat.Options;
import com.shico.mnm.agg.model.AggRemoteSettingsDS;
import com.shico.mnm.common.client.MonitorRestService;
import com.shico.mnm.common.event.DataEventType;
import com.shico.mnm.common.event.DataLoadedEvent;
import com.shico.mnm.common.event.DataLoadedEventHandler;
import com.shico.mnm.common.event.EventBus;
import com.shico.mnm.common.model.ListResult;
import com.shico.mnm.common.model.NestedList;
import com.shico.mnm.common.model.TARGET;

public class AggChartDataProviderImpl implements AggChartDataProvider, DataLoadedEventHandler {
	private final static Logger logger = Logger.getLogger("AggDataProviderImpl");
	
	String aggregatorAddress;
	DataTable data = null;
	DataTable vmdata = null;
	DataView processingTimeView = null;
	DataView loadView = null;
	DataView heapMemView = null;
	DataView nonHeapMemView = null;
	DataView sysLoadAvgView = null;
	DataView liveThreadView = null;
	
	MonitorRestService service;
	AggSettingsController settingsController;
	
	int fetchedRowIdx; // goes one ahead of fetched data. points to the next data-row to be fetched.
	
	static long timeSlice;	// time slice in milliseconds
	long timeSliceBase; 				// start time of the time slice
	int scheduleIntervalSec = 10;
	
	int slidingWinTime;  // Number of minutes in a sliding window 
	int slidingWinRows;	// Number of rows in a sliding window
	int vmSlidingWinRows;	// Number of rows in a sliding window
	static int slidingCur;		// cursor for the sliding window. How many windows have been slided to the left (disappeared from the view)	
	
	public AggChartDataProviderImpl() {
		super();

		settingsController = AggClientHandle.getAggSettingsController();

		EventBus.instance().addHandler(DataLoadedEvent.TYPE, this);
	}

	@Override
	public void getColumnNames(){
		if(data == null){
			data = DataTable.create();
			vmdata = DataTable.create();
			service.getColumnNames(TARGET.Aggregator.name(), new MethodCallback<ListResult<String>>() {
				public void onFailure(Method method, Throwable exception) {
					logger.log(Level.SEVERE, "Failed to get column metadata for aggregator."+ exception.getMessage());
					EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.FAILED_AGGREGATOR_METADATA_LOADED_EVENT));
				}
				public void onSuccess(Method method, ListResult<String> response) {
					logger.log(Level.INFO, "Retrieving column metadata info for aggregator. ");
					for (String st : response.getResult()) {
						if(st.startsWith("VM") || st.equals("time") || st.equals("utime")){
							vmdata.addColumn(ColumnType.NUMBER, st);
						}
						data.addColumn(ColumnType.NUMBER, st);
					}
					EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.AGGREGATOR_METADATA_LOADED_EVENT));
				}
			});
		}
	}
		
	
	@Override
	public void getRows(final long from, final long to) {
		final int rowIdx = fetchedRowIdx;

		service.getRows(TARGET.Aggregator.name(), from, to, new MethodCallback<NestedList<String>>() {
			
			public void onFailure(Method method, Throwable exception) {
				logger.log(Level.SEVERE, "Failed to get data rows. "+ exception.getMessage());
				EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.FAILED_AGGREGATOR_DATA_LOADED_EVENT));
			}

			public void onSuccess(Method method,
					NestedList<String> response) {
				logger.log(Level.INFO, "Retrieving rows from "+from+" to "+to);
				try{
					List<ListResult<String>> rows = response.getRows();
					for (ListResult<String> row : rows) {
						getData(row.getResult());
						getVMData(row.getResult());
						fetchedRowIdx++;
					}
					
					if(vmdata.getNumberOfRows() > vmSlidingWinRows){
						vmdata.removeRows(0, rows.size());
					}
					if(data.getNumberOfRows() > slidingWinRows){
						int toRemove = rows.size() / slidingWinRows;
						if(toRemove != 0){
							data.removeRows(0, toRemove);
							slidingCur += toRemove;
						}
					}
//					arrowFormat();
					Map<String, Object> info = new HashMap<String, Object>();
					info.put("from", rowIdx-rows.size());
					info.put("to", rowIdx);
					EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.AGGREGATOR_DATA_LOADED_EVENT, info));
				}catch(Exception e){
					logger.log(Level.SEVERE, "Exception while updating data. "+e.getMessage());
				}
			}
		});	
	}
	
	@Override
	public void getLastRow() {
		service.getLastRow(TARGET.Aggregator.name(), new MethodCallback<ListResult<String>>() {

			public void onFailure(Method method, Throwable exception) {
				logger.log(Level.SEVERE, "Failed to get data rows. "+ exception.getMessage());
				EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.FAILED_AGGREGATOR_DATA_LOADED_EVENT));
			}

			public void onSuccess(Method method,
					ListResult<String> response) {
				logger.log(Level.FINE, "Retrieving next data row.");
				try{
					List<String> row = response.getResult();
					getData(row);
					getVMData(row);
				
					fetchedRowIdx++;

					if(vmdata.getNumberOfRows() > vmSlidingWinRows){
						vmdata.removeRow(0);
					}
					if(data.getNumberOfRows() > slidingWinRows){
						data.removeRow(0);
						slidingCur++;
					}
					//				arrowFormat();
					EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.AGGREGATOR_DATA_LOADED_EVENT));
				}catch(Exception e){
					logger.log(Level.SEVERE, "Exception while updating data. "+e.getMessage());
				}
			}
		});
	}

	void getData(List<String> row){
		try{
			if(data.getNumberOfRows() == 0){
				data.addRow();
			}
			long timeCol = Long.valueOf(row.get(timeIdx));
			if(timeSliceBase == 0){
				timeSliceBase = timeCol;
			}else if((timeCol - timeSliceBase >= timeSlice)){
				data.addRow();
				timeSliceBase = timeCol;
			}
			int idx = data.getNumberOfRows();
			int colIdx = 0;
			for (String elem : row) {
				if(elem.startsWith("VM")){
					continue;
				}
				if(colIdx == utimeIdx){
					data.setValue(idx-1, colIdx, Long.valueOf(elem));
				}else if(elem.contains(".")){
					data.setValue(idx-1, colIdx, Float.valueOf(elem));
				}else{
					data.setValue(idx-1, colIdx, Integer.valueOf(elem));
				}
				colIdx++;
			}
		}catch(Exception e){
			logger.log(Level.SEVERE, "Exception while updating data. "+e.getMessage());
		}
	}
	
	void getVMData(List<String> row){
		try{
			if(row.isEmpty()){
				logger.log(Level.WARNING, "No row returned from the Aggregator Monitor.");
				return;
			}
			vmdata.addRow();
			int idx = vmdata.getNumberOfRows();
			vmdata.setValue(idx-1, 0, Long.valueOf(row.get(vmHeapMemIdx)));
			vmdata.setValue(idx-1, 1, Long.valueOf(row.get(vmNonHeapMemIdx)));
			vmdata.setValue(idx-1, 2, Integer.valueOf(row.get(vmThreadCountIdx)));
			vmdata.setValue(idx-1, 3, Float.valueOf(row.get(vmSysLoadAvgIdx)));
			vmdata.setValue(idx-1, 4, Integer.valueOf(row.get(timeIdx)));
			vmdata.setValue(idx-1, 5, Long.valueOf(row.get(utimeIdx)));
		}catch(Exception e){
			logger.log(Level.SEVERE, "Exception while updating vm-data. "+e.getMessage());
		}
	}
		
	@Override
	public DataView getProcessingTimeView() {
		processingTimeView = getNativeEventProcessingTimeView(DataView.create(data), data);
		return processingTimeView;
	}

	private native DataView getNativeEventProcessingTimeView(DataView view, DataTable data)/*-{
		view.setColumns([
			{calc:toTimeWindow, type:'string', label:'Time'},
			{sourceColumn:@com.shico.mnm.agg.client.AggChartDataProvider::numOfTotalExchangesIdx, label:'Number of Exchanges'},
			{sourceColumn:@com.shico.mnm.agg.client.AggChartDataProvider::totalProcessingTimeIdx, label:'Total Processing Time'},
			{sourceColumn:@com.shico.mnm.agg.client.AggChartDataProvider::lastProcessingTimeIdx, label:'Last Exchange Processing Time'},
			{sourceColumn:@com.shico.mnm.agg.client.AggChartDataProvider::maxProcessingTimeIdx, label:'Max Processing Time'},
			{sourceColumn:@com.shico.mnm.agg.client.AggChartDataProvider::minProcessingTimeIdx, label:'Min Processing Time'},
			{sourceColumn:@com.shico.mnm.agg.client.AggChartDataProvider::meanProcessingTimeIdx, label:'Mean Processing Time'}
			]);
		
		function toTimeWindow(dataTable, rowNum){
			$wnd.twim = @com.shico.mnm.agg.client.AggChartDataProviderImpl::timeSliceInMinutes()();
			var cursor = @com.shico.mnm.agg.client.AggChartDataProviderImpl::slidingCur;			
			var from = (rowNum+cursor) * $wnd.twim;
			var to = (rowNum+cursor+1) * $wnd.twim;
			return from+'-'+to+' m.';
		}
		
		return view;		
	}-*/;

	@Override
	public DataView getLoadView() {
		loadView = getNativeLoadView(DataView.create(data), data);
		return loadView;
	}

	private native DataView getNativeLoadView(DataView view, DataTable data)/*-{
		view.setColumns([
						{calc:toTimeWindow, type:'string', label:'Time'},
		     			{sourceColumn:@com.shico.mnm.agg.client.AggChartDataProvider::lastMinuteLoadIdx, label:'Last Minute Load'},
		     			{sourceColumn:@com.shico.mnm.agg.client.AggChartDataProvider::last5MinuteLoadIdx, label:'Last 5 Minutes Load'},
		     			{sourceColumn:@com.shico.mnm.agg.client.AggChartDataProvider::last15MinuteLoadIdx, label:'Last 15 minutes Load'}
		     			]);

		function toTimeWindow(dataTable, rowNum){
			$wnd.twim = @com.shico.mnm.agg.client.AggChartDataProviderImpl::timeSliceInMinutes()();
			var cursor = @com.shico.mnm.agg.client.AggChartDataProviderImpl::slidingCur;			
			var from = (rowNum+cursor) * $wnd.twim;
			var to = (rowNum+cursor+1) * $wnd.twim;
			return from+'-'+to+' m.';
		}
		     		
   		return view;		
  	}-*/;

	@Override
	public DataView getHeapMemView(){
		heapMemView = getHeapMemView(DataView.create(vmdata), vmdata);
		return heapMemView;
	}
	
	private native DataView getHeapMemView(DataView view, DataTable data)/*-{
		view.setColumns([{calc:toMinutes, type:'number', label:'Time'}, {calc:heapMemtoMB, type:'number', label:'Memory in MB'}]);
		
		function toMinutes(dataTable, rowNum){
			return dataTable.getValue(rowNum, 4) / 60000;
		}
		function heapMemtoMB(dataTable, rowNum){
			return dataTable.getValue(rowNum, 0) / 1048576;
		}
		
		return view;		
	}-*/;
	
	@Override
	public DataView getNonHeapMemView(){
		nonHeapMemView = getNonHeapMemView(DataView.create(vmdata), vmdata);
		return nonHeapMemView;
	}
	
	private native DataView getNonHeapMemView(DataView view, DataTable data)/*-{
		view.setColumns([{calc:toMinutes, type:'number', label:'Time'}, {calc:nonHeapMemtoMB, type: 'number', label:'Memory in MB'}]);
		
		function toMinutes(dataTable, rowNum){
			return dataTable.getValue(rowNum, 4) / 60000;
		}
		function nonHeapMemtoMB(dataTable, rowNum){
			return dataTable.getValue(rowNum, 1) / 1048576;
		}
		
		return view;		
	}-*/;
	
	@Override
	public DataView getSysLoadAvgView(){
		sysLoadAvgView = getSysLoadAvgView(DataView.create(vmdata), vmdata); 
		return sysLoadAvgView;
	}
	
	private native DataView getSysLoadAvgView(DataView view, DataTable data)/*-{
		view.setColumns([{calc:toMinutes, type:'number', label:'Time'}, {sourceColumn:3, label:'Load Average'}]);
		
		function toMinutes(dataTable, rowNum){
			return dataTable.getValue(rowNum, 4) / 60000;
		}
		
		return view;
	}-*/;
	
	@Override
	public DataView getLiveThreadView(){
		liveThreadView = getNativeLiveThreadView(DataView.create(vmdata), vmdata); 
		return liveThreadView;
	}
	
	private native DataView getNativeLiveThreadView(DataView view, DataTable data)/*-{
		view.setColumns([{calc:toMinutes, type:'number', label:'Time'}, {sourceColumn:2 , label:'Live Thread Count'}]);
		
		function toMinutes(dataTable, rowNum){
			return dataTable.getValue(rowNum, 0) / 60000;
		}
		
		return view;		
	}-*/;

	@Override
	public AbstractDataTable getDataTable() {
		return data;
	}
	
	private static double prevMax, prevMin, prevMean;
	void arrowFormat(){
		int[] columns = new int[]{maxProcessingTimeIdx, minProcessingTimeIdx, meanProcessingTimeIdx};
		double base = 0;
		Options options = Options.create();
		for (int colIdx : columns) {			
			switch(colIdx){
			case maxProcessingTimeIdx:			
				base = prevMax;
				break;
			case minProcessingTimeIdx:
				base = prevMin;
				break;
			case meanProcessingTimeIdx:
				base = prevMean;
			}
			options.setBase(base);
			ArrowFormat af = ArrowFormat.create(options);
			af.format(data, colIdx);
		}
		
		prevMax = data.getValueInt(data.getNumberOfRows()-1, maxProcessingTimeIdx);
		prevMin = data.getValueInt(data.getNumberOfRows()-1, minProcessingTimeIdx);
		prevMean = data.getValueInt(data.getNumberOfRows()-1, meanProcessingTimeIdx);		
	}
	
	public static int timeSliceInMinutes(){
		return (int)(timeSlice / 60000);
	}
	
	Timer timer;
	@Override
	public void schedule(final int scheduleIntervalSec, int viewWindowInMin) {
		timeSlice = (viewWindowInMin / 5) * 60000; // each sliding window will contain up to five data-rows
		if(timer != null){
			timer.cancel();
		}
		timer = new Timer() {
			public void run() {
				getLastRow();
			}
		};
		timer.scheduleRepeating(scheduleIntervalSec*1000);
		slidingWinRows = (int)((viewWindowInMin * 60000) / timeSlice);
		vmSlidingWinRows = (viewWindowInMin * 60) / scheduleIntervalSec;
	}

	@Override
	public void onDataLoaded(DataLoadedEvent event) {
		switch (event.eventType) {
		case AGGREGATOR_METADATA_LOADED_EVENT:
			// get rows for the last 20 seconds
			long now = System.currentTimeMillis();
			getRows(now - (60*1000), now);
			break;
		case AGG_CHART_SETTINGS_CHANGED_EVENT:
			String chartUrl = (String)event.info.get(AggRemoteSettingsDS.CHARTURL);
			if(chartUrl == null || chartUrl.trim().isEmpty()){
				return;
			}
			
//			Resource resource = new Resource("http://127.0.0.1:9119/statistics/rest/monitor");
			Resource resource = new Resource(chartUrl);
			service = GWT.create(MonitorRestService.class);
			((RestServiceProxy)service).setResource(resource);
			
			
			getColumnNames();

			// Set viewsize and Refresh interval
			int refreshInterval = scheduleIntervalSec;
			try{
				refreshInterval = Integer.parseInt((String)settingsController.getSetting(AggRemoteSettingsDS.CHARTREFRESHINTERVAL));
			}catch(NumberFormatException nfe){
				logger.log(Level.WARNING, nfe.getMessage());
			}
			int winSize = slidingWinTime;
			try{
				winSize = Integer.parseInt((String)settingsController.getSetting(AggRemoteSettingsDS.CHARTWINSIZE));
			}catch(NumberFormatException nfe){
				logger.log(Level.WARNING, nfe.getMessage());
			}
			schedule(Math.max(refreshInterval, 10), Math.max(winSize,30));
			
//			schedule(10, 30);
			break;	
		}
	}
}
