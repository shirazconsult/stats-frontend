package com.shico.mnm.common.component;

import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.layout.PortalLayout;
import com.smartgwt.client.widgets.layout.Portlet;

public class PortalWin extends PortalLayout {

	public PortalWin(int numColumns) {
		super(numColumns);
		
        setWidth100();    
        setHeight100();  
        setColumnBorder("0");  
        setShowColumnMenus(false);
        setOverflow(Overflow.VISIBLE);  
        setColumnOverflow(Overflow.VISIBLE);
        setPreventColumnUnderflow(false);
        setMembersMargin(5); 
        setCanResizePortlets(true);
        setCanDrop(true);
	}

	public Portlet getPortlet(String title){
		Portlet[] portlets = getPortlets();
		for (Portlet portlet : portlets) {
			if(portlet.getTitle().equals(title)){
				return portlet;
			}
		}
		return null;
	}
	
	public boolean hasPortlet(String title){
		return getPortlet(title) != null;
	}
}
