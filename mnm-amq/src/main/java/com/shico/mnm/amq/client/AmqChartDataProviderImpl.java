package com.shico.mnm.amq.client;

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
import com.google.gwt.visualization.client.formatters.NumberFormat;
import com.shico.mnm.amq.model.AmqRemoteSettingsDS;
import com.shico.mnm.common.client.MonitorRestService;
import com.shico.mnm.common.event.DataEventType;
import com.shico.mnm.common.event.DataLoadedEvent;
import com.shico.mnm.common.event.DataLoadedEventHandler;
import com.shico.mnm.common.event.EventBus;
import com.shico.mnm.common.model.ListResult;
import com.shico.mnm.common.model.NestedList;
import com.shico.mnm.common.model.TARGET;

public class AmqChartDataProviderImpl implements AmqChartDataProvider, DataLoadedEventHandler {
	private static Logger logger = Logger.getLogger("AmqChartDataProviderImpl");
	
	String brokerAddress;
	DataTable data = null;
	DataView enqDeqInfView = null;
	DataView avgEnqTimeView = null;
	DataView memUsageView = null;
	DataView diskUsageView = null;
	MonitorRestService service;
	int fetchedRowIdx;
	int scheduleIntervalSec = 10;
	int slidingWinTime = 10; // Number of minutes in a sliding window 
	int slidingWinRows; // Number of rows in a sliding window
	boolean updateView;
	AmqSettingsController settingsController;
			
	public AmqChartDataProviderImpl() {
		super();
		
		settingsController = AmqClientHandle.getAmqSettingsController();

		EventBus.instance().addHandler(DataLoadedEvent.TYPE, this);
	}

	@Override
	public void getColumnNames(){
		if(data == null){
			data = DataTable.create();
			service.getColumnNames(TARGET.ActiveMQ.name(), new MethodCallback<ListResult<String>>() {
				public void onFailure(Method method, Throwable exception) {
					logger.log(Level.SEVERE, "Failed to get column metadata."+ exception.getMessage());
					EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.FAILED_BROKER_METADATA_LOADED_EVENT));
				}
				public void onSuccess(Method method, ListResult<String> response) {
					logger.log(Level.INFO, "Retrieving column metadata info. ");
					for (String st : response.getResult()) {
						data.addColumn(ColumnType.NUMBER, st);	
					}
					EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.BROKER_METADATA_LOADED_EVENT));
				}
			});
		}
	}
	
	NumberFormat timeFormatter = null;
	private void formatColumns(){
		if(timeFormatter == null){
			com.google.gwt.visualization.client.formatters.NumberFormat.Options options = 
					com.google.gwt.visualization.client.formatters.NumberFormat.Options.create();
			options.setFractionDigits(0);
			timeFormatter = NumberFormat.create(options);
		}	
		timeFormatter.format(data, timeIdx);
	}
	
	@Override
	public void getRows(final long from, final long to){
		service.getRows(TARGET.ActiveMQ.name(), from, to, new MethodCallback<NestedList<String>>() {
			
			public void onFailure(Method method, Throwable exception) {
				logger.log(Level.SEVERE, "Failed to get data rows. "+ exception.getMessage());
				EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.FAILED_BROKER_DATA_LOADED_EVENT));
			}

			public void onSuccess(Method method,
					NestedList<String> response) {
				logger.log(Level.INFO, "Retrieving rows from "+from+" - "+to);
				try{
					List<ListResult<String>> rows = response.getRows();
					int idx = data.getNumberOfRows();
					int size = rows.size();
					if(size == 0){
						logger.log(Level.WARNING, "No rows returned for period "+from+" - "+to);
						return;
					}
					data.addRows(size);
					fetchedRowIdx = fetchedRowIdx+size;
					for (ListResult<String> row : rows) {
						int colIdx = 0;
						for (String elem : row.getResult()) {	
							if(colIdx == utimeIdx){
								data.setValue(idx, colIdx, Long.valueOf(elem));
							}else if(elem.contains(".")){
								data.setValue(idx, colIdx, Float.valueOf(elem));
							}else{
								data.setValue(idx, colIdx, Integer.valueOf(elem));
							}
							colIdx++;
						}
						idx++;
					}
					if(data.getNumberOfRows() > slidingWinRows){
						data.removeRows(0, size);
						updateView = true;
					}
					Map<String, Object> info = new HashMap<String, Object>();
					info.put("from", idx-rows.size());
					info.put("to", idx);
					EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.BROKER_DATA_LOADED_EVENT, info));
				}catch(Exception e){
					logger.log(Level.SEVERE, "Exception while updating data. "+e.getMessage());
				}
			}
		});		
	}
	
	@Override
	public void getLastRow(){
		service.getLastRow(TARGET.ActiveMQ.name(), new MethodCallback<ListResult<String>>() {
			
			public void onFailure(Method method, Throwable exception) {
				logger.log(Level.SEVERE, "Failed to get data rows. "+ exception.getMessage());
				EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.FAILED_BROKER_DATA_LOADED_EVENT));
			}

			public void onSuccess(Method method,
					ListResult<String> response) {
				try{
					List<String> row = response.getResult();
					if(row.isEmpty()){
						logger.log(Level.WARNING, "No row returned from ActiveMQ monitor.");
						return;
					}
					int idx = data.getNumberOfRows();
					data.addRows(1);
					fetchedRowIdx++;
					int colIdx = 0;
					for (String elem : row) {
						if(colIdx == utimeIdx){
							data.setValue(idx, colIdx, Long.valueOf(elem));
						}else if(elem.contains(".")){
							data.setValue(idx, colIdx, Float.valueOf(elem));
						}else{
							data.setValue(idx, colIdx, Integer.valueOf(elem));
						}
						colIdx++;
					}
					if(data.getNumberOfRows() > slidingWinRows){
						data.removeRows(0, 1);
						updateView = true;
					}
					EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.BROKER_DATA_LOADED_EVENT));
				}catch(Exception e){
					logger.log(Level.SEVERE, "Exception while updating data. "+e.getMessage());
				}
			}
		});		
	}
				
	public DataView getEnqDeqInflView(){
		if(enqDeqInfView == null || updateView){
			enqDeqInfView = getNativeEnqDeqInflView(DataView.create(data), data);
		}
		return enqDeqInfView;
	}
	
	private native DataView getNativeEnqDeqInflView(DataView view, DataTable data)/*-{
		view.setColumns([{calc:toMinutes, type:'number', label:'Time'}, {calc:enqThousands, type:'number', label:'Enqueue count'},{calc:deqThousands, type:'number', label:'Dequeue count'},{calc:infThousands, type:'number', label:'Inflight count'}]);
		
		function toMinutes(dataTable, rowNum){
			return dataTable.getValue(rowNum, @com.shico.mnm.amq.client.AmqChartDataProvider::timeIdx) / 60000;
		}
		function enqThousands(dataTable, rowNum){
			return dataTable.getValue(rowNum, @com.shico.mnm.amq.client.AmqChartDataProvider::enqIdx) / 1000;
		}
		function deqThousands(dataTable, rowNum){
			return dataTable.getValue(rowNum, @com.shico.mnm.amq.client.AmqChartDataProvider::deqIdx) / 1000;
		}
		function infThousands(dataTable, rowNum){
			return dataTable.getValue(rowNum, @com.shico.mnm.amq.client.AmqChartDataProvider::inflIdx) / 1000;
		}
		
		return view;		
	}-*/;
	
	public DataView getAvgEnqTimeView(){
		if(avgEnqTimeView == null || updateView){
			avgEnqTimeView = getNativeAvgEnqTimeView(DataView.create(data), data);
		}
		return avgEnqTimeView;
	}
	
	private native DataView getNativeAvgEnqTimeView(DataView view, DataTable data)/*-{
		view.setColumns([{calc:toMinutes, type:'number', label:'Time'}, {calc:enqSeconds, type: 'number', label:'Avgerage enqueue time'}]);
		
		function toMinutes(dataTable, rowNum){
			return dataTable.getValue(rowNum, @com.shico.mnm.amq.client.AmqChartDataProvider::timeIdx) / 60000;
		}
		function enqSeconds(dataTable, rowNum){
			return dataTable.getValue(rowNum, @com.shico.mnm.amq.client.AmqChartDataProvider::enqIdx) / 1000;
		}
		
		return view;		
	}-*/;
	
	public DataView getMemUsageView(){
		if(memUsageView == null || updateView){
			memUsageView = getMemUsageView(DataView.create(data), data); 
		}
		return memUsageView;
	}
	
	private native DataView getMemUsageView(DataView view, DataTable data)/*-{
		view.setColumns(
		[{calc:toMinutes, type:'number', label:'Time'}, 
		{calc:toMBytes, type:'number', label:'Memory usage'}]);
		
		function toMinutes(dataTable, rowNum){
			return dataTable.getValue(rowNum, @com.shico.mnm.amq.client.AmqChartDataProvider::timeIdx) / 60000;
		}
		function toMBytes(dataTable, rowNum){
			return dataTable.getValue(rowNum, @com.shico.mnm.amq.client.AmqChartDataProvider::memUsageIdx) / 1048576;
		}
		
		return view;
	}-*/;
	
	public DataView getDiskUsageView(){
		if(diskUsageView == null || updateView){
			diskUsageView = getDiskUsageView(DataView.create(data), data); 
		}
		return diskUsageView;
	}
	
	private native DataView getDiskUsageView(DataView view, DataTable data)/*-{
		view.setColumns([{calc:toMinutes, type:'number', label:'Time'}, {calc:toMBytes, type:'number', label:'Disk space usage'}]);
		
		function toMinutes(dataTable, rowNum){
			return dataTable.getValue(rowNum, @com.shico.mnm.amq.client.AmqChartDataProvider::timeIdx) / 60000;
		}
		function toMBytes(dataTable, rowNum){
			return dataTable.getValue(rowNum, @com.shico.mnm.amq.client.AmqChartDataProvider::storeUsageIdx) / 1048576;
		}
		
		return view;		
	}-*/;

	@Override
	public int getConsumerCount() {
		return data.getValueInt(data.getNumberOfRows()-1, consCnt);	
	}

	@Override
	public int getProducerCount() {
		return data.getValueInt(data.getNumberOfRows()-1, prodCnt);	
	}

	@Override
	public AbstractDataTable getDataTable() {
		return data;
	}

	Timer timer;
	@Override
	public void schedule(final int schedulePeriodInSec, int viewWindowInMin) {
		if(timer != null){
			timer.cancel();
		}
		timer = new Timer() {
			public void run() {
				getLastRow();
			}
		};
		timer.scheduleRepeating(schedulePeriodInSec*1000);
		slidingWinRows = (viewWindowInMin * 60) / schedulePeriodInSec;		
	}

	@Override
	public void onDataLoaded(DataLoadedEvent event) {
		switch (event.eventType) {
		case BROKER_METADATA_LOADED_EVENT:
			// get rows for the last 20 seconds
			long now = System.currentTimeMillis();
			getRows(now - (60*1000), now);
			break;
		case AMQ_CHART_SETTINGS_CHANGED_EVENT:
			String chartUrl = (String)event.info.get(AmqRemoteSettingsDS.CHARTURL);
			if(chartUrl == null || chartUrl.trim().isEmpty()){
				return;
			}
			
			Resource resource = new Resource(chartUrl);
			service = GWT.create(MonitorRestService.class);
			((RestServiceProxy)service).setResource(resource);

			// Get column metadata
			getColumnNames();

			// Set viewsize and Refresh interval
			int refreshInterval = scheduleIntervalSec;
			try{
				refreshInterval = Integer.parseInt((String)settingsController.getSetting(AmqRemoteSettingsDS.CHARTREFRESHINTERVAL));
			}catch(NumberFormatException nfe){
				logger.log(Level.WARNING, nfe.getMessage());
			}
			try{
				slidingWinTime = Integer.parseInt((String)settingsController.getSetting(AmqRemoteSettingsDS.CHARTWINSIZE));
			}catch(NumberFormatException nfe){
				logger.log(Level.WARNING, nfe.getMessage());
			}
			schedule(Math.max(refreshInterval, 10), Math.max(slidingWinTime,10));
			break;
		}
	}

	public int getScheduleIntervalSec() {
		return scheduleIntervalSec;
	}

	public void setScheduleIntervalSec(int scheduleIntervalSec) {
		this.scheduleIntervalSec = scheduleIntervalSec;
	}
	
}
