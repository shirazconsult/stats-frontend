package com.shico.mnm.amq.client.components;

import java.util.HashMap;
import java.util.Map;

import com.shico.mnm.amq.client.AmqSettingsController;
import com.shico.mnm.amq.client.HasData;
import com.shico.mnm.amq.model.AmqRemoteSettingsDS;
import com.shico.mnm.amq.model.MessageListDS;
import com.shico.mnm.amq.model.QueueListDS;
import com.shico.mnm.common.component.PortalWin;
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
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.util.ValueCallback;
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
	QueueListDS datasource;
	String brokerUrl;
	
	public QueueListPortlet(AmqSettingsController settingsController){
		super("Queues");
		
		this.settingsController = settingsController;
		brokerUrl = (String)settingsController.getSetting(AmqRemoteSettingsDS.BROKERURL);
		datasource = new QueueListDS(brokerUrl);

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
			String burl = (String)event.info.get(AmqRemoteSettingsDS.BROKERURL);
			if(burl != null && !burl.trim().isEmpty()){
				if(brokerUrl != null && !burl.equals(brokerUrl)){
					datasource.destroy();
					datasource = new QueueListDS(burl);
					listGrid.setDataSource(datasource);
				}
				update();
			}
			brokerUrl = burl;
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
			listGrid = new ListGrid();
			listGrid.setAlign(Alignment.CENTER);
			listGrid.setWidth100();
			listGrid.setHeight(140);
			listGrid.setCellHeight(24);
			listGrid.setShowAllRecords(true);
			listGrid.setDataSource(datasource); 

			ListGridField nameF = new ListGridField("name", "Name");
			ListGridField consCtnF = new ListGridField("consumerCount", "Consumer Count");
			consCtnF.setAlign(Alignment.CENTER);
			ListGridField prodCntColF = new ListGridField("producerCount", "Producer Count");
			prodCntColF.setAlign(Alignment.CENTER);
			ListGridField enqCntColF = new ListGridField("enqueueCount", "Enqueue Count");
			enqCntColF.setAlign(Alignment.CENTER);
			ListGridField deqCntColF = new ListGridField("dequeueCount", "Dequeue Count");
			deqCntColF.setAlign(Alignment.CENTER);
			ListGridField sizeColF = new ListGridField("queueSize", "Queue Size");
			sizeColF.setAlign(Alignment.CENTER);
			
			listGrid.setSelectionType(SelectionStyle.SINGLE);
			listGrid.setDefaultFields(new ListGridField[]{nameF, consCtnF, prodCntColF, enqCntColF, deqCntColF, sizeColF});
			
			listGrid.addRecordDoubleClickHandler(new RecordDoubleClickHandler() {
				@Override
				public void onRecordDoubleClick(RecordDoubleClickEvent event) {
					Record record = event.getRecord();
					openMessagePortlet(record.getAttribute(QueueListDS.NAME), null);
				}
			});
		}
		return listGrid;
	}
	
	public void update(){
		Criteria criteria = new Criteria(QueueListDS.BROKERNAME, "local");
		listGrid.fetchData(criteria);
	}
	
	private IButton getPurgeButton(){
		IButton btn = new IButton("Purge");
		btn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final ListGridRecord record = listGrid.getSelectedRecord();
				if(record == null){
					SC.say("Please select a destination.");
					return;
				}
				SC.ask("Are you sure you want to purge queue "+record.getAttribute(QueueListDS.NAME)+" ?", new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if(value){
							// purge is sent as update op.
							record.setAttribute(QueueListDS.CMD, QueueListDS.PURGE_CMD);
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
	
	private IButton getDeleteButton(){
		IButton btn = new IButton("Delete");
		btn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final ListGridRecord record = listGrid.getSelectedRecord();
				if(record == null){
					SC.say("Please select a destination.");
					return;
				}
				final String qName = record.getAttribute(QueueListDS.NAME);
				SC.ask("Are you sure you want to delete queue "+qName+" ?", new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if(value){
							listGrid.removeData(record);
							if(msgListPortlet != null && ((PortalWin)getPortalContainer()).hasPortlet(qName)){
								Portlet portlet = ((PortalWin)getPortalContainer()).getPortlet(qName);
								getPortalContainer().removePortlet(portlet);
							}						
						}
					}
				});				
			}
		});
		return btn;
	}

	private IButton getBrowseButton(){
		IButton btn = new IButton("Browse");
		btn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final ListGridRecord record = listGrid.getSelectedRecord();
				if(record == null){
					SC.say("Please select a destination.");
					return;
				}
				openMessagePortlet(record.getAttribute(QueueListDS.NAME), null);
			}
		});
		return btn;
	}

	private IButton getSelectButton(){
		IButton btn = new IButton("Select");
		btn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final ListGridRecord record = listGrid.getSelectedRecord();
				if(record == null){
					SC.say("Please select a destination.");
					return;
				}
				new MsgActionWin(DataEventType.SELECT, getDataRecords(), new ValueCallback() {					
					@Override
					public void execute(String value) {
						openMessagePortlet(record.getAttribute(QueueListDS.NAME), value);
					}
				});
			}
		});
		return btn;
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
	
	private IButton getMoveButton(){
		IButton btn = new IButton("Bulk Move");
		btn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final ListGridRecord record = listGrid.getSelectedRecord();
				if(record == null){
					SC.say("Please select a destination.");
					return;
				}
				int size = Integer.parseInt(record.getAttribute(QueueListDS.QUEUESIZE));
				if(size <= 0){
					SC.say("There is no message in the destination to move.");
					return;
				}
				final String qName = record.getAttribute(QueueListDS.NAME);
				new MsgActionWin(DataEventType.BULK_MOVE, getDataRecords(), new ValueCallback() {
					@Override
					public void execute(final String toQ) {
						if(toQ.equals(qName)){
							SC.say("Cannot move the messages, since the source and the destination are the same.");
							return;
						}
						SC.ask("Are you sure you want to move all the messages from "+qName+" to "+toQ+" ?", new BooleanCallback() {
							@Override
							public void execute(Boolean value) {
								if(value){
									listGrid.updateData(newCopyMoveCMDRecord(qName, toQ, "JMSMessageID like '%'", QueueListDS.MOVE_CMD), new DSCallback() {								
										@Override
										public void execute(DSResponse response, Object rawData, DSRequest request) {
											if(response.getStatus() == 0){
												listGrid.invalidateCache();
												if(msgListPortlet != null && ((PortalWin)getPortalContainer()).hasPortlet(qName)){
													Portlet portlet = ((PortalWin)getPortalContainer()).getPortlet(qName);
													getPortalContainer().removePortlet(portlet);
												}																		
											}											
										}
									});
								}
							}
						});
					};
				});
			};
		});
		return btn;
	}

	private void openMessagePortlet(String qn, String selector){
		if(msgListPortlet == null){
			msgListPortlet = new MessageListPortlet(settingsController);
			msgListPortlet.setPortalContainer(getPortalContainer());
		}
		if(!((PortalWin)getPortalContainer()).hasPortlet(qn)){
			getPortalContainer().addPortlet(msgListPortlet, 0, 1);
		}
		msgListPortlet.restore();
		if(msgListPortlet.isDirty()){
			msgListPortlet.draw();
		}
		msgListPortlet.setQueueListProvider(this);
		msgListPortlet.update(qn, "local", selector);		
	}
	
	// Creates a Recored for putting in the DSRequest for copy and move operations
	private Record newCopyMoveCMDRecord(String fromQ, String toQ, String selector, String copyOrMoveCmd){
		Map<String, String> props = new HashMap<String, String>();
		props.put(QueueListDS.SELECTOR, selector);
		props.put(QueueListDS.NAME, fromQ);
		props.put(QueueListDS.TO_QUEUE, toQ);
		props.put(QueueListDS.BROKERNAME, "local");
		props.put(MessageListDS.CMD, copyOrMoveCmd);
		return new Record(props);
	}
	
	HLayout headerPanel;
	private HLayout getHeaderPanel(){
		headerPanel = new HLayout();
		headerPanel.setAlign(Alignment.RIGHT);
		headerPanel.setTitle("Browse");

		headerPanel.addMember(getBrowseButton());
		headerPanel.addMember(getSelectButton());
		headerPanel.addMember(getMoveButton());
		headerPanel.addMember(getPurgeButton());
		headerPanel.addMember(getDeleteButton());
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
	protected void handleClose() {
		// do nothing. This portlet must not be closed.
	}

	@Override
	public Record[] getDataRecords() {
		RecordList recordList = listGrid.getDataAsRecordList();
		return recordList.getRange(0, recordList.getLength());
	}

}
