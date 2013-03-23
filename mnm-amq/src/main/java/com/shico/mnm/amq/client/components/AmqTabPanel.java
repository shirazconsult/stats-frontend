package com.shico.mnm.amq.client.components;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.storage.client.Storage;
import com.google.gwt.storage.client.StorageMap;
import com.shico.mnm.amq.client.AmqChartDataProviderImpl;
import com.shico.mnm.amq.client.AmqClientHandle;
import com.shico.mnm.amq.client.AmqSettingsControllerImpl;
import com.shico.mnm.amq.client.charts.AvgEnqTimeChartPortlet;
import com.shico.mnm.amq.client.charts.DiskUsageChartPortlet;
import com.shico.mnm.amq.client.charts.EnqDecInflChartPortlet;
import com.shico.mnm.amq.client.charts.MemUsageChartPortlet;
import com.shico.mnm.amq.model.AmqRemoteSettingsDS;
import com.shico.mnm.amq.model.BrokerInfoDS;
import com.shico.mnm.amq.model.QueueListDS;
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
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.layout.PortalLayout;
import com.smartgwt.client.widgets.layout.Portlet;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;

public class AmqTabPanel extends VLayout { 
	private static final Logger logger = Logger.getLogger("AmqTabPanel");
			
	// temp. field for controlling the authentication flow. Must be removed
	// when real authentication is in place.
	static boolean authenticated = true;
	
	AmqChartDataProviderImpl dataClient;
	AmqSettingsControllerImpl settingsController;
	
	TabSet container;
	VLayout settingsPanel;
	VLayout mainAdminPanel;
	VLayout monitorPanel;
	VLayout queuePanel;
	BrokerInfoPortlet brokerInfoPortlet;
	QueueListPortlet queueListPortlet;
	
	boolean settingsLoaded = false;
	
	public AmqTabPanel(final ChartDataProvider chartDataProvider, final AmqSettingsControllerImpl settingsController) {
		super();
		
		this.settingsController = settingsController;
		
		if(authenticated){
			this.dataClient = (AmqChartDataProviderImpl)chartDataProvider;
		}
		
		ChildRunnable settingLoader = new ChildRunnable() {			
			@Override
			public void doRun() {
				Criteria criteria = new Criteria(AmqRemoteSettingsDS.APP, AmqClientHandle.APP_NAME);
				DataSource settingsDS = settingsController.getSettingsDS();
				if(settingsDS.getClientOnly() != null && settingsDS.getClientOnly()){
					logger.log(Level.INFO, "Loading settings from local storage.");
					// Read from local storage
					Storage settingsStorage = Storage.getLocalStorageIfSupported();
					if(settingsStorage == null){
						throw new IllegalStateException("Cannot read settings from local storage, because it is not supported. Please use a browser with HTML5.");
					}
					StorageMap sm = new StorageMap(settingsStorage);
					if(sm != null && !sm.isEmpty()){
						Map settingsMap = new HashMap();
						settingsMap.putAll(sm);
						settingsController.setSettingsMap(settingsMap);
						
						String restUrl = sm.get(AmqRemoteSettingsDS.BROKERURL);
						if(restUrl != null){
							settingsController.setBrokerInfoDS(new BrokerInfoDS(restUrl));
							settingsController.setQueueListDS(new QueueListDS(restUrl));							
						}
						settingsLoaded = true;
					}else{
						settingsController.setBrokerInfoDS(new BrokerInfoDS(""));
						settingsController.setQueueListDS(new QueueListDS(""));							
					}

					logger.log(Level.INFO, "Settings loaded from local storage.");
					getParent().done();
				}else{
					logger.log(Level.INFO, "Loading settings from server.");

					settingsDS.fetchData(criteria, new DSCallback() {
						@SuppressWarnings("rawtypes")
						@Override
						public void execute(DSResponse response, Object rawData, DSRequest request) {
							Map settings = response.getData()[0].toMap();
							settingsController.setSettingsMap(settings);

							// instantiate datasources
							String restUrl = (String)settings.get(AmqRemoteSettingsDS.BROKERURL);

							settingsController.setBrokerInfoDS(new BrokerInfoDS(restUrl));
							settingsController.setQueueListDS(new QueueListDS(restUrl));

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
		adminTab.setPane(getAmqMainAdminPanel());
		adminTab.addTabSelectedHandler(new TabSelectedHandler() {			
			@Override
			public void onTabSelected(TabSelectedEvent event) {
				setAmqMainAdminPanelPortal();
			}
		});
		container.addTab(adminTab);
		if(authenticated){
			Tab queueTab = new Tab("Queues");
			queueTab.setPane(getAmqQueuePanel());
			queueTab.addTabSelectedHandler(new TabSelectedHandler() {				
				@Override
				public void onTabSelected(TabSelectedEvent event) {
					setAmqQueuePanelPortal();
				}
			});
			container.addTab(queueTab);

			Tab monitorTab = new Tab("Monitor");
			monitorTab.setPane(getMonitorPanel());
			monitorTab.addTabSelectedHandler(new TabSelectedHandler() {				
				@Override
				public void onTabSelected(TabSelectedEvent event) {
					setMonitorPanelPortal();
				}
			});
			container.addTab(monitorTab);
		}
		
		
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

	        portalLayout.addPortlet(new EnqDecInflChartPortlet(dataClient, 0.45, 0.40), 0, 0);
	        portalLayout.addPortlet(new AvgEnqTimeChartPortlet(dataClient, 0.45, 0.40), 0, 1);
	        portalLayout.addPortlet(new MemUsageChartPortlet(dataClient, 0.45, 0.40), 1, 0);
	        portalLayout.addPortlet(new DiskUsageChartPortlet(dataClient, 0.45, 0.40), 1, 1); 

			monitorPanel.addMember(portalLayout);

			if(settingsLoaded){
				EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.AMQ_CHART_SETTINGS_CHANGED_EVENT, settingsController.getSettingsMap()));
			}
		}
	}
	
	public VLayout getAmqMainAdminPanel(){
		if(mainAdminPanel == null){
			mainAdminPanel = new VLayout();
	
			mainAdminPanel.setWidth100();
		}
		return mainAdminPanel;
	}

	private void setAmqMainAdminPanelPortal(){
		if(mainAdminPanel.getMembers().length == 0){			
			PortalLayout portalLayout = new PortalWin(1);
			portalLayout.addPortlet(getBrokerInfoPortlet(), 0, 0);
			int height = getBrokerInfoPortlet().getHeight();
			portalLayout.setHeight(height);
			mainAdminPanel.addMember(portalLayout);
			
			PortalLayout amqSettingsPortal = getAmqSettingsPortal();
			
			height += amqSettingsPortal.getHeight();
			mainAdminPanel.setHeight(height);
			
			mainAdminPanel.addMember(amqSettingsPortal);
			
			if(settingsLoaded){
				// Because MainAdmin portal contains the brokerInfo portlet which needs to be notified of settings.
				EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.AMQ_SETTINGS_LOADED_EVENT, settingsController.getSettingsMap()));
				EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.AMQ_ADMIN_SETTINGS_CHANGED_EVENT, settingsController.getSettingsMap()));
			}
		}	
	}
	
	public PortalLayout getAmqSettingsPortal(){
		PortalLayout portalLayout = new PortalWin(2);  
		
		portalLayout.addPortlet(getAmqAdminSettingsPortlet(), 0, 0);
		portalLayout.addPortlet(getAmqChartSettingsPortlet(), 1, 0);
		
		portalLayout.setHeight(Math.max(getAmqAdminSettingsPortlet().getHeight(), getAmqChartSettingsPortlet().getHeight()));
		return portalLayout;
	}

	AmqChartSettingsPortlet amqChartSettingsPortlet;
	private Portlet getAmqChartSettingsPortlet() {
		if(amqChartSettingsPortlet == null){
			amqChartSettingsPortlet = new AmqChartSettingsPortlet(settingsController);
		}
		return amqChartSettingsPortlet;
	}

	AmqAdminSettingsPortlet amqAdminSettingsPortlet;
	private Portlet getAmqAdminSettingsPortlet() {
		if(amqAdminSettingsPortlet == null){
			amqAdminSettingsPortlet = new AmqAdminSettingsPortlet(settingsController);
		}
		return amqAdminSettingsPortlet;
	}

	public VLayout getAmqQueuePanel(){
		if(queuePanel == null){
			queuePanel = new VLayout();
	
			queuePanel.setWidth100();
		}
		return queuePanel;
	}
	
	private void setAmqQueuePanelPortal(){
		if(queuePanel.getMembers().length == 0){			
			PortalWin portalLayout = new PortalWin(1);  
	        
	        portalLayout.addPortlet(getQueueListPortlet());
	        // queuelist portlet has to be able to drop msg-portlets into the portal
	        getQueueListPortlet().setPortalContainer(portalLayout);
	        
	        queuePanel.addMember(portalLayout);
	        
			if(settingsLoaded){
				EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.AMQ_ADMIN_SETTINGS_CHANGED_EVENT, settingsController.getSettingsMap()));
			}
		}
	}

	public QueueListPortlet getQueueListPortlet(){
		if(queueListPortlet == null){
			queueListPortlet = new QueueListPortlet(settingsController);
		}
		return queueListPortlet;
	}
	public BrokerInfoPortlet getBrokerInfoPortlet(){
		if(brokerInfoPortlet == null){
			brokerInfoPortlet = new BrokerInfoPortlet(settingsController);
		}
		return brokerInfoPortlet;
	}
}
