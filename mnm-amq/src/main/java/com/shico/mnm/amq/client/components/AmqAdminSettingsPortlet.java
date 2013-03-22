package com.shico.mnm.amq.client.components;

import java.util.Map;

import com.shico.mnm.amq.client.AmqClientHandle;
import com.shico.mnm.amq.client.AmqSettingsController;
import com.shico.mnm.amq.model.AmqRemoteSettingsDS;
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
import com.smartgwt.client.widgets.form.events.SubmitValuesEvent;
import com.smartgwt.client.widgets.form.events.SubmitValuesHandler;
import com.smartgwt.client.widgets.form.fields.HiddenItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class AmqAdminSettingsPortlet extends PortletWin implements DataLoadedEventHandler {
	public final static String TITLE = "Admin Settings";
	VLayout container;
	AmqSettingsController settingsConroller;
	DynamicForm userSettingsform;
	DynamicForm adminSettingsform;
	SettingsValuesManager valuesManager;
	
	public AmqAdminSettingsPortlet(AmqSettingsController settingsConroller) {
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
		
		valuesManager = new SettingsValuesManager();
		valuesManager.addMember(getUserSettingsForm());
		valuesManager.addMember(getAdminSettingsForm());
		valuesManager.setDataSource(settingsConroller.getSettings());
		
		valuesManager.addSubmitValuesHandler(new SubmitValuesHandler() {
			
			@Override
			public void onSubmitValues(SubmitValuesEvent event) {
				Map valuesAsMap = event.getValuesAsMap();
				System.out.println("00000000 "+event.toString());
				if(valuesAsMap != null){
					for (Object key : valuesAsMap.keySet()) {
						System.out.println("0000000 "+key+" = "+valuesAsMap.get(key));
					}
				}
			}
		});
		
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
			userSettingsform.setNumCols(4);
			
			TextItem userItem = new TextItem(AmqRemoteSettingsDS.BROKERUSER, "User name");
			TextItem pwdItem = new PasswordItem(AmqRemoteSettingsDS.BROKERPWD, "Password");

			userSettingsform.setFields(userItem, pwdItem);
			
			// validators
			userSettingsform.setValidateOnChange(true);
			userItem.setRequired(true);
			pwdItem.setRequired(true);
		}
		return userSettingsform;
	}

	private DynamicForm getAdminSettingsForm() {
		if(adminSettingsform == null){
			adminSettingsform = new DynamicForm();
			adminSettingsform.setIsGroup(true);
			adminSettingsform.setGroupTitle("Admin Settings");
			adminSettingsform.setTitleOrientation(TitleOrientation.TOP);
			adminSettingsform.setWidth100();
			adminSettingsform.setCellPadding(5);
			adminSettingsform.setNumCols(2);
			
			HiddenItem appItem = new HiddenItem(AmqRemoteSettingsDS.APP);
			appItem.setValue(AmqClientHandle.APP_NAME);
			TextItem urlItem = new TextItem(AmqRemoteSettingsDS.BROKERURL, "Broker Url");
			urlItem.setWidth(400);
			urlItem.setColSpan(2);
			
			adminSettingsform.setFields(urlItem, appItem);
			adminSettingsform.setValidateOnChange(true);
			urlItem.setRequired(true);
		}
		return adminSettingsform;
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
				valuesManager.saveData();
				EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.AMQ_ADMIN_SETTINGS_CHANGED_EVENT));
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
		Criteria criteria = new Criteria(AmqRemoteSettingsDS.APP, AmqClientHandle.APP_NAME);
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
		case AMQ_SETTINGS_LOADED_EVENT:
		case AMQ_ADMIN_SETTINGS_CHANGED_EVENT:
			update();
			break;
		}
	}

}
