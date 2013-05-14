package com.shico.mnm.stats.client;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.shico.mnm.common.event.DataEventType;
import com.shico.mnm.common.event.DataLoadedEvent;
import com.shico.mnm.common.event.DataLoadedEventHandler;
import com.shico.mnm.common.event.EventBus;
import com.shico.mnm.common.model.ListResult;
import com.shico.mnm.common.model.NestedList;

public abstract class AbstractStatsChartDataProvider implements DataLoadedEventHandler {
	private static Logger logger = Logger.getLogger("AbstractStatsChartDataProviderImpl");
	
	protected abstract void setData(DataTable data);
	protected abstract void setGroupedData(DataTable data);
	protected abstract DataTable getData();
	protected abstract void fireEvent(DataEventType eventtype);
	
	StatsRestService service;
	protected int slidingWinSize = 10000; // number of rows to keep/draw

	public AbstractStatsChartDataProvider() {
		super();		
		
		EventBus.instance().addHandler(DataLoadedEvent.TYPE, this);
	}

	public void getColumnNames() {
		service.getViewColumns(new MethodCallback<ListResult<String>>() {
			public void onFailure(Method method, Throwable exception) {
				logger.log(Level.SEVERE, "Failed to get column metadata."+ exception.getMessage());
				fireEvent(DataEventType.FAILED_STATS_METADATA_LOADED_EVENT);
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
				
				fireEvent(DataEventType.STATS_METADATA_LOADED_EVENT);
			}
		});
	}

	public void getRows(long from, long to) {
		logger.log(Level.INFO, "Requesting records from "+from+" to "+to);
		service.getViewRows(from, to, new MethodCallback<NestedList<Object>>() {
			
			public void onFailure(Method method, Throwable exception) {
				logger.log(Level.SEVERE, "Failed to get data rows. "+ exception.getMessage());
				fireEvent(DataEventType.FAILED_STATS_DATA_LOADED_EVENT);
			}

			public void onSuccess(Method method, NestedList<Object> response) {
				try{
					List<ListResult<Object>> page = response.getRows();
					if(page.isEmpty()){
						logger.log(Level.WARNING, "No row returned from Statistics Rest Service.");
						return;
					}
					int idx = getData().getNumberOfRows();
					getData().addRows(page.size());
					for (ListResult<Object> row : page) {
						int colIdx = 0;
						List<Object> rec = row.getResult();
						for (Object elem : rec) {		
							if(colIdx <= 2){
								getData().setValue(idx, colIdx, elem == null ? null : elem.toString());
							}else{
								getData().setValue(idx, colIdx, (Double)elem);
							}	
							colIdx++;
						}
						idx++;
					}
					// If num. of rows are more than slidingWinSize, then shrink back to slidingWinSize 
					if(getData().getNumberOfRows() > slidingWinSize){
						getData().removeRows(0, getData().getNumberOfRows()-slidingWinSize);
					}
					
					DataTable groupedData = getGroupedData(getData());
					setGroupedData(groupedData);
					
					fireEvent(DataEventType.STATS_DATA_LOADED_EVENT);					
				}catch(Exception e){
					logger.log(Level.SEVERE, "Exception while updating data. "+e.getMessage());
				}
			}
		});
	}

	private native DataTable getGroupedData(DataTable data)/*-{
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
}
