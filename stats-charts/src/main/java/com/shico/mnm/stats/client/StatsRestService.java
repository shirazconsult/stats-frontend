package com.shico.mnm.stats.client;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import com.shico.mnm.common.model.ListOfMap;
import com.shico.mnm.common.model.ListResult;
import com.shico.mnm.common.model.NestedList;

@Path("/rest/stats")
public interface StatsRestService extends RestService {
	@GET
	@Path("/viewcolumns")
	public void getViewColumns(MethodCallback<ListResult<String>> callback);
	
	@GET
	@Path("/viewpage/{from}/{to}")
	public void getViewPage(
			@PathParam("from") long from,
			@PathParam("to") long to,
			MethodCallback<NestedList<Object>> callback);

	@GET
	@Path("/viewpage/{type}/{from}/{to}")
	public void getViewPage(
			@PathParam("type") String type,
			@PathParam("from") long from,
			@PathParam("to") long to,
			MethodCallback<NestedList<Object>> callback);
	
	@GET
	@Path("/viewpage/{type}/{from}/{to}/{options}")
	public void getViewPage(
			@PathParam("type") String type, 
			@PathParam("from") long from, 
			@PathParam("to") long to,
			@PathParam("options") String options,
			MethodCallback<NestedList<Object>> callback);
	
	@GET
	@Path("/view/{from}/{to}")
	public void getViewPage(
			@PathParam("from") String from, 
			@PathParam("to") String to,
			MethodCallback<NestedList<Object>> callback);
	
	@GET
	@Path("/view/{type}/{from}/{to}")
	public void getViewPage(
			@PathParam("type") String type, 
			@PathParam("from") String from, 
			@PathParam("to") String to,
			MethodCallback<NestedList<Object>> callback);

	@GET
	@Path("/view/{type}/{from}/{to}/{options}")
	public void getViewPage(
			@PathParam("type") String type, 
			@PathParam("from") String from, 
			@PathParam("to") String to,
			@PathParam("options") String options,
			MethodCallback<NestedList<Object>> callback);	
	
	@GET
	@Path("/viewbatch/{type}/{from}/{to}/{options}")
	public void getViewPageInBatch(
			@PathParam("type") String type, 
			@PathParam("from") String from, 
			@PathParam("to") String to,
			@PathParam("options") String options,
			// The response is actually a ListResult<NestedList<Object>>, but since the gwt compiler for some
			// reason cannot compile it, then we have to do the unmarshalling manually.
			MethodCallback<ListResult<Object>> callback);
}
