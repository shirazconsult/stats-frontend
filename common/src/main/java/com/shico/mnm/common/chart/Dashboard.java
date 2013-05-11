package com.shico.mnm.common.chart;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLFlow;

public class Dashboard extends HTMLFlow {
	static final String SETELEMENT_TWICE_ERROR = "Element may only be set once";

	private static String html = "<div id=\"dashboard_div\"> </div>\n";
	
	private JavaScriptObject jso;
	private Element div;
	
	
	public Dashboard(JavaScriptObject jsObj) {
		super(jsObj);
	}

	public Dashboard(String contents) {
		super(contents);
	}

	public Dashboard() {
		super(html);
	}
	
	@Override
	public String getInnerHTML() {
//		return div.getInnerHTML();
		return html;
	}

	public final native void draw(AbstractDataTable data) /*-{
    	this.@com.shico.mnm.common.chart.Dashboard::jso.draw(data);
  	}-*/;
	
	protected native JavaScriptObject createJso() /*-{
		return new $wnd.google.visualization.Dashboard($doc.getElementById('dashboard_div'));
//		return new $wnd.google.visualization.Dashboard(parent);
	 }-*/;

	protected native void bind(JavaScriptObject controls, JsArray<JavaScriptObject> charts)/*-{
		var dashboard = new $wnd.google.visualization.Dashboard($doc.getElementById('dashboard_div'));
		dashboard.bind(controls, charts);
//		this.@com.shico.mnm.common.chart.Dashboard::jso.bind(controls, charts);
	}-*/;

}
