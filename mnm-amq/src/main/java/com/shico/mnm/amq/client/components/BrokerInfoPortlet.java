package com.shico.mnm.amq.client.components;

import com.shico.mnm.amq.client.AmqClientHandle;
import com.shico.mnm.amq.client.AmqSettingsController;
import com.shico.mnm.amq.model.AmqRemoteSettingsDS;
import com.shico.mnm.amq.model.BrokerInfoDS;
import com.shico.mnm.common.component.PortletWin;
import com.shico.mnm.common.event.DataLoadedEvent;
import com.shico.mnm.common.event.DataLoadedEventHandler;
import com.shico.mnm.common.event.EventBus;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.FormItemValueFormatter;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.HiddenItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.layout.VLayout;

public class BrokerInfoPortlet extends PortletWin implements DataLoadedEventHandler {
	VLayout container;
	DynamicForm aboutInfo;
	DynamicForm generalInfo;
	ValuesManager valuesManager;
	AmqSettingsController settingsController;
	
	public BrokerInfoPortlet(AmqSettingsController settingsController) {
		super("Broker Information");
		this.settingsController = settingsController;
		
		container = new VLayout();
		container.setWidth100();
		container.setMembersMargin(10);
		container.setMargin(10);
				
		setupAboutInfo();
		setupGeneralSettingsInfo();
				
		container.addMember(aboutInfo);
		container.addMember(generalInfo);		
		
		container.setAutoHeight();
		
		addItem(container);
		
		setHeight(170);
		
		valuesManager = new ValuesManager();
		valuesManager.addMember(aboutInfo);
		valuesManager.addMember(generalInfo);
		valuesManager.setDataSource(settingsController.getBrokerInfoDS());
		
		EventBus.instance().addHandler(DataLoadedEvent.TYPE, this);
	}
	
	@Override
	public void onDataLoaded(DataLoadedEvent event) {
		switch (event.eventType) {
		case BROKER_INFO_LOADED_EVENT:
			update();
			break;
		case AMQ_ADMIN_SETTINGS_CHANGED_EVENT:
			String brokerUrl = (String)event.info.get(AmqRemoteSettingsDS.BROKERURL);
			settingsController.setBrokerInfoDS(new BrokerInfoDS(brokerUrl));
			valuesManager.setDataSource(settingsController.getBrokerInfoDS());
			if(brokerUrl != null && !brokerUrl.trim().isEmpty()){
				update();
			}
			break;
		}
	}
	
	public void update() {
		Criteria criteria = new Criteria(BrokerInfoDS.BROKER_NAME, "local");
		valuesManager.fetchData(criteria);
	}

	private DynamicForm setupAboutInfo(){
		aboutInfo = new DynamicForm();
		aboutInfo.setWidth100();
		aboutInfo.setIsGroup(true);
		aboutInfo.setGroupTitle("About");
		aboutInfo.setCellPadding(5);

		aboutInfo.setNumCols(6);
		StaticTextItem nameItem = new StaticTextItem(BrokerInfoDS.BROKER_NAME);
		StaticTextItem versionItem = new StaticTextItem(BrokerInfoDS.BROKER_VER);
		StaticTextItem masterItem = new StaticTextItem(BrokerInfoDS.IS_SLAVE);
		StaticTextItem persistent = new StaticTextItem(BrokerInfoDS.IS_PERSISTENT);		
		StaticTextItem idItem = new StaticTextItem(BrokerInfoDS.BROKER_ID);
		idItem.setColSpan(2);
		
		aboutInfo.setItems(nameItem, versionItem, masterItem, persistent, idItem);
		
		return aboutInfo;
	}

	private DynamicForm setupGeneralSettingsInfo(){
		generalInfo = new DynamicForm();
		generalInfo.setWidth100();
		generalInfo.setIsGroup(true);
		generalInfo.setGroupTitle("General Settings");
		generalInfo.setCellPadding(5);
		
		generalInfo.setNumCols(6);
		
		StaticTextItem memItem = new StaticTextItem(BrokerInfoDS.MEM_LIMIT);
		memItem.setValueFormatter(byteFormatter);
		StaticTextItem storeItem = new StaticTextItem(BrokerInfoDS.STORE_LIMIT);
		storeItem.setValueFormatter(byteFormatter);
		StaticTextItem tempItem = new StaticTextItem(BrokerInfoDS.TEMP_LIMIT);
		tempItem.setValueFormatter(byteFormatter);
		StaticTextItem dataDirItem = new StaticTextItem(BrokerInfoDS.DATA_DIR);
		dataDirItem.setColSpan(3);
		
		HiddenItem appItem = new HiddenItem(AmqRemoteSettingsDS.APP);
		appItem.setValue(AmqClientHandle.APP_NAME);

		generalInfo.setItems(memItem, storeItem, tempItem, dataDirItem, appItem);
		
		return generalInfo;
	}
		
	@Override
	protected void handleRefresh(ClickEvent event) {
		update();
	}

	@Override
	protected void handleSettings(ClickEvent event) {
		SC.say("Not implemented yet.");
	}

	@Override
	protected void handleHelp(ClickEvent event) {
		SC.say("Not implemented yet.");
	}
	
	@Override
	protected void handleClose() {
		// do nothing. this portlet must not be closed
	}

	FormItemValueFormatter byteFormatter = new FormItemValueFormatter() {
		
		@Override
		public String formatValue(Object value, Record record, DynamicForm form,
				FormItem item) {
			if(value != null){
				float f = Float.valueOf((String)value);
				return (f / 1048576)+" MB";
			}
			return "";
		}
	};
}
