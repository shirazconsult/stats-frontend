package com.shico.mnm.amq.client.components;

import java.util.LinkedHashMap;
import java.util.Set;

import com.shico.mnm.amq.client.AmqClientHandle;
import com.shico.mnm.amq.client.AmqSettingsControllerImpl;
import com.shico.mnm.amq.model.Message;
import com.shico.mnm.amq.model.QueueListDS;
import com.shico.mnm.common.event.DataEventType;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.ValueCallback;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class MsgActionWin extends Window {
	protected ComboBoxItem queueListBox;
	String fromQ;
	Set<Message> msgs;
	VLayout container;
	ValueCallback valueCallback;
	Record[] queueInfo;
	
	public MsgActionWin(DataEventType action, Record[] queueInfo, ValueCallback valueCallback) {
		this.valueCallback = valueCallback;
		this.queueInfo = queueInfo;
		
		setup(action);
	}

	private void setup(DataEventType action){
		setWidth(400);  
		setHeight(125);
        setTitle(getCaption(action));  
        setShowMinimizeButton(false);  
        setIsModal(true);  
        setShowModalMask(true);  
        centerInPage();

        addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClickEvent event) {
                destroy();  
			}
		});
		
		container = new VLayout();
		container.setWidth("*");
		container.setMembersMargin(20);
		container.setMargin(10);
		container.setLayoutAlign(VerticalAlignment.CENTER);
				
		DynamicForm df = new DynamicForm();
		switch(action){
		case ADD_Q:
			df.setFields(getInputTextItem());
			df.setWidth100();
			getInputTextItem().setTitle("Queue");
			container.addMember(df);
			break;
		case SELECT:
			df.setFields(getInputTextItem());
			df.setWidth100();
			getInputTextItem().setTitle("Selector");
			container.addMember(df);
			break;
		case MOVE:
		case BULK_MOVE:
		case COPY:			
			df.setFields(getQueueListBox());
			df.setWidth100();
			container.addMember(df);
			break;
		}
		
		HLayout hp = new HLayout();
		hp.setLayoutAlign(Alignment.CENTER);
		hp.setMembersMargin(10);
		
		hp.addMember(getActionBtn(action));
		hp.addMember(getCancelBtn());
		hp.setWidth("*");
		container.addMember(hp);
		
		addItem(container);
		
		show();
	}

	TextItem inputTextItem;
	private TextItem getInputTextItem(){
		if(inputTextItem == null){
			inputTextItem = new TextItem("InputTextField");
			inputTextItem.setWidth(300);
		}
		return inputTextItem;		
	}
	
	private ComboBoxItem getQueueListBox(){
		if(queueListBox == null){
			queueListBox = new ComboBoxItem();  
			queueListBox.setTitle("Destination");  
			queueListBox.setType("comboBox"); 
			queueListBox.setAddUnknownValues(false);
		
			queueListBox.setValueMap(getQueueList());
			queueListBox.setWidth(300);
		}
		return queueListBox;
	}
	
	private LinkedHashMap<String, String> getQueueList(){
		LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		
		if(queueInfo != null && queueInfo.length >=1){			
			for (Record rec : queueInfo) {
				String qName = rec.getAttributeAsString(QueueListDS.NAME);
				result.put(qName, qName);
			}
		}
		return result;
	}
	
	private String getCaption(DataEventType action) {
		switch (action) {
		case MOVE:
			return "Select a Queue/Topic to move to";
		case BULK_MOVE:
			return "Select a Queue/Topic to move alle the messages to";
		case COPY:
			return "Select a Queue/Topic to copy to";
		case ADD_Q:
			return "Add Queue";
		case SELECT:
			return "Browse with Selector";
		default:
			return "Unknow Action !!";
		}
	}

	private IButton getActionBtn(DataEventType action){
		IButton btn = new IButton();
		btn.setShowDown(true);
		switch (action) {
		case MOVE:
			btn.setTitle("Move");
			btn.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					valueCallback.execute(getQueueListBox().getValueAsString());
					hide();					
				}
			});
			break;
		case BULK_MOVE:
			btn.setTitle("Bulk Move");
			btn.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					valueCallback.execute(getQueueListBox().getValueAsString());
					hide();					
				}
			});
			break;			
		case COPY:
			btn.setTitle("Copy");
			btn.addClickHandler(new ClickHandler() {				
				@Override
				public void onClick(ClickEvent event) {
					valueCallback.execute(getQueueListBox().getValueAsString());
					hide();
				}
			});
			break;
		case ADD_Q:
			btn.setTitle("Add Queue");
			btn.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					valueCallback.execute(getInputTextItem().getValueAsString());
					hide();
				}
			});
			break;
		case SELECT:
			btn.setTitle("Select");
			btn.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					valueCallback.execute(getInputTextItem().getValueAsString());
					hide();
				}
			});
			break;			
		}
		
		return btn;
	}
	
	private IButton getCancelBtn(){
		IButton btn = new IButton("Cancel");
		btn.addClickHandler(new ClickHandler() {				
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		
		return btn;
	}	
}
