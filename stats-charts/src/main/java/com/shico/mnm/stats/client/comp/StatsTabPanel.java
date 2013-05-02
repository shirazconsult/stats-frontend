package com.shico.mnm.stats.client.comp;

import java.util.logging.Logger;

import com.shico.mnm.common.client.ChartDataProvider;
import com.shico.mnm.common.component.PortalWin;
import com.shico.mnm.common.event.DataEventType;
import com.shico.mnm.common.event.DataLoadedEvent;
import com.shico.mnm.common.event.EventBus;
import com.shico.mnm.stats.client.StatsChartDataProviderImpl;
import com.shico.mnm.stats.client.charts.LiveUsageChartPortlet;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.layout.PortalLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;

public class StatsTabPanel extends VLayout { 
	private static final Logger logger = Logger.getLogger("AmqTabPanel");
				
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
			int height = getLiveUsageChartPortlet().getHeight();
			portalLayout.setHeight(height);
			liveChartPanel.addMember(portalLayout);
			
			EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.STATS_CHART_SETTINGS_CHANGED_EVENT));  //, settingsController.getSettingsMap()));
		}	
	}
	
	private LiveUsageChartPortlet liveUsageChartPortlet;
	public LiveUsageChartPortlet getLiveUsageChartPortlet(){
		if(liveUsageChartPortlet == null){
			liveUsageChartPortlet = new LiveUsageChartPortlet(dataClient, 0.45, 0.40);
		}
		return liveUsageChartPortlet;
	}
}
