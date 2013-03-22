package com.shico.mnm.common.chart;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;

public class LineChartPopup extends PopupPanel {
	LineChartPanel chart;
	double heightRatio, widthRatio;
	
	public LineChartPopup(LineChartPanel chart) {
		super(true, true);
		this.chart = chart;
		this.setGlassEnabled(true);
		this.setAnimationEnabled(true);
		this.chart.removeDoubleClickHandler();
		
		this.heightRatio = chart.heightRatio += 0.25;
		this.widthRatio = chart.widthRatio += 0.25;
		
		this.setStyleName("shico-ChartPopup");
	}
	
	@Override
	public void center() {
		setSize();
		add(chart);
		chart.draw();
		super.center();
	}

	private void setSize(){
		setHeight((Window.getClientHeight()*heightRatio)+"px");
		setWidth((Window.getClientWidth()*widthRatio)+"px");
	}
}
