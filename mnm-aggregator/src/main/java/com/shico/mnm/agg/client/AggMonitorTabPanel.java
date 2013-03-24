package com.shico.mnm.agg.client;

import com.shico.mnm.agg.client.charts.HeapMemChartPortlet;
import com.shico.mnm.agg.client.charts.LoadViewPortlet;
import com.shico.mnm.agg.client.charts.NonHeapMemChartPortlet;
import com.shico.mnm.agg.client.charts.ProcessingTimeTablePortlet;
import com.shico.mnm.common.client.ChartDataProvider;
import com.shico.mnm.common.component.PortalWin;
import com.shico.mnm.common.event.DataEventType;
import com.shico.mnm.common.event.DataLoadedEvent;
import com.shico.mnm.common.event.EventBus;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.layout.PortalLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;

public class AggMonitorTabPanel extends VLayout {
	
	AggChartDataProviderImpl dataClient;

	TabSet container;
	VLayout monitorPanel;

	public AggMonitorTabPanel(ChartDataProvider chartDataProvider) {
		super();
		
		dataClient = (AggChartDataProviderImpl)chartDataProvider;

		setup();
	}
	
	void setup(){
		container = new TabSet();
		container.setTabBarPosition(Side.TOP);  
		container.setTabBarAlign(Side.LEFT);  
		
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
			
			EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.AGG_CHART_SETTINGS_CHANGED_EVENT));
		}
	}
	
	public TabSet getTabContainer(){
		return container;
	}

}
