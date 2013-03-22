package com.shico.mnm.common.chart;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.visualization.client.DataView;
import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.HorizontalAxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.LineChart;
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

public abstract class LineChartPortlet extends PortletWin implements DataLoadedEventHandler, MaximizeClickHandler, RestoreClickHandler {
	protected VLayout container;
	protected LineChart chart; 
	protected double originalWidthRatio, originalHeightRatio, widthRatio, heightRatio;
	protected LineChartPopup chartPopup;
	protected ChartDataProvider dataProvider;
	private HandlerRegistration dcHandlerRegistration;
	
	protected abstract Options getOptions();
	protected abstract DataView getView();
	
	public LineChartPortlet(ChartDataProvider dataProvider, double widthRatio, double heightRatio) {
		super();
		this.dataProvider = dataProvider; 
		setHeight("25%");

		container = new VLayout();
		container.setWidth100();
		
		this.originalWidthRatio = this.widthRatio = widthRatio;
		this.originalHeightRatio = this.heightRatio = heightRatio;		
		
		addItem(container);
		
		addMaximizeClickHandler(this);
		addRestoreClickHandler(this);
	}
	
	public void draw(){
		try {
			DataView view = getView();
			if(chart == null){
				chart = new LineChart(view, getOptions());
				if (chart != null) {
					container.addMember(chart);
				}
			}else {
				chart.draw(view, getOptions());
			}
		} catch (Exception e) {
			System.out.println("Error in drawing LineChart."+e.getMessage());
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
