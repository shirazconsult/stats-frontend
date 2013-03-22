package com.shico.statistics.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.Table;
import com.google.gwt.visualization.client.visualizations.corechart.LineChart;
import com.shico.mnm.agg.client.AggMonitorTabPanel;
import com.shico.mnm.agg.client.AggregatorDataClient;
import com.shico.mnm.amq.client.AmqChartDataProviderImpl;
import com.shico.mnm.amq.client.AmqClientHandle;
import com.shico.mnm.common.client.ChildRunnable;
import com.shico.mnm.common.client.ParentRunnable;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Statistics implements EntryPoint {
	TabSet mainTabPanel;

	AmqChartDataProviderImpl amqDataClient;
	AggregatorDataClient aggDataClient = new AggregatorDataClient();
	
	public void onModuleLoad() {

		// Root panel
		final VLayout wrapper = new VLayout();
		wrapper.setWidth100();
		wrapper.setHeight100();

		
		// Load data and components
		Runnable onAmqLoadCallback = new Runnable() {
			public void run() {
//				amqDataClient.getColumnNames();
//				aggDataClient.getColumnNames();
			}
		};

		ChildRunnable visloader = new ChildRunnable() {			
			@Override
			public void doRun() {
				VisualizationUtils.loadVisualizationApi(new Runnable() {
					public void run() {
						getParent().done();
					}
				}, LineChart.PACKAGE, Table.PACKAGE);
			}
		};
		
		ParentRunnable parent = new ParentRunnable(visloader) {			
			@Override
			public void doRun() {
				wrapper.addMember(getMainTabPanel());
			}
		};
		
		parent.run();

//		AmqChartDataProviderImpl amqDataClient = AmqClientHandle.getChartDataClient();
//		AmqAdminClient amqAdminClient = AmqClientHandle.getAdminClient();

//		amqDataClient.schedule(5, 30);
//		aggDataClient.schedule(10, 30);
//		amqAdminClient.getBrokerInfo("local", true);
//		amqAdminClient.getMetadata("local");
//		AmqClientHandle.getPlaceHistoryHandler().handleCurrentHistory();
		wrapper.draw();
	}	

	TabSet getMainTabPanel(){
		mainTabPanel = new TabSet();
		mainTabPanel.setHeight100();
		
		Tab amqTab = new Tab("ActiveMQ");
		amqTab.setPane(AmqClientHandle.getAmqTabPanel());
		mainTabPanel.addTab(amqTab);
		
		Tab aggTab = new Tab("Aggregator");
		aggTab.setPane(new AggMonitorTabPanel(aggDataClient));
		mainTabPanel.addTab(aggTab);
		
		//		mainTabPanel.add(AmqClientHandle.getAmqTabPanel(), "ActiveMQ", false);
//		mainTabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
//			@Override
//			public void onSelection(SelectionEvent<Integer> event) {
//				int tabIdx = event.getSelectedItem();
//				Widget widget = mainTabPanel.getWidget(tabIdx);
//				if(widget instanceof TabLayoutPanel){
//					((TabPanel)widget).selectTab(0);
//				}
//			}
//		});
		mainTabPanel.selectTab(0);

		return mainTabPanel;
	}	
}
