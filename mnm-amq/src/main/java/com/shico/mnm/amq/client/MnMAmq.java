package com.shico.mnm.amq.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;

public class MnMAmq implements EntryPoint {
	private static final Logger logger = Logger.getLogger("AMQEntryPoint");
	public static boolean loaded;
	
	@Override
	public void onModuleLoad() {
//		 see GWT Issue 1617: UncaughtExceptionHandler not triggered for exceptions occurring in onModuleLoad() [http://code.google.com/p/google-web-toolkit/issues/detail?id=1617]
		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
			
			@Override
			public void onUncaughtException(Throwable e) {
				Window.alert(e.getMessage());
				logger.log(Level.SEVERE, "Failed to startup AMQ entry point.", e);
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
	}

}
