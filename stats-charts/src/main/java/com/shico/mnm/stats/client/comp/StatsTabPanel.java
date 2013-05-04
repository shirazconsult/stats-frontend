package com.shico.mnm.stats.client.comp;

import java.util.logging.Logger;

import com.shico.mnm.common.client.ChartDataProvider;
import com.shico.mnm.common.component.PortalWin;
import com.shico.mnm.common.event.DataEventType;
import com.shico.mnm.common.event.DataLoadedEvent;
import com.shico.mnm.common.event.EventBus;
import com.shico.mnm.stats.client.StatsChartDataProviderImpl;
import com.shico.mnm.stats.client.charts.LiveUsageBubbleChartPortlet;
import com.shico.mnm.stats.client.charts.LiveUsageChartPortlet;
import com.shico.mnm.stats.client.charts.MovieRentChartPortlet;
import com.shico.mnm.stats.client.charts.WidgetShowChartPortlet;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.layout.PortalLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;

public class StatsTabPanel extends VLayout { 
	private static final Logger logger = Logger.getLogger("StatsTabPanel");
				
	StatsChartDataProviderImpl dataClient;
	
	TabSet container;
	VLayout liveChartPanel;

	public StatsTabPanel(final ChartDataProvider chartDataProvider) {
		super();
				
		this.dataClient = (StatsChartDataProviderImpl)chartDataProvider;

		setup();
	}

	void setup(){
		container = new TabSet();
		container.setTabBarPosition(Side.TOP);  
		container.setTabBarAlign(Side.LEFT);  

		Tab liveChartTab = new Tab("LiveStats");
		liveChartTab.setPane(getLiveChartTabPanel());
		liveChartTab.addTabSelectedHandler(new TabSelectedHandler() {			
			@Override
			public void onTabSelected(TabSelectedEvent event) {
				setLiveChartPanelPortal();
			}
		});
		container.addTab(liveChartTab);

		setWidth100();

		addMember(container);
	}
			
	public VLayout getLiveChartTabPanel(){
		if(liveChartPanel == null){
			liveChartPanel = new VLayout();
	
			liveChartPanel.setWidth100();
		}
		return liveChartPanel;
	}

	private void setLiveChartPanelPortal(){
		if(liveChartPanel.getMembers().length == 0){			
			PortalLayout portalLayout = new PortalWin(1);
			portalLayout.addPortlet(getLiveUsageChartPortlet(), 0, 0);
			portalLayout.addPortlet(getLiveUsageBubbleChartPortlet(), 0, 1);
			portalLayout.addPortlet(getMovieRentChartPortlet(), 0, 2);
			portalLayout.addPortlet(getWidgShowChartPortlet(), 0, 3);
			
			int height = getLiveUsageChartPortlet().getHeight() +
					getLiveUsageBubbleChartPortlet().getHeight() +
					getMovieRentChartPortlet().getHeight() +
					getWidgShowChartPortlet().getHeight();
			
			portalLayout.setHeight(height);
			liveChartPanel.addMember(portalLayout);
			
			EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.STATS_CHART_SETTINGS_CHANGED_EVENT));  //, settingsController.getSettingsMap()));
		}	
	}
	
	private LiveUsageChartPortlet liveUsageChartPortlet;
	private LiveUsageBubbleChartPortlet liveUsageBubbleChartPortlet;
	private MovieRentChartPortlet movieRentChartPortlet;
	private WidgetShowChartPortlet widgetShowChartPortlet;
	public LiveUsageChartPortlet getLiveUsageChartPortlet(){
		if(liveUsageChartPortlet == null){
			liveUsageChartPortlet = new LiveUsageChartPortlet(dataClient, 1, 0.25);
		}
		return liveUsageChartPortlet;
	}
	public LiveUsageBubbleChartPortlet getLiveUsageBubbleChartPortlet(){
		if(liveUsageBubbleChartPortlet == null){
			liveUsageBubbleChartPortlet = new LiveUsageBubbleChartPortlet(dataClient, 1, 0.25);
		}
		return liveUsageBubbleChartPortlet;
	}
	public MovieRentChartPortlet getMovieRentChartPortlet(){
		if(movieRentChartPortlet == null){
			movieRentChartPortlet = new MovieRentChartPortlet(dataClient, 1, 0.25);
		}
		return movieRentChartPortlet;
	}
	public WidgetShowChartPortlet getWidgShowChartPortlet(){
		if(widgetShowChartPortlet == null){
			widgetShowChartPortlet = new WidgetShowChartPortlet(dataClient, 1, 0.25);
		}
		return widgetShowChartPortlet;
	}
}
