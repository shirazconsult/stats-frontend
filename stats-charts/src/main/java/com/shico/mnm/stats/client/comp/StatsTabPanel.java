package com.shico.mnm.stats.client.comp;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.shico.mnm.common.client.ChartDataProvider;
import com.shico.mnm.common.client.ChildRunnable;
import com.shico.mnm.common.client.ParentRunnable;
import com.shico.mnm.common.component.PortalWin;
import com.shico.mnm.common.event.DataEventType;
import com.shico.mnm.common.event.DataLoadedEvent;
import com.shico.mnm.common.event.EventBus;
import com.shico.mnm.stats.client.LiveStatsChartDataProvider;
import com.shico.mnm.stats.client.StatsClientHandle;
import com.shico.mnm.stats.client.StatsSettingsController;
import com.shico.mnm.stats.client.charts.LiveUsageBubbleChartPortlet;
import com.shico.mnm.stats.client.charts.LiveUsageChartPortlet;
import com.shico.mnm.stats.client.charts.LiveUsageDashboardPortlet;
import com.shico.mnm.stats.client.charts.LiveUsageTableAndBubbleChartPortlet;
import com.shico.mnm.stats.client.charts.LiveUsageTableAndColumnChartPortlet;
import com.shico.mnm.stats.client.charts.MovieRentChartPortlet;
import com.shico.mnm.stats.client.charts.WidgetShowChartPortlet;
import com.shico.mnm.stats.model.StatsRemoteSettingsDS;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.layout.PortalLayout;
import com.smartgwt.client.widgets.layout.Portlet;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;

public class StatsTabPanel extends VLayout { 
	private static final Logger logger = Logger.getLogger("StatsTabPanel");
				
	LiveStatsChartDataProvider dataClient;
	StatsSettingsController settingsController;
	
	TabSet container;
	VLayout liveChartPanel;
	VLayout mainAdminPanel;

	boolean settingsLoaded = false;

	public StatsTabPanel(final ChartDataProvider chartDataProvider, final StatsSettingsController settingsController) {
		super();
				
		this.dataClient = (LiveStatsChartDataProvider)chartDataProvider;
		this.settingsController = settingsController;
		
		ChildRunnable settingLoader = new ChildRunnable() {			
			@Override
			public void doRun() {
				if(settingsController.useLocalStorage()){
					logger.log(Level.INFO, "Loading settings from local storage.");
					settingsController.setSettingsMapFromLocalStorage();

					//						String restUrl = sm.get(AggRemoteSettingsDS.AGGREGATORURL);
					//						settingsController.setBrokerInfoDS(new BrokerInfoDS(restUrl));

					settingsLoaded = true;
					logger.log(Level.INFO, "Settings loaded from local storage.");
					getParent().done();
				}else{
					logger.log(Level.INFO, "Loading settings from server.");

					Criteria criteria = new Criteria(StatsRemoteSettingsDS.APP, StatsClientHandle.APP_NAME);
					settingsController.getSettingsDS().fetchData(criteria, new DSCallback() {
						@SuppressWarnings("rawtypes")
						@Override
						public void execute(DSResponse response, Object rawData, DSRequest request) {
							Map settings = response.getData()[0].toMap();
							settingsController.setSettingsMap(settings);

							// instantiate datasources
//							String restUrl = (String)settings.get(AggRemoteSettingsDS.AGGREGATORURL);
//
//							settingsController.setBrokerInfoDS(new BrokerInfoDS(restUrl));

							settingsLoaded = true;
							logger.log(Level.INFO, "Settings loaded from server.");

							getParent().done();
						}
					});
				}
			};
		};
		
		ParentRunnable parent = new ParentRunnable(settingLoader) {			
			@Override
			public void doRun() {
				setup();
			}
		};
		
		parent.run();
	}

	void setup(){
		container = new TabSet();
		container.setTabBarPosition(Side.TOP);  
		container.setTabBarAlign(Side.LEFT);  

		Tab adminTab = new Tab("Admin");
		adminTab.setPane(getStatsMainAdminPanel());
		adminTab.addTabSelectedHandler(new TabSelectedHandler() {			
			@Override
			public void onTabSelected(TabSelectedEvent event) {
				setStatsMainAdminPanelPortal();
			}
		});
		container.addTab(adminTab);

		Tab liveChartTab = new Tab("Live");
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

	public VLayout getStatsMainAdminPanel(){
		if(mainAdminPanel == null){
			mainAdminPanel = new VLayout();
	
			mainAdminPanel.setWidth100();
		}
		return mainAdminPanel;
	}

	private void setLiveChartPanelPortal(){
		if(liveChartPanel.getMembers().length == 0){			
			PortalLayout portalLayout = new PortalWin(1);
			portalLayout.addPortlet(getLiveUsageTableAndColumnChartPortlet(), 0, 0);
			portalLayout.addPortlet(getLiveUsageTableAndBubbleChartPortlet(), 0, 1);
//			portalLayout.addPortlet(getMovieRentChartPortlet(), 0, 2);
//			portalLayout.addPortlet(getWidgShowChartPortlet(), 0, 3);
			
			int height = getLiveUsageTableAndColumnChartPortlet().getHeight() +
					getLiveUsageTableAndBubbleChartPortlet().getHeight();
//					getMovieRentChartPortlet().getHeight() +
//					getWidgShowChartPortlet().getHeight();
			
			portalLayout.setHeight(height);
			liveChartPanel.addMember(portalLayout);
			
			EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.STATS_CHART_SETTINGS_CHANGED_EVENT, settingsController.getSettingsMap()));
		}	
	}
	
	private void setStatsMainAdminPanelPortal(){
		if(mainAdminPanel.getMembers().length == 0){			
			PortalLayout portalLayout = new PortalWin(1);
//			portalLayout.addPortlet(getBrokerInfoPortlet(), 0, 0);
//			int height = getBrokerInfoPortlet().getHeight();
			int height = 0;
//			portalLayout.setHeight(height);
//			mainAdminPanel.addMember(portalLayout);
			
			PortalLayout amqSettingsPortal = getStatsSettingsPortal();
			
			height += amqSettingsPortal.getHeight();
			mainAdminPanel.setHeight(height);
			
			mainAdminPanel.addMember(amqSettingsPortal);
			
			if(settingsLoaded){
				// Because MainAdmin portal contains the brokerInfo portlet which needs to be notified of settings.
				EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.STATS_SETTINGS_LOADED_EVENT, settingsController.getSettingsMap()));
				EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.STATS_ADMIN_SETTINGS_CHANGED_EVENT, settingsController.getSettingsMap()));
			}
		}	
	}

	public PortalLayout getStatsSettingsPortal(){
		PortalLayout portalLayout = new PortalWin(2);  
		
//		portalLayout.addPortlet(getAggAdminSettingsPortlet(), 0, 0);
		portalLayout.addPortlet(getStatsChartSettingsPortlet(), 0, 0);
		
//		portalLayout.setHeight(Math.max(getAmqAdminSettingsPortlet().getHeight(), getAmqChartSettingsPortlet().getHeight()));
		portalLayout.setHeight(getStatsChartSettingsPortlet().getHeight());

		return portalLayout;
	}


	private LiveUsageChartPortlet liveUsageChartPortlet;
	public LiveUsageChartPortlet getLiveUsageChartPortlet(){
		if(liveUsageChartPortlet == null){
			liveUsageChartPortlet = new LiveUsageChartPortlet(dataClient, 1, 0.20);
		}
		return liveUsageChartPortlet;
	}

	private LiveUsageTableAndColumnChartPortlet liveUsageTableAndColumnChartPortlet;
	public LiveUsageTableAndColumnChartPortlet getLiveUsageTableAndColumnChartPortlet(){
		if(liveUsageTableAndColumnChartPortlet == null){
			liveUsageTableAndColumnChartPortlet = new LiveUsageTableAndColumnChartPortlet(dataClient, 1.0, 0.40);
		}
		return liveUsageTableAndColumnChartPortlet;
	}

	private LiveUsageTableAndBubbleChartPortlet liveUsageTableAndBubbleChartPortlet;
	public LiveUsageTableAndBubbleChartPortlet getLiveUsageTableAndBubbleChartPortlet(){
		if(liveUsageTableAndBubbleChartPortlet == null){
			liveUsageTableAndBubbleChartPortlet = new LiveUsageTableAndBubbleChartPortlet(dataClient, 1.0, 0.40);
		}
		return liveUsageTableAndBubbleChartPortlet;
	}
	
	private LiveUsageDashboardPortlet liveUsageDashboardPortlet;
	public LiveUsageDashboardPortlet getLiveUsageDashboardPortlet(){
		if(liveUsageDashboardPortlet == null){
			liveUsageDashboardPortlet = new LiveUsageDashboardPortlet(dataClient, 1, 0.20);
		}
		return liveUsageDashboardPortlet;
	}
	
	private LiveUsageBubbleChartPortlet liveUsageBubbleChartPortlet;
	public LiveUsageBubbleChartPortlet getLiveUsageBubbleChartPortlet(){
		if(liveUsageBubbleChartPortlet == null){
			liveUsageBubbleChartPortlet = new LiveUsageBubbleChartPortlet(dataClient, 1, 0.20);
		}
		return liveUsageBubbleChartPortlet;
	}

	private MovieRentChartPortlet movieRentChartPortlet;
	public MovieRentChartPortlet getMovieRentChartPortlet(){
		if(movieRentChartPortlet == null){
			movieRentChartPortlet = new MovieRentChartPortlet(dataClient, 1, 0.20);
		}
		return movieRentChartPortlet;
	}

	private WidgetShowChartPortlet widgetShowChartPortlet;
	public WidgetShowChartPortlet getWidgShowChartPortlet(){
		if(widgetShowChartPortlet == null){
			widgetShowChartPortlet = new WidgetShowChartPortlet(dataClient, 1, 0.20);
		}
		return widgetShowChartPortlet;
	}
	
	StatsChartSettingsPortlet statsChartSettingsPortlet;
	private Portlet getStatsChartSettingsPortlet() {
		if(statsChartSettingsPortlet == null){
			statsChartSettingsPortlet = new StatsChartSettingsPortlet(settingsController);
		}
		return statsChartSettingsPortlet;
	}
	
}
