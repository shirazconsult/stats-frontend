package com.shico.mnm.common.chart;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.visualizations.corechart.CoreChart;
import com.google.gwt.visualization.client.visualizations.corechart.Options;

public class BubbleChart extends CoreChart {

	/**
	 * Creates a new chart widget
	 */
	public BubbleChart() {
		super();
	}

	public BubbleChart(AbstractDataTable data, Options options) {
		super(data, options);
		options.setType(CoreChart.Type.NONE);
	}


	@Override
	protected native JavaScriptObject createJso(Element parent) /*-{
    	return new $wnd.google.visualization.BubbleChart(parent);
	}-*/;

}