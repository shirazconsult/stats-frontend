package com.shico.mnm.amq.client.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shico.mnm.amq.client.AmqSettingsController;
import com.shico.mnm.amq.client.HasData;
import com.shico.mnm.amq.model.AmqRemoteSettingsDS;
import com.shico.mnm.amq.model.MessageListDS;
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
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.SelectionAppearance;
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
import com.smartgwt.client.widgets.layout.VLayout;

public class MessageListPortlet extends PortletWin implements DataLoadedEventHandler {
	VLayout container;
	MessageViewPortlet msgViewPortlet;
	ListGrid listGrid;
	String title;
	MessageListDS datasource;
	AmqSettingsController settingsController;
	HasData queueListProvider;
	
	public MessageListPortlet(AmqSettingsController settingsController){
		this.settingsController = settingsController;

		String restUrl = (String)settingsController.getSetting(AmqRemoteSettingsDS.BROKERURL);
		datasource = new MessageListDS("messageListDS", restUrl+"/message");

		container = new VLayout();
		container.setWidth100();
		container.setAutoHeight();
		container.setMembersMargin(10);
		container.setMargin(10);
		
		container.addMember(getHeaderPanel());
		container.addMember(getListGrid());
		container.setWidth("100%");
			    
		addItem(container);
				
		EventBus.instance().addHandler(DataLoadedEvent.TYPE, this);
	}
			
	public void update(String qn, String broker){
		Criteria criteria = new Criteria(MessageListDS.QUEUE, qn);
		criteria.addCriteria(MessageListDS.BROKER, broker);
		
		listGrid.fetchData(criteria);
		
		headerPanel.setTitle(qn);
		setTitle(qn);
	}
	
	@SuppressWarnings("incomplete-switch")
	@Override
	public void onDataLoaded(DataLoadedEvent event) {
		switch (event.eventType) {
		case MSG_EVENT:
			switch(event.getMsgActionType()){
			case DELETE:
			case MOVE:
				if(event.getSource() != MessageListPortlet.class){
					listGrid.invalidateCache();
				}
				break;
			}
		}
	}

	private ListGrid getListGrid(){
		if(listGrid == null){
			listGrid = new ListGrid();
			listGrid.setWidth100();
			listGrid.setHeight(250);
			listGrid.setShowAllRecords(true);  
			listGrid.setCellHeight(24);
			listGrid.setDataSource(datasource);
//			listGrid.setDateFormatter(DateDisplayFormat.TOLOCALESTRING);
			listGrid.setSelectionAppearance(SelectionAppearance.CHECKBOX);
			listGrid.setCanSelectAll(true);
			
			ListGridField msgIdF = new ListGridField(MessageListDS.MESSAGEID, "Message ID");
			msgIdF.setWidth(310);
			ListGridField timestampF = new ListGridField(MessageListDS.TIMESTAMP, "Timestamp");
			timestampF.setWidth(250);
			timestampF.setAlign(Alignment.LEFT);
			ListGridField expireF = new ListGridField(MessageListDS.EXPIRATION, "Expiration");
			expireF.setAlign(Alignment.CENTER);
			ListGridField delModeF = new ListGridField(MessageListDS.DELIVERYMODE, "Delivery mode");
			ListGridField typeF = new ListGridField(MessageListDS.TYPE, "Type");
			ListGridField prioF = new ListGridField(MessageListDS.PRIORITY, "Priority");
			prioF.setAlign(Alignment.CENTER);
			
			listGrid.setAutoFitClipFields(MessageListDS.TYPE, MessageListDS.PRIORITY, MessageListDS.EXPIRATION, MessageListDS.DELIVERYMODE);
			
			listGrid.setDefaultFields(new ListGridField[]{msgIdF, timestampF, expireF, delModeF, typeF, prioF});	
			
			listGrid.addRecordDoubleClickHandler(new RecordDoubleClickHandler() {
				@Override
				public void onRecordDoubleClick(RecordDoubleClickEvent event) {
					Record record = event.getRecord();
					if(msgViewPortlet == null){
						msgViewPortlet = new MessageViewPortlet(settingsController);
						msgViewPortlet.setPortalContainer(getPortalContainer());
					}
					if(!getPortalContainer().hasPortlet(MessageViewPortlet.TITLE)){
						getPortalContainer().addPortlet(msgViewPortlet, 0, 2);
					}
					msgViewPortlet.restore();
					msgViewPortlet.setHeight(250);
					if(msgViewPortlet.isDirty()){
						msgViewPortlet.draw();
					}
					msgViewPortlet.setQueueListProvider(queueListProvider);
					msgViewPortlet.update(record);
				}
			});
		}
		return listGrid;
	}
	
	private IButton getMoveBtn(){
		IButton btn = new IButton("Move");
		btn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				new MsgActionWin(DataEventType.MOVE, queueListProvider.getDataRecords(), new ValueCallback() {
					@Override
					public void execute(final String value) {
						final ListGridRecord[] recs = getListGrid().getSelectedRecords();
						if(recs == null || recs.length == 0){
							SC.say("Please select one or more messages.");
							return;
						}
						listGrid.updateData(newCopyMoveCMDRecord(recs, MessageListDS.MOVE_CMD, value), new DSCallback() {							
							@Override
							public void execute(DSResponse response, Object rawData, DSRequest request) {
								if(response.getStatus() == DSResponse.STATUS_SUCCESS){
									listGrid.invalidateCache();
									Map<String, Object> info = getMsgEventInfo(extractMsgIds(recs), DataEventType.MOVE, getTitle(), value);
									EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.MSG_EVENT, info, MessageListPortlet.class));
								}
							}
						});
					}
				});
			}
		});
		return btn;
	}

	private IButton getCopyBtn(){
		IButton btn = new IButton("Copy");
		btn.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				new MsgActionWin(DataEventType.COPY, queueListProvider.getDataRecords(), new ValueCallback() {
					@Override
					public void execute(final String value) {
						final ListGridRecord[] recs = getListGrid().getSelectedRecords();
						if(recs == null || recs.length == 0){
							SC.say("Please select one or more messages.");
							return;
						}
						listGrid.updateData(newCopyMoveCMDRecord(recs, MessageListDS.COPY_CMD, value), new DSCallback() {							
							@Override
							public void execute(DSResponse response, Object rawData, DSRequest request) {
								if(response.getStatus() == DSResponse.STATUS_SUCCESS){
									listGrid.invalidateCache();
									Map<String, Object> info = getMsgEventInfo(extractMsgIds(recs), DataEventType.COPY, getTitle(), value);
									EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.MSG_EVENT, info, MessageListPortlet.class));
								}
							}
						});
					}
				});
			}
		});
		return btn;
	}

	private IButton getDeleteBtn(){
		IButton btn = new IButton("Delete");
		btn.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				final ListGridRecord[] recs = getListGrid().getSelectedRecords();
				if(recs == null || recs.length == 0){
					SC.say("Please select one or more messages.");
					return;
				}
				SC.confirm("Are you sure you want to delete the selected messages ?", new BooleanCallback() {					
					@Override
					public void execute(Boolean value) {
						if(recs.length == 1){
							listGrid.removeSelectedData();
						}else{
							// bulk deletes are handled differently
							listGrid.removeData(newCopyMoveCMDRecord(recs, null, null), new DSCallback() {
								@Override
								public void execute(DSResponse response, Object rawData, DSRequest request) {
									if(response.getStatus() == DSResponse.STATUS_SUCCESS){
										listGrid.invalidateCache();
									}
								}
							});
						}
						Map<String, Object> info = getMsgEventInfo(extractMsgIds(recs), DataEventType.DELETE, getTitle(), null);
						EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.MSG_EVENT, info, MessageListPortlet.class));				
					}
				});
			}
		});
		return btn;
	}

	// Creates a Recored for putting in the DSRequest for copy and move and bulk-delete operations
	private Record newCopyMoveCMDRecord(ListGridRecord[] recs, String copyOrMoveCmd, String toQ){
		StringBuffer sb = new StringBuffer();
		for (ListGridRecord rec : recs) {
			sb.append(rec.getAttributeAsString(MessageListDS.MESSAGEID)).append(",");
		}
		sb.deleteCharAt(sb.length()-1);

		Map<String, String> props = new HashMap<String, String>();
		props.put(MessageListDS.MESSAGEID, sb.toString());
		props.put(MessageListDS.QUEUE, getTitle());
		props.put(MessageListDS.BROKER, "local");

		if(toQ != null){
			props.put(MessageListDS.TO_QUEUE, toQ);
		}
		if(copyOrMoveCmd != null){
			props.put(MessageListDS.CMD, copyOrMoveCmd);
		}
		return new Record(props);
	}
	
	private List<String> extractMsgIds(ListGridRecord[] recs){
		List<String> result = new ArrayList<String>();
		if(recs != null){
			for (ListGridRecord rec : recs) {
				result.add(rec.getAttributeAsString(MessageListDS.MESSAGEID));
			}
		}
		return result;
	}
	
	private Map<String, Object> getMsgEventInfo(Object data, DataEventType type, String fromQ, String toQ){
		Map<String, Object> info = new HashMap<String, Object>();
		info.put(DataLoadedEvent.DATA_KEY, data);
		info.put(DataLoadedEvent.SUBTYPE_KEY, type);
		info.put("fromQ", fromQ);
		info.put("toQ", toQ);
		return info;
	}

	HLayout headerPanel;
	private HLayout getHeaderPanel(){
		headerPanel = new HLayout();
		headerPanel.setAlign(Alignment.RIGHT);
		headerPanel.setTitle("Browse");

		headerPanel.addMember(getCopyBtn());
		headerPanel.addMember(getMoveBtn());
		headerPanel.addMember(getDeleteBtn());
		headerPanel.setHeight(25);
		
		return headerPanel;
	}
	
	public void setQueueListProvider(HasData queueListProvider){
		this.queueListProvider = queueListProvider;
	}
	
	@Override
	protected void handleRefresh() {
		 listGrid.invalidateCache();
	}

	@Override
	protected void handleSettings() {
	}

	@Override
	protected void handleHelp() {
	}
}
