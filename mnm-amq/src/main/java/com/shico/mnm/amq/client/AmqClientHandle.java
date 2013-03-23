package com.shico.mnm.amq.client;

import java.util.logging.Logger;

import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.web.bindery.event.shared.EventBus;
import com.shico.mnm.amq.client.components.AmqTabPanel;

public class AmqClientHandle {
	private final static Logger logger = Logger.getLogger("AmqClientHandle");
	
	public final static String ADMIN_REST_URL = "rest/admin/";
	public final static String APP_NAME = "mnm-amq";
	
	private static AmqChartDataProviderImpl chartDataClient;
	private static AmqSettingsControllerImpl settingsController;

	private static PlaceController placeController;
	private static EventBus eventBus;
	private static PlaceHistoryHandler placeHistoryHandler;
	
	private static AmqTabPanel amqTabPanel;
	private static AmqErrorHandler amqErrorHandler;
		
	public static AmqChartDataProviderImpl getChartDataClient(){
		if(chartDataClient == null){
			chartDataClient = new AmqChartDataProviderImpl();
		}
		return chartDataClient;
	}
	
	public static AmqTabPanel getAmqTabPanel(){
		if(amqTabPanel == null){
			amqTabPanel = new AmqTabPanel(getChartDataClient(), getAmqSettingsController());
		}
		return amqTabPanel;
	}
		
	public static PlaceController getPlaceController() {
		if(placeController == null){
			placeController = new PlaceController(getEventBus());
		}
		return placeController;
	}
	public static EventBus getEventBus() {
		if (eventBus == null){
			eventBus = new SimpleEventBus();
		}
		return eventBus;
	}

	public static AmqErrorHandler getAmqErrorHandler(){
		if(amqErrorHandler == null){
			amqErrorHandler = new AmqErrorHandler();
		}
		return amqErrorHandler;
	}
	
	public static AmqSettingsControllerImpl getAmqSettingsController(){
		if(settingsController == null){
			settingsController = new AmqSettingsControllerImpl();
		}
		return settingsController;
	}

}
