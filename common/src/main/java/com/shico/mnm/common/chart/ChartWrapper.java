package com.shico.mnm.common.chart;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDrawOptions;
import com.google.gwt.visualization.client.visualizations.Visualization;

public class ChartWrapper {
	Visualization chart;
	AbstractDataTable data;
	AbstractDrawOptions options;
	double widthRatio;
	double heightRatio;
	
	public ChartWrapper(Visualization chart, AbstractDrawOptions options, double widthRatio,
			double heightRatio) {
		super();
		this.chart = chart;
		this.options = options;
		this.widthRatio = widthRatio;
		this.heightRatio = heightRatio;
	}

	public ChartWrapper(Visualization chart, AbstractDataTable data, AbstractDrawOptions options, double widthRatio,
			double heightRatio) {
		this(chart, options, widthRatio, heightRatio);
		this.data = data;
	}
	
//	public void setChartArea(int percent){
//		options.setChartArea((ChartArea) getChartArea(percent+"%"));
//		options.setWidth((int)(Window.getClientWidth()*widthRatio));
//		options.setHeight((int)(Window.getClientHeight()*heightRatio));
//	}
	
	private native JavaScriptObject getLegendTextStyle()/*-{
		return {color: 'black', fontSize: 10};
	}-*/;

}
