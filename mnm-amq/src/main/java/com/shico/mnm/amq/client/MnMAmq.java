package com.shico.mnm.amq.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;

public class MnMAmq implements EntryPoint {
	public static boolean loaded;
	
	@Override
	public void onModuleLoad() {
//		 see GWT Issue 1617: UncaughtExceptionHandler not triggered for exceptions occurring in onModuleLoad() [http://code.google.com/p/google-web-toolkit/issues/detail?id=1617]
		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
			
			@Override
			public void onUncaughtException(Throwable e) {
				Window.alert(e.getMessage());
				e.printStackTrace();
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
		// initialize ErrorDialog
		AmqClientHandle.getAmqErrorHandler();
		
		final AmqSettingsControllerImpl settingsController = AmqClientHandle.getAmqSettingsController();
		settingsController.init();
		
//		Criteria criteria = new Criteria(AdminSettingsDS.APP, AmqClientHandle.APP_NAME);
//		settingsController.getSettings().fetchData(criteria, new DSCallback() {
//			@SuppressWarnings("rawtypes")
//			@Override
//			public void execute(DSResponse response, Object rawData, DSRequest request) {
//				Map settings = response.getData()[0].toMap();
//
//				// instantiate datasources
//				String restUrl = (String)settings.get(AdminSettingsDS.BROKERURL);
//				settingsController.setBrokerInfoDS(new BrokerInfoDS("BrokerInfoDS", restUrl+"/brokerInfo"));
//
//				EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.SETTINGS_CHANGED_EVENT, settings));
//				loaded = true;
//			}
//		});

		// initialize activity mappers and places
//		AmqActivityMapper amqActivityMapper = new AmqActivityMapper(
//				AmqClientHandle.getAdminClient(),
//				AmqClientHandle.getChartDataClient());
//		ActivityManager activityManager = new ActivityManager(amqActivityMapper, AmqClientHandle.getEventBus());
//		activityManager.setDisplay(AmqClientHandle.getAmqTabPanel());
//		
//		AmqClientHandle.getPlaceHistoryHandler().register(
//				AmqClientHandle.getPlaceController(), 
//				AmqClientHandle.getEventBus(), 
//				new QueueListPlace("local", "refresh"));
	}

}
