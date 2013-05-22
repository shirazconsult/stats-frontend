package com.shico.mnm.stats.client;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import com.shico.mnm.common.model.ListResult;
import com.shico.mnm.common.model.NestedList;

@Path("/rest/stats")
public interface StatsRestService extends RestService {
	@GET
	@Path("/viewcolumns")
	public void getViewColumns(MethodCallback<ListResult<String>> callback);
	
	@GET
	@Path("/views/{from}/{to}")
	public void getViewRows(
			@PathParam("from") long from,
			@PathParam("to") long to,
			MethodCallback<NestedList<Object>> callback);

	@GET
	@Path("/views/{type}/{from}/{to}")
	public void getViewRows(
			@PathParam("type") String type,
			@PathParam("from") long from,
			@PathParam("to") long to,
			MethodCallback<NestedList<Object>> callback);

//	@GET
//	@Path("/columns")
//	public void getColumnNames(MethodCallback<ListResult<String>> callback);
//
//	@GET
//	@Path("/nextpage")
//	public void getRows(@QueryParam("numOfPages") int numOfPages,
//			MethodCallback<NestedList<Object>> callback);
}
