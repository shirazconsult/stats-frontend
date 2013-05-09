package com.shico.mnm.agg.components;

import com.shico.mnm.agg.client.AggClientHandle;
import com.shico.mnm.agg.client.AggSettingsController;
import com.shico.mnm.agg.model.AggRemoteSettingsDS;
import com.shico.mnm.agg.model.AggregatorInfoDS;
import com.shico.mnm.common.component.PortletWin;
import com.shico.mnm.common.event.DataLoadedEvent;
import com.shico.mnm.common.event.DataLoadedEventHandler;
import com.shico.mnm.common.event.EventBus;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.HiddenItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.layout.VLayout;

public class AggregatorInfoPortlet extends PortletWin implements DataLoadedEventHandler {
	VLayout container;
	DynamicForm aboutInfo;
	DynamicForm generalInfo;
	ValuesManager valuesManager;
	AggSettingsController settingsController;
	
	public AggregatorInfoPortlet(AggSettingsController settingsController) {
		super("Aggregator Information");
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
		
		setHeight(200);
		
		valuesManager = new ValuesManager();
		valuesManager.addMember(aboutInfo);
		valuesManager.addMember(generalInfo);
		valuesManager.setDataSource(settingsController.getAggregatorInfoDS());
		
		EventBus.instance().addHandler(DataLoadedEvent.TYPE, this);
	}
	
	@Override
	public void onDataLoaded(DataLoadedEvent event) {
		switch (event.eventType) {
		case AGG_INFO_LOADED_EVENT:
			update();
			break;
		case AGG_ADMIN_SETTINGS_CHANGED_EVENT:
			String aggregatorUrl = (String)event.info.get(AggRemoteSettingsDS.AGGREGATORURL);
			settingsController.setAggregatorInfoDS(new AggregatorInfoDS(aggregatorUrl));
			valuesManager.setDataSource(settingsController.getAggregatorInfoDS());
			if(aggregatorUrl != null && !aggregatorUrl.trim().isEmpty()){
				update();
			}
			break;
		}
	}
	
	public void update() {
		Criteria criteria = new Criteria(AggregatorInfoDS.AGG_NAME, "localhost");
		valuesManager.fetchData(criteria);
	}

	private DynamicForm setupAboutInfo(){
		aboutInfo = new DynamicForm();
		aboutInfo.setWidth100();
		aboutInfo.setIsGroup(true);
		aboutInfo.setGroupTitle("About");
		aboutInfo.setCellPadding(5);

		aboutInfo.setNumCols(6);
		StaticTextItem nameItem = new StaticTextItem(AggregatorInfoDS.AGG_NAME, "Name");
		StaticTextItem versionItem = new StaticTextItem(AggregatorInfoDS.AGG_VER, "Version");
		StaticTextItem protoVerItem = new StaticTextItem(AggregatorInfoDS.PROTO_VER, "Protocol Version");
		StaticTextItem homeDirItem = new StaticTextItem(AggregatorInfoDS.HOME_DIR, "Home Directory");
		homeDirItem.setColSpan(2);
		
		aboutInfo.setItems(nameItem, versionItem, protoVerItem, homeDirItem);
		
		return aboutInfo;
	}

	private DynamicForm setupGeneralSettingsInfo(){
		generalInfo = new DynamicForm();
		generalInfo.setWidth100();
		generalInfo.setIsGroup(true);
		generalInfo.setGroupTitle("General Settings");
		generalInfo.setCellPadding(5);
		
		generalInfo.setNumCols(8);
		
		StaticTextItem uptimeItem = new StaticTextItem(AggregatorInfoDS.UPTIME, "Uptime");
		StaticTextItem lastExchComplItem = new StaticTextItem(AggregatorInfoDS.LAST_EXCH_COMPL_TS, "Last Completed Exchange");
		StaticTextItem exchTotleItem = new StaticTextItem(AggregatorInfoDS.EXCH_TOTAL, "Completed Exchanges");
		StaticTextItem exchComplItem = new StaticTextItem(AggregatorInfoDS.EXCH_COMPL, "Total Exchanges");
		uptimeItem.setColSpan(2);
		lastExchComplItem.setColSpan(2);
		exchTotleItem.setColSpan(2);
		exchComplItem.setColSpan(2);
		
		HiddenItem appItem = new HiddenItem(AggRemoteSettingsDS.APP);
		appItem.setValue(AggClientHandle.APP_NAME);

		generalInfo.setItems(uptimeItem, lastExchComplItem, exchTotleItem, exchComplItem);
		
		return generalInfo;
	}
		
	@Override
	protected void handleRefresh() {
		update();
	}

	@Override
	protected void handleSettings() {
		SC.say("Not implemented yet.");
	}

	@Override
	protected void handleHelp() {
		SC.say("Not implemented yet.");
	}
	
	@Override
	protected void handleClose() {
		// do nothing. this portlet must not be closed
	}
}
