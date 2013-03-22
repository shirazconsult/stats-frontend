package com.shico.mnm.common.client;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import com.shico.mnm.common.model.ListResult;
import com.shico.mnm.common.model.NestedList;

@Path("/rest/monitor")
public interface StatRestService extends RestService {
	@GET
	@Path("columns/{target}")
	public void getColumnNames(@PathParam("target") String target,
			MethodCallback<ListResult<String>> callback);

	@POST
	@Path("rows/{target}/{from}/{to}")
	public void getRows(@PathParam("target") String target,
			@PathParam("from") long from, @PathParam("to") long to,
			MethodCallback<NestedList<String>> callback);

	@GET
	@Path("row/{target}")
	public void getLastRow(@PathParam("target") String target,
			MethodCallback<ListResult<String>> callback);

}
