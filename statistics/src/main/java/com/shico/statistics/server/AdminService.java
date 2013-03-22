package com.shico.statistics.server;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.shico.statistics.model.Settings;
import com.shico.statistics.server.jaxb.DSRequest;
import com.shico.statistics.server.jaxb.DSResponse;
import com.shico.statistics.server.jaxb.SettingsResponse;

@Service("adminService")
@Path("/admin")
@Produces( { MediaType.APPLICATION_XML})
@Consumes( { MediaType.TEXT_XML })
public class AdminService {
	private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

	@POST
	@Path("/settings")
	public SettingsResponse settings(DSRequest<Settings> request){
		logger.info("admin/settings called with {} operation."+request.getOperationType().name());
		SettingsResponse response = new SettingsResponse();
		
		switch(request.getOperationType()){
		case FETCH:
			return fetchSettings(request, response);
		case ADD:
			logger.error("Invalid not-supported operation.");
			response.setStatus(DSResponse.STATUS_FAILURE);
			return response;
		case UPDATE:
			return saveSettings(request, response);
		case REMOVE:
			logger.error("Invalid not-supported operation.");
			response.setStatus(DSResponse.STATUS_FAILURE);
			return response;			
		default:
			response.setStatus(DSResponse.STATUS_FAILURE);
		}
		return response;
	}

	private SettingsResponse fetchSettings(DSRequest<Settings> request,
			SettingsResponse response) {
		Settings data = null;
		try {
			data = (Settings)request.getData(Settings.class);
		} catch (JAXBException e) {
			logger.error("Failed to unmarshall the Settings bean in the request.data.", e);
			response.setStatus(DSResponse.STATUS_FAILURE);
			return response;
		}
		
		if(data.getApp().equals("mnm-amq")){
			response.addRecord(new Settings(
					data.getApp(), "http://127.0.0.1:9119/statistics/rest/amq/admin", 
					"http://127.0.0.1:9119/statistics/rest/monitor", 
					"farhad", "XXX", "farhad", "XXX", 10000, 180000));
		}
		response.setStatus(0);
		
		logger.info("Returning response for {} operation. status = {}", request.getOperationType().name(), response.getStatus());
		return response;
	}	
	
	private SettingsResponse saveSettings(DSRequest<Settings> request,
			SettingsResponse response) {
		Settings data = null;
		try {
			data = (Settings)request.getData(Settings.class);
		} catch (JAXBException e) {
			logger.error("Failed to unmarshall the Settings bean in the request.data.", e);
			response.setStatus(DSResponse.STATUS_FAILURE);
			return response;
		}
		
		if(data.getApp().equals("mnm-amq")){
			logger.info("Settings saved.");
		}
		response.addRecord(data);
		response.setStatus(0);
		
		logger.info("Returning response for {} operation. status = {}", request.getOperationType().name(), response.getStatus());
		return response;		
	}
		
}
