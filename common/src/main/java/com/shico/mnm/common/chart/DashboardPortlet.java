package com.shico.mnm.common.chart;


import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.ChartArea;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.google.gwt.visualization.client.visualizations.corechart.TextStyle;
import com.shico.mnm.common.client.ChartDataProvider;
import com.shico.mnm.common.component.PortletWin;
import com.shico.mnm.common.event.DataLoadedEventHandler;
import com.smartgwt.client.widgets.events.MaximizeClickEvent;
import com.smartgwt.client.widgets.events.MaximizeClickHandler;
import com.smartgwt.client.widgets.events.RestoreClickEvent;
import com.smartgwt.client.widgets.events.RestoreClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;

public abstract class DashboardPortlet extends PortletWin implements DataLoadedEventHandler, MaximizeClickHandler, RestoreClickHandler {
	private static Logger logger = Logger.getLogger("DashboardPortlet");
	
	protected VLayout container;
	protected VLayout vPanel;
	protected double originalWidthRatio, originalHeightRatio, widthRatio, heightRatio;
	protected ChartDataProvider dataProvider;
	private HandlerRegistration dcHandlerRegistration;
	
	private Dashboard dashboard;
	
	protected abstract JsArray<JavaScriptObject> getCharts();
	protected abstract JavaScriptObject getControls();
	protected abstract String getPanelTitle();
	protected abstract AbstractDataTable getData();

	public DashboardPortlet(ChartDataProvider dataProvider, double widthRatio, double heightRatio) {
		super();
		this.dataProvider = dataProvider; 
		container = new VLayout();
		vPanel = new VLayout();
		container.addMember(vPanel);
		
		Label label = new Label(getPanelTitle());
		vPanel.addMember(label);
		
		this.originalWidthRatio = this.widthRatio = widthRatio;
		this.originalHeightRatio = this.heightRatio = heightRatio;		
		
		addItem(container);

		addMaximizeClickHandler(this);
		addRestoreClickHandler(this);
	}

	public void draw(){
		try {
			if(dashboard == null){
				dashboard = new Dashboard();
				vPanel.addMember(dashboard);
				dashboard.bindDashboard(getControls(), getCharts());
			}
			dashboard.draw(getData());
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error in drawing Table."+e.getMessage());
		}		
	}

	protected Options getChartOptions(String title) {
		Options options = Options.create();
		options.setWidth((int)(Window.getClientWidth()*widthRatio));
		options.setHeight((int)(Window.getClientHeight()*heightRatio));
		
		options.setTitle(title);
		
		options.setChartArea((ChartArea) getChartArea());
		options.setLegendTextStyle((TextStyle) getLegendTextStyle());
		return options;
	}

	@Override
	public void onMaximizeClick(MaximizeClickEvent event) {
		widthRatio = 0.8;
		heightRatio = 0.8;
		draw();
	}
	
	@Override
	public void onRestoreClick(RestoreClickEvent event) {
		widthRatio = originalWidthRatio;
		heightRatio = originalHeightRatio;
		draw();
	}

	private native JavaScriptObject getCssClassNames()/*-{
		var cssClassNames = {headerRow: 'shico-VTabel-HeaderRow',
    						selectedTableRow: 'shico-VTable-SelectedTableRow'
    						};
		return cssClassNames;
	}-*/;
	
	private native JavaScriptObject getLegendTextStyle()/*-{
		return {color: 'black', fontSize: 10};
	}-*/;

	private native JavaScriptObject getChartArea()/*-{
		return {left:50, width:"80%"}
	}-*/;

}
