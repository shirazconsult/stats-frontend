package com.shico.mnm.common.component;

import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.HeaderControl;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.PortalLayout;
import com.smartgwt.client.widgets.layout.Portlet;

public abstract class PortletWin extends Portlet {
	
	protected abstract void handleRefresh();
	protected abstract void handleSettings();
	protected abstract void handleHelp();
	
	private PortalWin portalContainer;
	
	public PortletWin() {
		super();
		
        setWidth("*"); 
        setAnimateMinimize(true);    
        setHeaderControls(
        		HeaderControls.MAXIMIZE_BUTTON,
        		HeaderControls.MINIMIZE_BUTTON, 
        		HeaderControls.HEADER_LABEL, 
        		new HeaderControl(HeaderControl.REFRESH_THIN, new ClickHandler() {					
					@Override
					public void onClick(ClickEvent event) {
						handleRefresh();
					}
				}),
        		new HeaderControl(HeaderControl.SETTINGS, new ClickHandler() {					
					@Override
					public void onClick(ClickEvent event) {
						handleSettings();
					}
				}), 
        		new HeaderControl(HeaderControl.HELP, new ClickHandler() {					
					@Override
					public void onClick(ClickEvent event) {
						handleHelp();
					}
				}), 
        		HeaderControls.CLOSE_BUTTON);    
        setOverflow(Overflow.VISIBLE);  
	}

	public PortletWin(String title){
		this();
		setTitle(title);
	}
	public void setPortalContainer(PortalWin portalContainer) {
		this.portalContainer = portalContainer;
	}
	public PortalWin getPortalContainer() {
		return portalContainer;
	}
}
