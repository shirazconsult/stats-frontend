package com.shico.mnm.common.chart;


import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.visualization.client.DataView;
import com.google.gwt.visualization.client.visualizations.Table;
import com.google.gwt.visualization.client.visualizations.Table.Options;
import com.google.gwt.visualization.client.visualizations.Table.Options.CssClassNames;
import com.shico.mnm.common.client.ChartDataProvider;
import com.shico.mnm.common.component.PortletWin;
import com.shico.mnm.common.event.DataLoadedEventHandler;
import com.smartgwt.client.widgets.events.MaximizeClickEvent;
import com.smartgwt.client.widgets.events.MaximizeClickHandler;
import com.smartgwt.client.widgets.events.RestoreClickEvent;
import com.smartgwt.client.widgets.events.RestoreClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;

public abstract class TableViewPortlet extends PortletWin implements DataLoadedEventHandler, MaximizeClickHandler, RestoreClickHandler {
	protected VLayout container;
	protected VLayout vPanel;
	protected Table table; 
	protected double originalWidthRatio, originalHeightRatio, widthRatio, heightRatio;
	protected ChartDataProvider dataProvider;
	private HandlerRegistration dcHandlerRegistration;
	
	protected abstract Options getOptions();
	protected abstract DataView getView();
	protected abstract String getPanelTitle();

	public TableViewPortlet(ChartDataProvider dataProvider, double widthRatio, double heightRatio) {
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
			DataView view = getView();
			if(table == null){
				table = new Table();
				vPanel.addMember(table);
			}else {
				table.draw(view, getOptions());
			}
		} catch (Exception e) {
			System.out.println("Error in drawing Table."+e.getMessage());
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

	protected Options getOptions(String title) {
		Options options = Options.create();
		options.setWidth(String.valueOf((int)(Window.getClientWidth()*widthRatio)));
		options.setHeight(String.valueOf((int)(Window.getClientHeight()*heightRatio)));
		
		options.setCssClassNames((CssClassNames) getCssClassNames());
		options.setAllowHtml(true);
		
		table.setTitle(title);

		return options;
	}

	private native JavaScriptObject getCssClassNames()/*-{
		var cssClassNames = {headerRow: 'shico-VTabel-HeaderRow',
    						selectedTableRow: 'shico-VTable-SelectedTableRow'
    						};
		return cssClassNames;
	}-*/;
}
