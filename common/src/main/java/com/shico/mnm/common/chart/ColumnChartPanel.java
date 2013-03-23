package com.shico.mnm.common.chart;


import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.visualization.client.ChartArea;
import com.google.gwt.visualization.client.DataView;
import com.google.gwt.visualization.client.visualizations.corechart.ColumnChart;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.google.gwt.visualization.client.visualizations.corechart.TextStyle;
import com.shico.mnm.common.client.ChartDataProvider;
import com.shico.mnm.common.event.DataLoadedEventHandler;

public abstract class ColumnChartPanel extends Composite implements DataLoadedEventHandler, ResizeHandler {
	private static Logger logger = Logger.getLogger("ColumnChartPanel");
	
	protected FocusPanel container;
	protected ColumnChart chart; 
	protected double widthRatio, heightRatio;
//	protected ChartPopup chartPopup;
	protected ChartDataProvider dataProvider;
	private HandlerRegistration dcHandlerRegistration;
	
	protected abstract Options getOptions();
	protected abstract DataView getView();
//	protected abstract ChartPopup asPopupPanel();

	public ColumnChartPanel(ChartDataProvider dataProvider, double widthRatio, double heightRatio) {
		super();
		this.dataProvider = dataProvider; 
		container = new FocusPanel();
		this.widthRatio = widthRatio;
		this.heightRatio = heightRatio;
		
//		dcHandlerRegistration = container.addDoubleClickHandler(new DoubleClickHandler() {			
//			@Override
//			public void onDoubleClick(DoubleClickEvent event) {
//				chartPopup = asPopupPanel();
//				chartPopup.center();
//			}
//		});
		// at the moment there is no obvious way to omit the border on a FocusPanel
		// when it is focused.
		container.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				container.getElement().getStyle().setOutlineWidth(0, Unit.PX);				
			}
		});
	}

	public void draw(){
		try{
			DataView view = getView();
			if(chart == null){
				chart = new ColumnChart(dataProvider.getDataTable(), getOptions());				
				container.add(chart);
			}else {
				chart.draw(view, getOptions());
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error in drawing chart."+e.getMessage());
		}		
	}
	
	@Override
	public void onResize(ResizeEvent event) {
		draw();
	}
	
	public void removeDoubleClickHandler(){
		if(dcHandlerRegistration != null){
			dcHandlerRegistration.removeHandler();
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
	
	private native JavaScriptObject getLegendTextStyle()/*-{
		return {color: 'black', fontSize: 10};
	}-*/;
	
	private native JavaScriptObject getChartArea()/*-{
		return {left:50, width:"80%"}
	}-*/;
}
