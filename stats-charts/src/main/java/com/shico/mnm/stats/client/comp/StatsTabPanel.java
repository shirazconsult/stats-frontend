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
import com.shico.mnm.stats.client.DeadStatsChartDataProvider;
import com.shico.mnm.stats.client.LiveStatsChartDataProvider;
import com.shico.mnm.stats.client.StatsClientHandle;
import com.shico.mnm.stats.client.StatsSettingsController;
import com.shico.mnm.stats.client.charts.inprogress.LiveUsageBubbleChartPortlet;
import com.shico.mnm.stats.client.charts.inprogress.LiveUsageChartPortlet;
import com.shico.mnm.stats.client.charts.inprogress.LiveUsageDashboardPortlet;
import com.shico.mnm.stats.client.charts.inprogress.MovieRentChartPortlet;
import com.shico.mnm.stats.client.charts.inprogress.WidgetShowChartPortlet;
import com.shico.mnm.stats.client.charts.live.LiveUsageTableAndBubbleChartPortlet;
import com.shico.mnm.stats.client.charts.live.LiveUsageTableAndColumnChartPortlet;
import com.shico.mnm.stats.client.charts.periodic.LiveUsageColumnChartPortlet;
import com.shico.mnm.stats.client.charts.periodic.LiveUsageTableAndBubbleChartPortlet2;
import com.shico.mnm.stats.model.StatsRemoteSettingsDS;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.PortalLayout;
import com.smartgwt.client.widgets.layout.Portlet;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;

public class StatsTabPanel extends VLayout { 
	private static final Logger logger = Logger.getLogger("StatsTabPanel");
				
	LiveStatsChartDataProvider liveStatsChartDataProvider;
	DeadStatsChartDataProvider deadStatsChartDataProvider;

	StatsSettingsController settingsController;
	
	TabSet container;
	VLayout liveChartPanel;
	VLayout deadChartPanel;
	VLayout mainAdminPanel;

	boolean settingsLoaded = false;

	public StatsTabPanel(final ChartDataProvider liveStatsChartDataProvider, final ChartDataProvider deadStatsChartDataProvider, final StatsSettingsController settingsController) {
		super();
				
		this.liveStatsChartDataProvider = (LiveStatsChartDataProvider)liveStatsChartDataProvider;
		this.deadStatsChartDataProvider = (DeadStatsChartDataProvider)deadStatsChartDataProvider;
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

		Tab liveChartTab = new Tab("Live Charts");
		liveChartTab.setPane(getLiveChartTabPanel());
		liveChartTab.addTabSelectedHandler(new TabSelectedHandler() {			
			@Override
			public void onTabSelected(TabSelectedEvent event) {
				setLiveChartPanelPortal();
			}
		});
		container.addTab(liveChartTab);

		Tab deadChartTab = new Tab("Periodic Charts");
		deadChartTab.setPane(getDeadChartTabPanel());
		deadChartTab.addTabSelectedHandler(new TabSelectedHandler() {			
			@Override
			public void onTabSelected(TabSelectedEvent event) {
				setDeadChartPanelPortal();
			}
		});
		container.addTab(deadChartTab);


		setWidth100();

		addMember(container);
	}
			
	private Canvas getDeadChartTabPanel() {
		if(deadChartPanel == null){
			deadChartPanel = new VLayout();
	
			deadChartPanel.setWidth100();
		}
		return deadChartPanel;
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
			
			EventBus.instance().fireEvent(
					new DataLoadedEvent(DataEventType.STATS_CHART_SETTINGS_CHANGED_EVENT, settingsController.getSettingsMap(), "_LiveChartPanel"));
		}	
	}
	
	private void setDeadChartPanelPortal(){
		if(deadChartPanel.getMembers().length == 0){
			EventBus.instance().fireEvent(
					new DataLoadedEvent(DataEventType.STATS_CHART_SETTINGS_CHANGED_EVENT, settingsController.getSettingsMap(), "_DeadChartPanel"));
			
			PortalLayout portalLayout = new PortalWin(1);
			portalLayout.addPortlet(getLiveUsageTableAndBubbleChartPortlet2(), 0, 0);
			portalLayout.addPortlet(getLiveUsageColumnChartPortlet(), 0, 1);
	
			int height = getLiveUsageTableAndBubbleChartPortlet2().getHeight() +
					getLiveUsageColumnChartPortlet().getHeight();			
			portalLayout.setHeight(height);
			deadChartPanel.addMember(portalLayout);	
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
			
			height = height+amqSettingsPortal.getHeight();
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
			liveUsageChartPortlet = new LiveUsageChartPortlet(liveStatsChartDataProvider, 1, 0.20);
		}
		return liveUsageChartPortlet;
	}

	private LiveUsageTableAndColumnChartPortlet liveUsageTableAndColumnChartPortlet;
	public LiveUsageTableAndColumnChartPortlet getLiveUsageTableAndColumnChartPortlet(){
		if(liveUsageTableAndColumnChartPortlet == null){
			liveUsageTableAndColumnChartPortlet = new LiveUsageTableAndColumnChartPortlet(liveStatsChartDataProvider, 1.0, 0.40);
		}
		return liveUsageTableAndColumnChartPortlet;
	}

	private LiveUsageTableAndBubbleChartPortlet liveUsageTableAndBubbleChartPortlet;
	public LiveUsageTableAndBubbleChartPortlet getLiveUsageTableAndBubbleChartPortlet(){
		if(liveUsageTableAndBubbleChartPortlet == null){
			liveUsageTableAndBubbleChartPortlet = new LiveUsageTableAndBubbleChartPortlet(liveStatsChartDataProvider, 1.0, 0.40);
		}
		return liveUsageTableAndBubbleChartPortlet;
	}

	private LiveUsageTableAndBubbleChartPortlet2 liveUsageTableAndBubbleChartPortlet2;
	private Portlet getLiveUsageTableAndBubbleChartPortlet2() {
		if(liveUsageTableAndBubbleChartPortlet2 == null){
			liveUsageTableAndBubbleChartPortlet2 = new LiveUsageTableAndBubbleChartPortlet2(deadStatsChartDataProvider, 1.0, 0.4);
		}
		return liveUsageTableAndBubbleChartPortlet2;
	}

	private LiveUsageColumnChartPortlet liveUsageColumnChartPortlet;
	private Portlet getLiveUsageColumnChartPortlet() {
		if(liveUsageColumnChartPortlet == null){
			liveUsageColumnChartPortlet = new LiveUsageColumnChartPortlet(deadStatsChartDataProvider, 1.0, 0.4);
		}
		return liveUsageColumnChartPortlet;
	}

	private LiveUsageDashboardPortlet liveUsageDashboardPortlet;
	public LiveUsageDashboardPortlet getLiveUsageDashboardPortlet(){
		if(liveUsageDashboardPortlet == null){
			liveUsageDashboardPortlet = new LiveUsageDashboardPortlet(liveStatsChartDataProvider, 1, 0.20);
		}
		return liveUsageDashboardPortlet;
	}
	
	private LiveUsageBubbleChartPortlet liveUsageBubbleChartPortlet;
	public LiveUsageBubbleChartPortlet getLiveUsageBubbleChartPortlet(){
		if(liveUsageBubbleChartPortlet == null){
			liveUsageBubbleChartPortlet = new LiveUsageBubbleChartPortlet(liveStatsChartDataProvider, 1, 0.20);
		}
		return liveUsageBubbleChartPortlet;
	}

	private MovieRentChartPortlet movieRentChartPortlet;
	public MovieRentChartPortlet getMovieRentChartPortlet(){
		if(movieRentChartPortlet == null){
			movieRentChartPortlet = new MovieRentChartPortlet(liveStatsChartDataProvider, 1, 0.20);
		}
		return movieRentChartPortlet;
	}

	private WidgetShowChartPortlet widgetShowChartPortlet;
	public WidgetShowChartPortlet getWidgShowChartPortlet(){
		if(widgetShowChartPortlet == null){
			widgetShowChartPortlet = new WidgetShowChartPortlet(liveStatsChartDataProvider, 1, 0.20);
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
