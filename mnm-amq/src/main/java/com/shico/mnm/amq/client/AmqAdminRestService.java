package com.shico.mnm.amq.client;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.Options;
import org.fusesource.restygwt.client.RestService;

import com.shico.mnm.common.model.ListResult;
import com.shico.mnm.common.model.MapResult;
import com.shico.mnm.common.model.NestedList;

@Deprecated
@Path("/rest/admin/amq")
public interface AmqAdminRestService extends RestService {
	@GET
	@Options(timeout=5000)
	@Path("brokerInfo/{brokerName}")
	public void getBrokerInfo(@PathParam("brokerName") String brokerName,
			MethodCallback<MapResult<String, Object>> callback);

	@GET
	@Options(timeout=5000)
	@Path("/metadata/columns/{brokerName}")
	public void getMetadata(@PathParam("brokerName") String brokerName,
			MethodCallback<NestedList<String>> callback);

	@GET
	@Options(timeout=5000)
	@Path("/queuelist/{brokerName}")
	public void getQueueList(@PathParam("brokerName") String brokerName,
			MethodCallback<NestedList<Object>> callback);

	@GET
	@Path("/queue/purge/{brokerName}")
	@Options(timeout=5000)
	public void purgeQueue(
			@PathParam("brokerName") String brokerName,
			@QueryParam("queueName") String queueName,
			MethodCallback<NestedList<Object>> callback);

	@GET
	@Path("/queue/del/{brokerName}")
	@Options(timeout=5000)
	public void deleteQueue(
			@PathParam("brokerName") String brokerName,
			@QueryParam("queueName") String queueName,
			MethodCallback<NestedList<Object>> callback);

	@GET
	@Path("/queue/add/{brokerName}")
	@Options(timeout=5000)
	public void addQueue(
			@PathParam("brokerName") String brokerName,
			@QueryParam("queueName") String queueName,
			MethodCallback<NestedList<Object>> callback);

	@GET
	@Path("/queue/{brokerName}")
	@Options(timeout=5000)
	public void getMessages(
			@PathParam("brokerName") String brokerName,
			@QueryParam("queueName") String queueName,
			@QueryParam("selector") String selector,
			MethodCallback<NestedList<Object>> callback);

	@GET
	@Path("/msg/{brokerName}")
	@Options(timeout=5000)
	public void getMessage(
			@PathParam("brokerName") String brokerName,
			@QueryParam("queueName") String queueName,
			@QueryParam("messageId") String messageId,
			MethodCallback<MapResult<String, Object>> callback);

	@POST
	@Path("/msg/delete/{brokerName}")
	@Options(timeout=5000)
	public void deleteMessage(
			@PathParam("brokerName") String brokerName,
			@QueryParam("queueName") String queueName,
			ListResult<String> messageIds,
			MethodCallback callback);

	@POST
	@Path("/msg/move/{brokerName}")
	@Options(timeout=5000)
	public void moveMessage(
			@PathParam("brokerName") String brokerName,
			@QueryParam("fromQueue") String fromQ,
			@QueryParam("toQueue") String toQ,
			ListResult<String> messageIds,
			MethodCallback callback);
	
	@POST
	@Path("/msg/copy/{brokerName}")
	@Options(timeout=5000)
	public void copyMessage(
			@PathParam("brokerName") String brokerName,
			@QueryParam("fromQueue") String fromQ,
			@QueryParam("toQueue") String toQ,
			ListResult<String> messageIds,
			MethodCallback callback);
		
}
