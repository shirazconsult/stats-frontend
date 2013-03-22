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

@Path("/admin")
public interface SettingsService extends RestService {
	@GET
	@Options(timeout=5000)
	@Path("settings/user")
	public void getBrokerInfo(@PathParam("brokerName") String brokerName,
			MethodCallback<MapResult<String, Object>> callback);
	
}
