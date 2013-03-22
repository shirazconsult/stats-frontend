package com.shico.mnm.common.component;


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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.DataView;
import com.google.gwt.visualization.client.visualizations.Table;
import com.google.gwt.visualization.client.visualizations.Table.Options;
import com.google.gwt.visualization.client.visualizations.Table.Options.CssClassNames;
import com.shico.mnm.common.client.ChartDataProvider;
import com.shico.mnm.common.event.DataLoadedEventHandler;

public abstract class TableViewPanel extends Composite implements DataLoadedEventHandler, ResizeHandler {
	protected FocusPanel container;
	protected VerticalPanel vPanel;
	protected Table table; 
	protected double widthRatio, heightRatio;
	protected ChartDataProvider dataProvider;
	private HandlerRegistration dcHandlerRegistration;
	
	protected abstract Options getOptions();
	protected abstract DataView getView();
	protected abstract String getPanelTitle();

	public TableViewPanel(ChartDataProvider dataProvider, double widthRatio, double heightRatio) {
		super();
		this.dataProvider = dataProvider; 
		container = new FocusPanel();
		vPanel = new VerticalPanel();
		container.add(vPanel);
		
		Label label = new Label(getPanelTitle());
		label.setStyleName("shico-Title");
		vPanel.add(label);
		
		this.widthRatio = widthRatio;
		this.heightRatio = heightRatio;
		
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
		try {
			DataView view = getView();
			if(table == null){
				table = new Table();
				vPanel.add(table);
			}else {
				table.draw(view, getOptions());
			}
		} catch (Exception e) {
			System.out.println("Error in drawing Table."+e.getMessage());
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
