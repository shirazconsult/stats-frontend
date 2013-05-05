package com.shico.mnm.agg.components;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.Window;
import com.shico.mnm.agg.client.AggClientHandle;
import com.shico.mnm.agg.client.AggSettingsController;
import com.shico.mnm.agg.model.AggRemoteSettingsDS;
import com.shico.mnm.common.component.PortletWin;
import com.shico.mnm.common.event.DataEventType;
import com.shico.mnm.common.event.DataLoadedEvent;
import com.shico.mnm.common.event.DataLoadedEventHandler;
import com.shico.mnm.common.event.EventBus;
import com.shico.mnm.common.model.SettingsValuesManager;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.HiddenItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class AggChartSettingsPortlet extends PortletWin implements DataLoadedEventHandler {
	private static Logger logger = Logger.getLogger("AggChartSettingsPortlet");
	
	public final static String TITLE = "Chart Settings";
	VLayout container;
	AggSettingsController settingsConroller;
	DynamicForm userSettingsform;
	DynamicForm chartSettingsform;
	SettingsValuesManager valuesManager;
	
	public AggChartSettingsPortlet(AggSettingsController settingsConroller) {
		super(TITLE);
		this.settingsConroller = settingsConroller;
		
		container = new VLayout();
		container.setAlign(Alignment.CENTER);
		container.setAlign(VerticalAlignment.CENTER);
		container.setMembersMargin(10);
		container.setMargin(10);
		setHeight(250);
		
		container.addMember(getUserSettingsForm());
		container.addMember(getAdminSettingsForm());
		container.addMember(getButtonPanel());
		
		addItem(container);

		valuesManager = new SettingsValuesManager(AggClientHandle.APP_NAME);
		valuesManager.addMember(getUserSettingsForm());
		valuesManager.addMember(getAdminSettingsForm());
		valuesManager.setDataSource(settingsConroller.getSettingsDS());

		EventBus.instance().addHandler(DataLoadedEvent.TYPE, this);
	}

	private DynamicForm getUserSettingsForm() {
		if(userSettingsform == null){
			userSettingsform = new DynamicForm();
			userSettingsform.setIsGroup(true);
			userSettingsform.setGroupTitle("User Settings");
			userSettingsform.setTitleOrientation(TitleOrientation.TOP);
			userSettingsform.setWidth100();
			userSettingsform.setCellPadding(5);
			userSettingsform.setNumCols(2);
			
			TextItem userItem = new TextItem(AggRemoteSettingsDS.CHARTUSER, "User name");
			TextItem pwdItem = new PasswordItem(AggRemoteSettingsDS.CHARTPWD, "Password");

			userSettingsform.setFields(userItem, pwdItem);			
		}
		return userSettingsform;
	}

	private DynamicForm getAdminSettingsForm() {
		if(chartSettingsform == null){
			chartSettingsform = new DynamicForm();
			chartSettingsform.setIsGroup(true);
			chartSettingsform.setGroupTitle("Chart Settings");
			chartSettingsform.setTitleOrientation(TitleOrientation.TOP);
			chartSettingsform.setWidth100();
			chartSettingsform.setCellPadding(5);
			chartSettingsform.setNumCols(4);
			
			HiddenItem appItem = new HiddenItem(AggRemoteSettingsDS.APP);
			appItem.setValue(AggClientHandle.APP_NAME);
			TextItem urlItem = new TextItem(AggRemoteSettingsDS.CHARTURL, "Server URL");
			urlItem.setWidth(400);
			urlItem.setColSpan(4);
			TextItem refreshItem = new TextItem(AggRemoteSettingsDS.CHARTREFRESHINTERVAL, "Refresh Interval (Sec.)");
			TextItem viewItem = new TextItem(AggRemoteSettingsDS.CHARTWINSIZE, "View Size (Min.)");

			refreshItem.setLength(3);
			viewItem.setLength(5);
			chartSettingsform.setFields(urlItem, refreshItem, viewItem, appItem);
		}
		return chartSettingsform;
	}

	public HLayout getButtonPanel(){
		IButton resetBtn = new IButton("Reset");
		resetBtn.addClickHandler(new ClickHandler() {				
			@Override
			public void onClick(ClickEvent event) {
				valuesManager.resetValues();
			}
		});
		IButton submitBtn = new IButton("Save");
		submitBtn.addClickHandler(new ClickHandler() {				
			@Override
			public void onClick(ClickEvent event) {
				valuesManager.saveData(new Callback<Map, String>() {					
					@Override
					public void onSuccess(Map result) {
						if(result != null){
							settingsConroller.getSettingsMap().putAll(result);
							EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.AGG_CHART_SETTINGS_CHANGED_EVENT, result));
						}else{
							logger.log(Level.SEVERE, "Saving of settings probably failed. The returned settings is null.");
						}
					}					
					@Override
					public void onFailure(String reason) {
						Window.alert(reason);
					}
				});
			}
		});
		
		HLayout panel = new HLayout();
		panel.setWidth100();
		panel.setAlign(Alignment.LEFT);
		
		panel.addMember(submitBtn);
		panel.addMember(resetBtn);
		
		return panel;
	}
	
	public void update(){
		Criteria criteria = new Criteria(AggRemoteSettingsDS.APP, AggClientHandle.APP_NAME);
		valuesManager.fetchData(criteria);
	}
	
	@Override
	protected void handleRefresh() {
		update();
	}

	@Override
	protected void handleSettings() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void handleHelp() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDataLoaded(DataLoadedEvent event) {
		switch(event.eventType){
		case AGG_SETTINGS_LOADED_EVENT:
			update();
			break;
		}
	}

}
