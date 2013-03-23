package com.shico.mnm.amq.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.Resource;
import org.fusesource.restygwt.client.RestServiceProxy;

import com.google.gwt.core.client.GWT;
import com.shico.mnm.amq.model.AmqRemoteSettingsDS;
import com.shico.mnm.amq.model.BrokerInfoDS;
import com.shico.mnm.amq.model.Message;
import com.shico.mnm.amq.model.MessageColumns;
import com.shico.mnm.amq.model.Queue;
import com.shico.mnm.amq.model.QueueColumns;
import com.shico.mnm.common.event.DataEventType;
import com.shico.mnm.common.event.DataLoadedEvent;
import com.shico.mnm.common.event.DataLoadedEventHandler;
import com.shico.mnm.common.event.EventBus;
import com.shico.mnm.common.model.ListResult;
import com.shico.mnm.common.model.MapResult;
import com.shico.mnm.common.model.NestedList;

@Deprecated
public class AmqAdminClient implements DataLoadedEventHandler { 
	private static Logger logger = Logger.getLogger("AmqAdminClient");
	
	AmqAdminRestService adminService;
	AmqSettingsControllerImpl settingsController;
	
	// domain objects
	BrokerInfoDS brokerInfoDS;
	List<Queue> queueList;
	Map<String, List<Message>> msgLists = new HashMap<String, List<Message>>();
	public QueueColumns queueColNames;
	public MessageColumns msgColNames;
	
	public AmqAdminClient() {
		super();
		
		settingsController = AmqClientHandle.getAmqSettingsController();
		
		EventBus.instance().addHandler(DataLoadedEvent.TYPE, this);
	}

	public void getBrokerInfo(final String brokerName, boolean refresh){
		if(refresh){
			adminService.getBrokerInfo(brokerName, new MethodCallback<MapResult<String,Object>>() {
				@Override
				public void onSuccess(Method method, MapResult<String, Object> response) {
					logger.log(Level.INFO, "Retrieving broker information for "+brokerName);
//					brokerInfo.setData(response.result);
					EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.BROKER_INFO_LOADED_EVENT));
				}

				@Override
				public void onFailure(Method method, Throwable exception) {
					String msg = "Failed to get broker infomation for "+ brokerName+". " + exception.getMessage();
					logger.log(Level.SEVERE, msg);
					EventBus.instance().fireEvent(buildFailedMsgEvent(msg, exception, DataEventType.FAILED_BROKER_INFO_LOADED_EVENT));				
				}
			});
		}
	}

	public void getMetadata(final String brokerName){
		adminService.getMetadata(brokerName, new MethodCallback<NestedList<String>>() {

			@Override
			public void onFailure(Method method, Throwable exception) {
				String msg = "Failed to retrieve queue list columns from "+brokerName;
				logger.log(Level.SEVERE, msg);
				EventBus.instance().fireEvent(buildFailedMsgEvent(msg, exception, DataEventType.FAILED_BROKER_METADATA_LOADED_EVENT));				
			}

			@Override
			public void onSuccess(Method method, NestedList<String> response) {
				logger.log(Level.INFO, "Retrieving queue list columns from "+brokerName);
				
				List<ListResult<String>> rows = response.getRows();
				queueColNames = new QueueColumns(rows.get(0).getResult(), rows.get(1).getResult());
				msgColNames = new MessageColumns(rows.get(2).getResult(), rows.get(3).getResult());

				EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.QUEUELIST_METADATA_LOADED_EVENT));					
			}
		});
	}

	public void getQueueList(final String brokerName, boolean refresh){
		if(refresh){
			adminService.getQueueList(brokerName, new MethodCallback<NestedList<Object>>() {

				@Override
				public void onSuccess(Method method, NestedList<Object> response) {
					logger.log(Level.INFO, "Retrieving queues from "+brokerName);
					List<ListResult<Object>> ql = response.getRows();
					queueList = new ArrayList<Queue>();
					for (ListResult<Object> q : ql) {
						queueList.add(new Queue(q.getResult()));
					}						
					EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.QUEUELIST_INFO_LOADED_EVENT));					
				}

				@Override
				public void onFailure(Method method, Throwable exception) {
					String msg = "Failed to retrieve queues from "+ brokerName+". " + exception.getMessage();
					logger.log(Level.SEVERE, msg);
					EventBus.instance().fireEvent(buildFailedMsgEvent(msg, exception, DataEventType.FAILED_QUEUELIST_INFO_LOADED_EVENT));				
				}
			});
		}
	}

	public void getMsgList(final String brokerName, final String queueName, final String selector, boolean refresh){
		if(refresh){			
			adminService.getMessages(brokerName, queueName, selector, new MethodCallback<NestedList<Object>>() {

				@Override
				public void onSuccess(Method method, NestedList<Object> response) {
					logger.log(Level.INFO, "Retrieving messages from queue: "+queueName);	
					
					List<Message> msgs = new ArrayList<Message>();
					List<ListResult<Object>> ml = response.getRows();
					for (ListResult<Object> m : ml) {
						msgs.add(new Message(m.getResult(), msgColNames));
					}
					msgLists.put(queueName, msgs);
					
					Map<String, Object> info = response.getControlData().result;
					if(info == null){
						EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.MSGLIST_LOADED_EVENT));						
					}else{
						EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.MSGLIST_LOADED_EVENT, info));
					}
				}

				@Override
				public void onFailure(Method method, Throwable exception) {
					String msg = "Failed to retrieve messages from "+ queueName+". " + exception.getMessage();
					logger.log(Level.SEVERE, msg);
					EventBus.instance().fireEvent(buildFailedMsgEvent(msg, exception, DataEventType.FAILED_MSGLIST_LOADED_EVENT));				
				}
			});
		}
	}
	
	public void getMessage(final String brokerName, final String queueName, final String messageId){
		adminService.getMessage(brokerName, queueName, messageId, new MethodCallback<MapResult<String,Object>>() {

			@Override
			public void onFailure(Method method, Throwable exception) {
				String msg = "Failed to retrieve message with id = "+messageId+" from "+ queueName+". " + exception.getMessage();
				logger.log(Level.SEVERE, msg);
				EventBus.instance().fireEvent(buildFailedMsgEvent(msg, exception, DataEventType.FAILED_MSG_ACTION_EVENT));				
			}

			@Override
			public void onSuccess(Method method, MapResult<String, Object> response) {
				logger.log(Level.INFO, "Retrieving message "+messageId+" from queue: "+queueName);
				Map<String, Object> info = getMsgEventInfo(response.result, DataEventType.LOAD, queueName, null);
				EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.MSG_EVENT, info));					
			}
		});
	}

	public void deleteMessage(final String brokerName, final String qName, final Collection<String> msgIds){
		adminService.deleteMessage(brokerName, qName, toListResult(msgIds), new MethodCallback() {

			@Override
			public void onFailure(Method method, Throwable exception) {
				String msg = "Failed to delete message with id = "+msgIds+" from "+ qName+". " + exception.getMessage();
				logger.log(Level.SEVERE, msg);
				EventBus.instance().fireEvent(buildFailedMsgEvent(msg, exception, DataEventType.FAILED_MSG_ACTION_EVENT));				
			}

			@Override
			public void onSuccess(Method method, Object response) {
				logger.log(Level.INFO, "Deleting message "+msgIds+" from queue: "+qName);
				Map<String, Object> info = getMsgEventInfo(msgIds, DataEventType.DELETE, qName, null);
				EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.MSG_EVENT, info));				
			}
		});		
	}

	@SuppressWarnings("rawtypes")
	public void moveMessage(final String brokerName, final String fromQ, final String toQ, final Collection<String> msgIds){
		adminService.moveMessage(brokerName, fromQ, toQ, toListResult(msgIds), new MethodCallback() {

			@Override
			public void onFailure(Method method, Throwable exception) {
				String msg = "Failed to move message with id = "+msgIds+" from "+ fromQ+" to " + toQ+". "+exception.getMessage();
				logger.log(Level.SEVERE, msg);
				EventBus.instance().fireEvent(buildFailedMsgEvent(msg, exception, DataEventType.FAILED_MSG_ACTION_EVENT));				
			}

			@Override
			public void onSuccess(Method method, Object response) {
				logger.log(Level.INFO, "Moving message "+msgIds+" from "+ fromQ+" to " + toQ+". ");
				Map<String, Object> info = getMsgEventInfo(msgIds, DataEventType.MOVE, fromQ, toQ);
				EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.MSG_EVENT, info));				
			}
		});		
	}

	public void copyMessage(final String brokerName, final String fromQ, final String toQ, final Collection<String> msgIds){
		adminService.copyMessage(brokerName, fromQ, toQ, toListResult(msgIds), new MethodCallback() {

			@Override
			public void onFailure(Method method, Throwable exception) {
				String msg = "Failed to copy message with id = "+msgIds+" from "+ fromQ+" to " + toQ+". "+exception.getMessage();
				logger.log(Level.SEVERE, msg);
				EventBus.instance().fireEvent(buildFailedMsgEvent(msg, exception, DataEventType.FAILED_MSG_ACTION_EVENT));								
			}

			@Override
			public void onSuccess(Method method, Object response) {
				logger.log(Level.INFO, "Copying message "+msgIds+" from "+ fromQ+" to " + toQ+". ");
				Map<String, Object> info = getMsgEventInfo(msgIds, DataEventType.COPY, fromQ, toQ);
				EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.MSG_EVENT, info));				
			}
		});		
	}
	
	public void purgeQueue(final String brokerName, final String qName) {
		adminService.purgeQueue(brokerName, qName, new MethodCallback() {

			@Override
			public void onFailure(Method method, Throwable exception) {
				String msg = "Failed to purge queued "+ qName+". " + exception.getMessage();
				logger.log(Level.SEVERE, msg);
				EventBus.instance().fireEvent(buildFailedMsgEvent(msg, exception, DataEventType.FAILED_MSG_ACTION_EVENT));				
			}

			@Override
			public void onSuccess(Method method, Object response) {
				logger.log(Level.INFO, "Pruging queue: "+qName);
				Map<String, Object> info = getMsgEventInfo(null, DataEventType.PURGE, qName, null);
				EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.MSG_EVENT, info));				
			}
		});		
	}

	public void deleteQueue(final String brokerName, final String qName) {
		adminService.deleteQueue(brokerName, qName, new MethodCallback() {

			@Override
			public void onFailure(Method method, Throwable exception) {
				String msg = "Failed to purge queue "+ qName+". " + exception.getMessage();
				logger.log(Level.SEVERE, msg);
				EventBus.instance().fireEvent(buildFailedMsgEvent(msg, exception, DataEventType.FAILED_MSG_ACTION_EVENT));				
			}

			@Override
			public void onSuccess(Method method, Object response) {
				logger.log(Level.INFO, "Deleting queue: "+qName);
				Map<String, Object> info = getMsgEventInfo(null, DataEventType.DEL_Q, qName, null);
				EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.MSG_EVENT, info));				
			}
		});		
	}

	public void addQueue(final String brokerName, final String qName) {
		adminService.addQueue(brokerName, qName, new MethodCallback() {

			@Override
			public void onFailure(Method method, Throwable exception) {
				String msg = "Failed to add queue "+ qName+". " + exception.getMessage();
				logger.log(Level.SEVERE, msg);
				EventBus.instance().fireEvent(buildFailedMsgEvent(msg, exception, DataEventType.FAILED_MSG_ACTION_EVENT));				
			}

			@Override
			public void onSuccess(Method method, Object response) {
				logger.log(Level.INFO, "Creating queue: "+qName);
				Map<String, Object> info = getMsgEventInfo(null, DataEventType.ADD_Q, qName, null);
				EventBus.instance().fireEvent(new DataLoadedEvent(DataEventType.MSG_EVENT, info));				
			}
		});		
	}

	public BrokerInfoDS getBrokerInfo() {
		return brokerInfoDS;
	}
	
	public List<Queue> getQueueList() {
		return queueList;
	}
	public QueueColumns getQueueColNames() {
		return queueColNames;
	}

	public MessageColumns getMsgColNames() {
		return msgColNames;
	}

	public List<Message> getMsgList(String queueName) {
		return msgLists.get(queueName);
	}
	
	private ListResult<String> toListResult(Collection<String> msgIdCollection){
		ListResult<String> msgList = new ListResult<String>();
		for (String msgId : msgIdCollection) {			
			msgList.addElement(msgId);
		}
		return msgList;
	}

	private DataLoadedEvent buildFailedMsgEvent(String error, Object exc, DataEventType type){
		DataLoadedEvent failedEvent = new DataLoadedEvent(DataEventType.FAILED_EVENT);
		failedEvent.info = new HashMap<String, Object>();
		failedEvent.info.put(DataLoadedEvent.ERROR_KEY, error);
		failedEvent.info.put(DataLoadedEvent.EXCEPTION_KEY, exc);
		failedEvent.info.put(DataLoadedEvent.SUBTYPE_KEY, type);		
		return failedEvent;
	}

	private Map<String, Object> getMsgEventInfo(Object data, DataEventType type, String fromQ, String toQ){
		Map<String, Object> info = new HashMap<String, Object>();
		info.put(DataLoadedEvent.DATA_KEY, data);
		info.put(DataLoadedEvent.SUBTYPE_KEY, type);
		info.put("fromQ", fromQ);
		info.put("toQ", toQ);
		return info;
	}

	@Override
	public void onDataLoaded(DataLoadedEvent event) {
		switch(event.eventType){
		case AMQ_SETTINGS_LOADED_EVENT:
		case AMQ_ADMIN_SETTINGS_CHANGED_EVENT:
//			String restUrl = (String)event.info.get(AdminSettingsDS.BROKERURL);
			@SuppressWarnings("deprecation")
			Resource resource = new Resource(settingsController.getAdminRest());
			adminService = GWT.create(AmqAdminRestService.class);
			((RestServiceProxy)adminService).setResource(resource);
			break;
		}
		
	}

}
