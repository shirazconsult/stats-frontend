package com.shico.mnm.common.chart;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.visualization.client.visualizations.Visualization;

public class Dashboard {
	static final String SETELEMENT_TWICE_ERROR = "Element may only be set once";

	private JavaScriptObject jso;
	private Element element;
	
	public Dashboard() {
		super();
		Element div = DOM.createDiv();
		jso = createJso(div);
	}

	protected native JavaScriptObject createJso(Element parent) /*-{
	   return new $wnd.google.visualization.Dashboard(parent);
	 }-*/;

	protected native void bind(JavaScriptObject controls, Visualization[] charts)/*-{
		this.@com.google.gwt.visualization.client.visualizations.Visualization::jso.bind(controls, charts);
	}-*/;

}
