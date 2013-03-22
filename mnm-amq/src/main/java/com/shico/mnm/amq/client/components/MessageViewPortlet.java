package com.shico.mnm.amq.client.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.shico.mnm.amq.client.AmqSettingsController;
import com.shico.mnm.amq.client.HasData;
import com.shico.mnm.amq.model.AmqRemoteSettingsDS;
import com.shico.mnm.amq.model.MessageDetailDS;
import com.shico.mnm.amq.model.MessageListDS;
import com.shico.mnm.common.component.PortletWin;
import com.shico.mnm.common.event.DataEventType;
import com.shico.mnm.common.event.DataLoadedEvent;
import com.shico.mnm.common.event.EventBus;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.util.ValueCallback;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Portlet;
import com.smartgwt.client.widgets.layout.VLayout;

public class MessageViewPortlet extends PortletWin {
	public final static String TITLE = "Message View";
	int rowIdx;
	HLayout container;
	VLayout contentPanel;
	DynamicForm headerForm;
	DynamicForm propertiesForm;
	MessageDetailDS datasource;
	Map<String, Object> fetchedMsg; 
	Record msgRecord;
	AmqSettingsController settingsController;
	HasData queueListProvider;

	public MessageViewPortlet(AmqSettingsController settingsController) {
		this.settingsController = settingsController;

		String restUrl = (String)settingsController.getSetting(AmqRemoteSettingsDS.BROKERURL);
		datasource = new MessageDetailDS("messageDetailDS", restUrl+"/message");

		setTitle(TITLE);
		
		container = new HLayout();
		container.setWidth100();
		container.setMembersMargin(5);
		container.setMargin(5);
		
		container.setWidth("100%");
//		container.setHeight(250);
		
		container.addMember(getContentPanel());
		container.addMember(getButtonPanel());

		getContentPanel().addMember(getHeaderForm());
		
		addItem(container);
	}

	public void update(Record record){
		msgRecord = record;
		update(record.getAttributeAsString(MessageDetailDS.MESSAGEID), 
				record.getAttributeAsString(MessageDetailDS.QUEUE), 
				record.getAttributeAsString(MessageDetailDS.BROKER));
	}
	
	public void update(String msgId, String queue, String broker){
		resetView();
		contentPanel.addMember(getPropertiesForm());

		Criteria criteria = new Criteria(MessageDetailDS.MESSAGEID, msgId);
		criteria.addCriteria(MessageDetailDS.QUEUE, queue);
		criteria.addCriteria(MessageDetailDS.BROKER, broker);
		getHeaderForm().fetchData(criteria, new DSCallback() {			
			@Override
			public void execute(DSResponse response, Object rawData, DSRequest request) {
				if(response.getStatus() == 0){
					fetchedMsg = response.getData()[0].toMap();
					updatePropertiesView();
				}
			}
		});
	}
	
	private void resetView(){
		if(headerForm != null){
			headerForm.reset();
		}
		if(propertiesForm != null){
			propertiesForm.reset();
			contentPanel.removeChild(propertiesForm);
			propertiesForm = null;
		}
	}
	
	private DynamicForm getHeaderForm(){
		if(headerForm == null){
			headerForm = new DynamicForm();
			headerForm.setWidth100();
			headerForm.setIsGroup(true);
			headerForm.setGroupTitle("Message Headers");
			headerForm.setCellPadding(5);

			headerForm.setNumCols(8);

			StaticTextItem idItem = new StaticTextItem(MessageDetailDS.MESSAGEID, "Message ID");
			idItem.setColSpan(3);
			StaticTextItem correlationIdItem = new StaticTextItem(MessageDetailDS.CORRELATIONID, "Correlation ID");		
			correlationIdItem.setColSpan(3);
			StaticTextItem destinationItem = new StaticTextItem(MessageDetailDS.DESTINATION, "Destination");
			StaticTextItem replyToItem = new StaticTextItem(MessageDetailDS.REPLYTO, "Reply to");
			StaticTextItem deliveryModeItem = new StaticTextItem(MessageDetailDS.DELIVERYMODE, "Delivery mode");
			StaticTextItem redeliveryItem = new StaticTextItem(MessageDetailDS.REDELIVERED, "Redelivered");
			StaticTextItem timestampItem = new StaticTextItem(MessageDetailDS.TIMESTAMP, "Timestamp");
			timestampItem.setColSpan(3);
			StaticTextItem expirationItem = new StaticTextItem(MessageDetailDS.EXPIRATION, "Expiration time");
			expirationItem.setColSpan(3);
			StaticTextItem typeItem = new StaticTextItem(MessageDetailDS.TYPE, "Type");
			StaticTextItem priorityItem = new StaticTextItem(MessageDetailDS.PRIORITY, "Priority");

			headerForm.setItems(idItem, correlationIdItem, destinationItem, replyToItem, deliveryModeItem,
					redeliveryItem, timestampItem, expirationItem, typeItem, priorityItem);

			headerForm.setDataSource(datasource);
		}
		return headerForm; 
	}
	
	private DynamicForm getPropertiesForm(){
		if(propertiesForm == null){
			propertiesForm = new DynamicForm();
			propertiesForm.setWidth100();
			propertiesForm.setIsGroup(true);
			propertiesForm.setGroupTitle("Message Properties");
			propertiesForm.setCellPadding(5);

			propertiesForm.setNumCols(6);
		}    
		return propertiesForm;
	}
	
	private void updatePropertiesView(){
		// properties
		List<FormItem> propItems = new ArrayList<FormItem>();
		if(fetchedMsg != null){
			for (Entry<String, Object> entry : fetchedMsg.entrySet()) {
				String name = entry.getKey();
				if(name.startsWith("p.")){
					String displayName = name.substring(2);
					StaticTextItem f = new StaticTextItem(displayName, displayName);
					f.setValue(entry.getValue());
					propItems.add(f);
				}
			}
		}
		// body
		TextAreaItem bodyItem = new TextAreaItem("body");
		bodyItem.setValue(fetchedMsg.get("body"));
		bodyItem.setLength(5000);  
		bodyItem.setColSpan(6);  
		bodyItem.setWidth("*");  
		bodyItem.setHeight(30);
		bodyItem.setCanEdit(false);
        propItems.add(bodyItem);
        
        propertiesForm.setItems(propItems.toArray(new FormItem[]{}));		
	}
	
	private VLayout getContentPanel(){
		if(contentPanel == null){
			contentPanel = new VLayout();
			contentPanel.setAutoHeight();
			contentPanel.setMembersMargin(10);
			contentPanel.setMargin(10);
			contentPanel.setWidth("90%");
			contentPanel.setHeight(200);
		}	
		return contentPanel;
	}
	
	private VLayout getButtonPanel(){
		VLayout btnPanel = new VLayout();
		btnPanel.setAlign(Alignment.CENTER);
		btnPanel.setAlign(VerticalAlignment.TOP);
		btnPanel.setAutoHeight();
		btnPanel.setMembersMargin(5);
		btnPanel.setMargin(5);

		btnPanel.addMember(getMoveBtn());
		btnPanel.addMember(getCopyBtn());
		btnPanel.addMember(getDeleteBtn());
		
		return btnPanel;
	}
		
	private IButton getMoveBtn(){
		IButton btn = new IButton("Move");
		btn.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				new MsgActionWin(DataEventType.MOVE, queueListProvider.getDataRecords(), new ValueCallback() {
					@Override
					public void execute(final String value) {
						datasource.updateData(newCopyMoveCMDRecord(MessageListDS.MOVE_CMD, value), new DSCallback() {							
							@Override
							public void execute(DSResponse response, Object rawData, DSRequest request) {
								if(response.getStatus() == DSResponse.STATUS_SUCCESS){
									Map<String, Object> info = getMsgEventInfo(DataEventType.MOVE, getTitle(), value);
									EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.MSG_EVENT, info, MessageViewPortlet.class));
									removeMeFromContainer();
								}
							}
						});
					}
				});
			}
		});
		btn.setStylePrimaryName("shico-Button");
		return btn;
	}

	private IButton getDeleteBtn(){
		IButton btn = new IButton("Delete");
		btn.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				SC.confirm("Are you sure you want to delete the selected messages ?", new BooleanCallback() {					
					@Override
					public void execute(Boolean value) {
						if(value){
							datasource.removeData(msgRecord, new DSCallback() {
								@Override
								public void execute(DSResponse response, Object rawData, DSRequest request) {
									if(response.getStatus() == DSResponse.STATUS_SUCCESS){
										Map<String, Object> info = getMsgEventInfo(DataEventType.DELETE, getTitle(), null);
										EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.MSG_EVENT, info, MessageViewPortlet.class));
										removeMeFromContainer();
									}
								}
							});
						}
					}
				});
			}
		});
		btn.setStylePrimaryName("shico-Button");
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
						datasource.updateData(newCopyMoveCMDRecord(MessageListDS.COPY_CMD, value), new DSCallback() {							
							@Override
							public void execute(DSResponse response, Object rawData, DSRequest request) {
								if(response.getStatus() == DSResponse.STATUS_SUCCESS){
									Map<String, Object> info = getMsgEventInfo(DataEventType.COPY, getTitle(), value);
									EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.MSG_EVENT, info, MessageViewPortlet.class));
								}
							}
						});
					}
				});
			}
		});
		btn.setStylePrimaryName("shico-Button");
		return btn;
	}
	
	private Record newDeleteRecord(){
		return newCopyMoveCMDRecord(null, null);
	}
	private Record newCopyMoveCMDRecord(String copyOrMoveCmd, String toQ){
		Map<String, String> props = new HashMap<String, String>();
		props.put(MessageListDS.MESSAGEID, (String)fetchedMsg.get(MessageListDS.MESSAGEID));
		props.put(MessageListDS.QUEUE, (String)fetchedMsg.get(MessageListDS.QUEUE));
		props.put(MessageListDS.BROKER, (String)fetchedMsg.get(MessageListDS.BROKER));

		if(toQ != null){
			props.put(MessageListDS.TO_QUEUE, toQ);
		}
		if(copyOrMoveCmd != null){
			props.put(MessageListDS.CMD, copyOrMoveCmd);
		}
		return new Record(props);
	}
	private Map<String, Object> getMsgEventInfo(DataEventType type, String fromQ, String toQ){
		Map<String, Object> info = new HashMap<String, Object>();
		info.put(DataLoadedEvent.DATA_KEY, fetchedMsg.get(MessageListDS.MESSAGEID));
		info.put(DataLoadedEvent.SUBTYPE_KEY, type);
		info.put("fromQ", fromQ);
		info.put("toQ", toQ);
		return info;
	}

	private void removeMeFromContainer(){
		Portlet portlet = getPortalContainer().getPortlet(getTitle());
		getPortalContainer().removePortlet(portlet);
	}

	public void setQueueListProvider(HasData queueListProvider){
		this.queueListProvider = queueListProvider;
	}

	@Override
	protected void handleRefresh() {
		if(fetchedMsg != null){
			update((String)fetchedMsg.get(MessageDetailDS.MESSAGEID), 
					(String)fetchedMsg.get(MessageDetailDS.QUEUE), 
					(String)fetchedMsg.get(MessageDetailDS.BROKER));
		}
	}

	@Override
	protected void handleSettings() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void handleHelp() {
		// TODO Auto-generated method stub
		
	}
}
