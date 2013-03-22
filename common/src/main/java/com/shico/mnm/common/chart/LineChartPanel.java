package com.shico.mnm.common.chart;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.visualization.client.DataView;
import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.HorizontalAxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.LineChart;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.google.gwt.visualization.client.visualizations.corechart.TextStyle;
import com.shico.mnm.common.client.ChartDataProvider;
import com.shico.mnm.common.event.DataLoadedEventHandler;

public abstract class LineChartPanel extends Composite implements DataLoadedEventHandler, ResizeHandler {
	protected FocusPanel container;
	protected LineChart chart; 
	protected double widthRatio, heightRatio;
	protected LineChartPopup chartPopup;
	protected ChartDataProvider dataProvider;
	private HandlerRegistration dcHandlerRegistration;
	
	protected abstract Options getOptions();
	protected abstract DataView getView();
	protected abstract LineChartPopup asPopupPanel();
	
	public LineChartPanel(ChartDataProvider dataProvider, double widthRatio, double heightRatio) {
		super();
		this.dataProvider = dataProvider; 
		container = new FocusPanel();
		this.widthRatio = widthRatio;
		this.heightRatio = heightRatio;
		
		dcHandlerRegistration = container.addDoubleClickHandler(new DoubleClickHandler() {			
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				chartPopup = asPopupPanel();
				chartPopup.center();
			}
		});
		// at the moment there is no obvious way to omit the border on a FocusPanel
		// when it is focused.
		container.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				container.getElement().getStyle().setOutlineWidth(0, Unit.PX);				
			}
		});
		
		Window.addResizeHandler(this);
	}
	
	public void draw(){
		try {
			DataView view = getView();
			if(chart == null){
				chart = new LineChart(view, getOptions());
				if (chart != null) {
					container.add(chart);
				}
			}else {
				chart.draw(view, getOptions());
			}
		} catch (Exception e) {
			System.out.println("Error in drawing LineChart."+e.getMessage());
		}		
	}
	
//	@Override
//	public void onResize() {
//		draw();
//	}
	@Override
	public void onResize(ResizeEvent event) {
		draw();
	}
	
	public void removeDoubleClickHandler(){
		if(dcHandlerRegistration != null){
			dcHandlerRegistration.removeHandler();
		}
	}
	
	protected Options getOptions(String title, String hTitle, String vTitle) {
		Options options = Options.create();
		options.setWidth((int)(Window.getClientWidth()*widthRatio));
		options.setHeight((int)(Window.getClientHeight()*heightRatio));
		
		options.setTitle(title);
		
		AxisOptions hao = HorizontalAxisOptions.create();
		hao.setTitle(hTitle);
		options.setHAxisOptions(hao);

		AxisOptions vao = AxisOptions.create();
		vao.setTitle(vTitle);
		options.setVAxisOptions(vao);
		
		options.setLegendTextStyle((TextStyle)getLegendTextStyle());
		return options;
	}
	
	private native JavaScriptObject getLegendTextStyle()/*-{
		return {fontSize: 10};
	}-*/;

}
