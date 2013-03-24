package com.shico.mnm.agg.components;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.shico.mnm.agg.client.AggChartDataProvider;
import com.shico.mnm.agg.client.AggClientHandle;
import com.shico.mnm.agg.client.AggSettingsController;
import com.shico.mnm.agg.client.charts.HeapMemChartPortlet;
import com.shico.mnm.agg.client.charts.LoadViewPortlet;
import com.shico.mnm.agg.client.charts.NonHeapMemChartPortlet;
import com.shico.mnm.agg.client.charts.ProcessingTimeTablePortlet;
import com.shico.mnm.agg.model.AggRemoteSettingsDS;
import com.shico.mnm.common.client.ChartDataProvider;
import com.shico.mnm.common.client.ChildRunnable;
import com.shico.mnm.common.client.ParentRunnable;
import com.shico.mnm.common.component.PortalWin;
import com.shico.mnm.common.event.DataEventType;
import com.shico.mnm.common.event.DataLoadedEvent;
import com.shico.mnm.common.event.EventBus;
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

public class AggTabPanel extends VLayout { 
	private static final Logger logger = Logger.getLogger("AmqTabPanel");
			
	AggChartDataProvider dataClient;
	AggSettingsController settingsController;
	
	TabSet container;
	VLayout settingsPanel;
	VLayout mainAdminPanel;
	VLayout monitorPanel;
//	BrokerInfoPortlet brokerInfoPortlet;

	boolean settingsLoaded = false;
	
	public AggTabPanel(final ChartDataProvider chartDataProvider, final AggSettingsController settingsController) {
		super();
		
		this.settingsController = settingsController;
		
		this.dataClient = (AggChartDataProvider)chartDataProvider;
		
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

					Criteria criteria = new Criteria(AggRemoteSettingsDS.APP, AggClientHandle.APP_NAME);
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
//		container.addTabSelectedHandler(new TabSelectedHandler() {
//			@Override
//			public void onTabSelected(TabSelectedEvent event) {
//				container.selectTab(0);
//			}
//		});
		container.setTabBarPosition(Side.TOP);  
		container.setTabBarAlign(Side.LEFT);  

		Tab adminTab = new Tab("Admin");
		adminTab.setPane(getAggMainAdminPanel());
		adminTab.addTabSelectedHandler(new TabSelectedHandler() {			
			@Override
			public void onTabSelected(TabSelectedEvent event) {
				setAggMainAdminPanelPortal();
			}
		});
		container.addTab(adminTab);

		Tab monitorTab = new Tab("Monitor");
		monitorTab.setPane(getMonitorPanel());
		monitorTab.addTabSelectedHandler(new TabSelectedHandler() {				
			@Override
			public void onTabSelected(TabSelectedEvent event) {
				setMonitorPanelPortal();
			}
		});
		container.addTab(monitorTab);



		setWidth100();

		addMember(container);
	}
	
	public VLayout getMonitorPanel(){
		// Monitor Panel
		if(monitorPanel == null){
			monitorPanel = new VLayout();
			
			monitorPanel.setWidth100();
		}
		return monitorPanel;
	}
	
	private void setMonitorPanelPortal(){
		if(monitorPanel.getMembers().length == 0){
			PortalLayout portalLayout = new PortalWin(2);  

	        portalLayout.addPortlet(new HeapMemChartPortlet(dataClient, 0.45, 0.45), 0, 0);
	        portalLayout.addPortlet(new NonHeapMemChartPortlet(dataClient, 0.45, 0.45), 0, 1);
	        portalLayout.addPortlet(new LoadViewPortlet(dataClient, 0.45, 0.45), 1, 0);
	        portalLayout.addPortlet(new ProcessingTimeTablePortlet(dataClient, 0.45, 0.45), 1, 1); 

			monitorPanel.addMember(portalLayout);
			
			EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.AGG_CHART_SETTINGS_CHANGED_EVENT, settingsController.getSettingsMap()));
		}
	}

	public VLayout getAggMainAdminPanel(){
		if(mainAdminPanel == null){
			mainAdminPanel = new VLayout();
	
			mainAdminPanel.setWidth100();
		}
		return mainAdminPanel;
	}

	private void setAggMainAdminPanelPortal(){
		if(mainAdminPanel.getMembers().length == 0){			
			PortalLayout portalLayout = new PortalWin(1);
//			portalLayout.addPortlet(getBrokerInfoPortlet(), 0, 0);
//			int height = getBrokerInfoPortlet().getHeight();
			int height = 0;
//			portalLayout.setHeight(height);
//			mainAdminPanel.addMember(portalLayout);
			
			PortalLayout amqSettingsPortal = getAggSettingsPortal();
			
			height += amqSettingsPortal.getHeight();
			mainAdminPanel.setHeight(height);
			
			mainAdminPanel.addMember(amqSettingsPortal);
			
			if(settingsLoaded){
				// Because MainAdmin portal contains the brokerInfo portlet which needs to be notified of settings.
				EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.AGG_SETTINGS_LOADED_EVENT, settingsController.getSettingsMap()));
				EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.AGG_ADMIN_SETTINGS_CHANGED_EVENT, settingsController.getSettingsMap()));
			}
		}	
	}
	
	public PortalLayout getAggSettingsPortal(){
		PortalLayout portalLayout = new PortalWin(2);  
		
//		portalLayout.addPortlet(getAggAdminSettingsPortlet(), 0, 0);
		portalLayout.addPortlet(getAggChartSettingsPortlet(), 0, 0);
		
//		portalLayout.setHeight(Math.max(getAmqAdminSettingsPortlet().getHeight(), getAmqChartSettingsPortlet().getHeight()));
		portalLayout.setHeight(getAggChartSettingsPortlet().getHeight());

		return portalLayout;
	}

	AggChartSettingsPortlet aggChartSettingsPortlet;
	private Portlet getAggChartSettingsPortlet() {
		if(aggChartSettingsPortlet == null){
			aggChartSettingsPortlet = new AggChartSettingsPortlet(settingsController);
		}
		return aggChartSettingsPortlet;
	}

//	AggAdminSettingsPortlet aggAdminSettingsPortlet;
//	private Portlet getAmqAdminSettingsPortlet() {
//		if(aggAdminSettingsPortlet == null){
//			aggAdminSettingsPortlet = new AggAdminSettingsPortlet(settingsController);
//		}
//		return aggAdminSettingsPortlet;
//	}

//	public BrokerInfoPortlet getBrokerInfoPortlet(){
//		if(brokerInfoPortlet == null){
//			brokerInfoPortlet = new BrokerInfoPortlet(settingsController);
//		}
//		return brokerInfoPortlet;
//	}
}
