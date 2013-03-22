package com.shico.mnm.amq.client.components;

import com.shico.mnm.amq.client.AmqSettingsController;
import com.shico.mnm.amq.client.HasData;
import com.shico.mnm.amq.model.QueueListDS;
import com.shico.mnm.common.component.PortletWin;
import com.shico.mnm.common.event.DataEventType;
import com.shico.mnm.common.event.DataLoadedEvent;
import com.shico.mnm.common.event.DataLoadedEventHandler;
import com.shico.mnm.common.event.EventBus;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.util.ValueCallback;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Portlet;
import com.smartgwt.client.widgets.layout.VLayout;

public class QueueListPortlet extends PortletWin implements DataLoadedEventHandler, HasData {
	VLayout container;
	ListGrid listGrid;
	MessageListPortlet msgListPortlet;
	AmqSettingsController settingsController;
	
	public QueueListPortlet(AmqSettingsController settingsController){
		super("Queues");
		
		this.settingsController = settingsController;
		
		container = new VLayout();				
		container.setWidth100();
		container.setAutoHeight();
		container.setMembersMargin(10);
		container.setMargin(10);
		
		container.addMember(getHeaderPanel());
		container.addMember(getListGrid());
		
		addItem(container);
		
		setHeight(220);
		
		EventBus.instance().addHandler(DataLoadedEvent.TYPE, this);
	}
	
	@SuppressWarnings("incomplete-switch")
	@Override
	public void onDataLoaded(DataLoadedEvent event) {
		switch (event.eventType) {
		case AMQ_ADMIN_SETTINGS_CHANGED_EVENT:
			update();
			break;
		case MSG_EVENT:
			switch(event.getMsgActionType()){
			case DELETE:
			case MOVE:
			case COPY:
				listGrid.invalidateCache();
				break;
			}
		}
	}

	private ListGrid getListGrid(){
		if(listGrid == null){
			listGrid = new ListGrid(){

				@Override
				protected Canvas createRecordComponent(ListGridRecord record, Integer colNum) {
					String fieldName = this.getFieldName(colNum);
					
					if(fieldName.equals("operations")){
						return createButtonPanel(record);
					}
					return super.createRecordComponent(record, colNum);
				}
			};
			listGrid.setWidth100();
			listGrid.setHeight(140);
			listGrid.setShowAllRecords(true);  
			listGrid.setCellHeight(24);
			listGrid.setShowRecordComponents(true);          
			listGrid.setShowRecordComponentsByCell(true);  
			listGrid.setDataSource(settingsController.getQueueListDS()); 

			ListGridField nameF = new ListGridField("name", "Name");
			ListGridField consCtnF = new ListGridField("consumerCount", "Consumer Count");
			consCtnF.setAlign(Alignment.CENTER);
			ListGridField prodCntColF = new ListGridField("producerCount", "Producer Count");
			prodCntColF.setAlign(Alignment.CENTER);
			ListGridField enqCntColF = new ListGridField("enqueueCount", "Enqueue Count");
			enqCntColF.setAlign(Alignment.CENTER);
			ListGridField deqCntColF = new ListGridField("dequeueCount", "Dequeue Count");
			deqCntColF.setAlign(Alignment.CENTER);
			ListGridField expCntColF = new ListGridField("expiredCount", "Expired Count");
			expCntColF.setAlign(Alignment.CENTER);
			ListGridField sizeColF = new ListGridField("queueSize", "Queue Size");
			sizeColF.setAlign(Alignment.CENTER);
			ListGridField buttonF = new ListGridField("operations", "Operations");
			buttonF.setAlign(Alignment.CENTER);
			buttonF.setWidth(150);
			
			listGrid.setFields(nameF, consCtnF, prodCntColF, enqCntColF, deqCntColF, expCntColF, sizeColF, buttonF);		
			
			listGrid.addRecordDoubleClickHandler(new RecordDoubleClickHandler() {
				@Override
				public void onRecordDoubleClick(RecordDoubleClickEvent event) {
					Record record = event.getRecord();
					openMessagePortlet(record.getAttribute(QueueListDS.NAME));
				}
			});

		}
		return listGrid;
	}
	
	public void update(){
		Criteria criteria = new Criteria(QueueListDS.BROKERNAME, "local");
		listGrid.fetchData(criteria);
	}
	
	private IButton getPurgeButton(final ListGridRecord record){
		IButton btn = new IButton("purge");
		btn.setHeight(20);
		btn.setWidth(45);
		btn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SC.ask("Are you sure you want to purge queue "+record.getAttribute(QueueListDS.NAME)+" ?", new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if(value){
							// purge is sent as update op.
							listGrid.updateData(record, new DSCallback() {								
								@Override
								public void execute(DSResponse response, Object rawData, DSRequest request) {
									if(response.getStatus() == 0){
										listGrid.invalidateCache();
									}
									
								}
							});
							
						}
					}
				});
			}
		});
		return btn;
	}
	
	private IButton getDeleteButton(final ListGridRecord record){
		IButton btn = new IButton("delete");
		btn.setHeight(20);
		btn.setWidth(45);
		btn.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				final String qName = record.getAttribute(QueueListDS.NAME);
				SC.ask("Are you sure you want to delete queue "+qName+" ?", new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if(value){
							listGrid.removeData(record);
							if(msgListPortlet != null && getPortalContainer().hasPortlet(qName)){
								Portlet portlet = getPortalContainer().getPortlet(qName);
								getPortalContainer().removePortlet(portlet);
							}						
						}
					}
				});				
			}
		});
		return btn;
	}

	private IButton getBrowseButton(final ListGridRecord record){
		IButton btn = new IButton("browse");
		btn.setHeight(20);
		btn.setWidth(45);
		btn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				openMessagePortlet(record.getAttribute(QueueListDS.NAME));
			}
		});
		return btn;
	}

	private void openMessagePortlet(String qn){
		if(msgListPortlet == null){
			msgListPortlet = new MessageListPortlet(settingsController);
			msgListPortlet.setPortalContainer(getPortalContainer());
		}
		if(!getPortalContainer().hasPortlet(qn)){
			getPortalContainer().addPortlet(msgListPortlet, 0, 1);
		}
		msgListPortlet.restore();
		msgListPortlet.setHeight(300);
		if(msgListPortlet.isDirty()){
			msgListPortlet.draw();
		}
		msgListPortlet.setQueueListProvider(this);
		msgListPortlet.update(qn, "local");
	}
	private HLayout createButtonPanel(ListGridRecord record){	
		HLayout btnPanel = new HLayout(2);
		btnPanel.setAlign(Alignment.CENTER);
		btnPanel.setAlign(VerticalAlignment.CENTER);
		btnPanel.setHeight(24);
		
		btnPanel.addMember(getBrowseButton(record));
		btnPanel.addMember(getPurgeButton(record));
		btnPanel.addMember(getDeleteButton(record));
		
		return btnPanel;
	}
	
	private IButton getAddBtn(){
		IButton btn = new IButton("Add");
		btn.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				new MsgActionWin(DataEventType.ADD_Q, getDataRecords(), new ValueCallback() {
					@Override
					public void execute(String value) {
						Record record = new Record();
						record.setAttribute(QueueListDS.NAME, value);
						record.setAttribute(QueueListDS.BROKERNAME, "local");
						listGrid.addData(record);		
					}
				});
			}
		});
		return btn;
	}

	HLayout headerPanel;
	private HLayout getHeaderPanel(){
		headerPanel = new HLayout();
		headerPanel.setAlign(Alignment.RIGHT);
		headerPanel.setTitle("Browse");

		headerPanel.addMember(getAddBtn());
		headerPanel.setHeight(22);
		
		return headerPanel;
	}
	
	@Override
	protected void handleRefresh() {
		listGrid.invalidateCache();
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
	public Record[] getDataRecords() {
		RecordList recordList = listGrid.getDataAsRecordList();
		return recordList.getRange(0, recordList.getLength());
	}

}
