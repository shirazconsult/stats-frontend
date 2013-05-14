package com.shico.mnm.common.chart;


import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.user.client.Window;
import com.shico.mnm.common.component.PortletWin;
import com.shico.mnm.common.event.DataLoadedEventHandler;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.events.MaximizeClickEvent;
import com.smartgwt.client.widgets.events.MaximizeClickHandler;
import com.smartgwt.client.widgets.events.RestoreClickEvent;
import com.smartgwt.client.widgets.events.RestoreClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public abstract class MultipleChartPortlet extends PortletWin implements DataLoadedEventHandler, MaximizeClickHandler, RestoreClickHandler {
	private static Logger logger = Logger.getLogger("MultipleChartPortlet");
	
	protected HLayout container;
	protected double originalWidthRatio, originalHeightRatio, widthRatio, heightRatio;
	
	boolean added;
	protected abstract ChartWrapper[] getCharts();
	
	public MultipleChartPortlet(double widthRatio, double heightRatio) {
		super();
		container = new HLayout();
		container.setWidth100();
		container.setHeight((int)(Window.getClientHeight()*heightRatio));
		container.setAlign(Alignment.LEFT);
		container.setAlign(VerticalAlignment.TOP);
		container.setLayoutLeftMargin(20);
		container.setMembersMargin(10);
		
		this.originalWidthRatio = this.widthRatio = widthRatio;
		this.originalHeightRatio = this.heightRatio = heightRatio;		

		addItem(container);

		addMaximizeClickHandler(this);
		addRestoreClickHandler(this);
	}

	public void draw(){
		try{
			if(getCharts() != null){
				for (ChartWrapper cw : getCharts()) {
					cw.chart.setWidth(Math.ceil(container.getWidth()*cw.widthRatio)+"px");
					cw.chart.setHeight(Math.ceil(container.getHeight()*cw.heightRatio)+"px");						
					if(!added){		
						VLayout vl = new VLayout();
						vl.setAlign(VerticalAlignment.CENTER);
						vl.addMember(cw.chart);
						container.addMember(vl);
					}
					cw.chart.draw(cw.data, cw.options);
				}
				added = true;
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error in drawing chart."+e.getMessage());
		}		
	}
	
	@Override
	public void onMaximizeClick(MaximizeClickEvent event) {
		widthRatio = 0.8;
		heightRatio = 0.8;
		setContainerHeight();
		draw();
	}
	
	@Override
	public void onRestoreClick(RestoreClickEvent event) {
		widthRatio = originalWidthRatio;
		heightRatio = originalHeightRatio;
		setContainerHeight();
		draw();
	}
	
	private void setContainerHeight(){
		container.setHeight((int)(Window.getClientHeight()*heightRatio));
	}
}
