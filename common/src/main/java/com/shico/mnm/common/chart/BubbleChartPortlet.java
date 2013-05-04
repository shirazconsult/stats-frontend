package com.shico.mnm.common.chart;


import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.visualization.client.ChartArea;
import com.google.gwt.visualization.client.DataView;
import com.google.gwt.visualization.client.visualizations.corechart.ColumnChart;
import com.google.gwt.visualization.client.visualizations.corechart.CoreChart;
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

public abstract class BubbleChartPortlet extends PortletWin implements DataLoadedEventHandler, MaximizeClickHandler, RestoreClickHandler {
	private static Logger logger = Logger.getLogger("BubbleChartPortlet");
	
	protected VLayout container;
	protected BubbleChart chart; 
	protected double originalWidthRatio, originalHeightRatio, widthRatio, heightRatio;
	protected ChartDataProvider dataProvider;
	private HandlerRegistration dcHandlerRegistration;
	
	protected abstract Options getOptions();
	protected abstract DataView getView();

	public BubbleChartPortlet(ChartDataProvider dataProvider, double widthRatio, double heightRatio) {
		super();
		this.dataProvider = dataProvider; 
		container = new VLayout();
		container.setWidth100();
		
		this.originalWidthRatio = this.widthRatio = widthRatio;
		this.originalHeightRatio = this.heightRatio = heightRatio;		

		addItem(container);

		addMaximizeClickHandler(this);
		addRestoreClickHandler(this);
	}

	public void draw(){
		try{
			DataView view = getView();
			if(chart == null){
				chart = new BubbleChart(dataProvider.getDataTable(), getOptions());			
				container.addMember(chart);
			}else {
				chart.draw(view, getOptions());
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error in drawing chart."+e.getMessage());
		}		
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
	
	protected Options getChartOptions(String title) {
		Options options = Options.create();
		options.setWidth((int)(Window.getClientWidth()*widthRatio));
		options.setHeight((int)(Window.getClientHeight()*heightRatio));
		
		options.setTitle(title);
		
		options.setChartArea((ChartArea) getChartArea());
		options.setLegendTextStyle((TextStyle) getLegendTextStyle());
		return options;
	}
	
	private native JavaScriptObject getLegendTextStyle()/*-{
		return {color: 'black', fontSize: 10};
	}-*/;
	
	private native JavaScriptObject getChartArea()/*-{
		return {left:50, width:"80%"}
	}-*/;
}
