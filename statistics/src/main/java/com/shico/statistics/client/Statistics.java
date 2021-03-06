package com.shico.statistics.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.Table;
import com.google.gwt.visualization.client.visualizations.corechart.LineChart;
import com.shico.mnm.agg.client.AggClientHandle;
import com.shico.mnm.amq.client.AmqClientHandle;
import com.shico.mnm.common.client.ChildRunnable;
import com.shico.mnm.common.client.ParentRunnable;
import com.shico.mnm.common.event.DataEventType;
import com.shico.mnm.common.event.DataLoadedEvent;
import com.shico.mnm.common.event.EventBus;
import com.shico.mnm.stats.client.LiveStatsChartDataProvider;
import com.shico.mnm.stats.client.StatsClientHandle;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Statistics implements EntryPoint {
	private final static Logger logger = Logger.getLogger("Statistics");
	
	TabSet mainTabPanel;

	public void onModuleLoad() {
		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
			
			@Override
			public void onUncaughtException(Throwable e) {
				Window.alert(e.getMessage());
				logger.log(Level.SEVERE, "Uncaught Error in the Statistics Front-end.", e);
			}
		});
		
	    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
	        @Override
	        public void execute() {
	           startApplication();
	        }
	    });
	}

	private void startApplication(){
		
		// Root panel
		final VLayout wrapper = new VLayout();
		wrapper.setWidth100();
		wrapper.setHeight100();

		// Load data and components
		Runnable onAmqLoadCallback = new Runnable() {
			public void run() {
				// do nothing
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


		wrapper.draw();
	}	
	private LiveStatsChartDataProvider statsChartDataProvider;

	TabSet getMainTabPanel(){
		mainTabPanel = new TabSet();
		mainTabPanel.setHeight100();
		
		Tab statsTab = new Tab("Statistics");
		statsTab.setPane(StatsClientHandle.getStatsTabPanel());
		mainTabPanel.addTab(statsTab);
		
		Tab aggTab = new Tab("Aggregator");
		aggTab.setPane(AggClientHandle.getAggTabPanel());
		mainTabPanel.addTab(aggTab);

		Tab amqTab = new Tab("ActiveMQ");
		amqTab.setPane(AmqClientHandle.getAmqTabPanel());
		mainTabPanel.addTab(amqTab);

		mainTabPanel.selectTab(0);
		return mainTabPanel;
	}	
}
