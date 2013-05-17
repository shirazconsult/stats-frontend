package com.shico.mnm.common.component;

import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.HeaderControl;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.PortalLayout;
import com.smartgwt.client.widgets.layout.Portlet;

public abstract class PortletWin extends Portlet {
	
	protected abstract void handleRefresh(ClickEvent event);
	protected abstract void handleSettings(ClickEvent event);
	protected abstract void handleHelp(ClickEvent event);
//	protected abstract void handleClose();
	
	private PortalLayout portalContainer;
	
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
						handleRefresh(event);
					}
				}),
        		new HeaderControl(HeaderControl.SETTINGS, new ClickHandler() {					
					@Override
					public void onClick(ClickEvent event) {
						handleSettings(event);
					}
				}), 
        		new HeaderControl(HeaderControl.HELP, new ClickHandler() {					
					@Override
					public void onClick(ClickEvent event) {
						handleHelp(event);
					}
				}), 
				new HeaderControl(HeaderControl.CLOSE, new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						handleClose();
					}
				}));    
        setOverflow(Overflow.VISIBLE);  
	}

	public PortletWin(String title){
		this();
		setTitle(title);
	}
	public void setPortalContainer(PortalLayout portalContainer) {
		this.portalContainer = portalContainer;
	}
	public PortalLayout getPortalContainer() {
		return portalContainer;
	}
	
	protected void handleClose() {
		getPortalContainer().removePortlet(this);
	}

}
