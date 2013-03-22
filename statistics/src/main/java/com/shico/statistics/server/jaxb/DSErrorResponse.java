package com.shico.statistics.server.jaxb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import com.shico.statistics.model.Settings;
import com.shico.statistics.model.User;
import com.smartgwt.client.data.RestDataSource;

/**
 * 
 * @author farhad
 *
 */
public class DSErrorResponse {
    public static int STATUS_FAILURE = -1;
    public static int STATUS_LOGIN_INCORRECT = -5;
    public static int STATUS_LOGIN_REQUIRED = -7;
    public static int STATUS_LOGIN_SUCCESS = -8;
    public static int STATUS_MAX_LOGIN_ATTEMPTS_EXCEEDED = -6;
    public static int STATUS_SERVER_TIMEOUT = -100;
    public static int STATUS_SUCCESS = 0;
    public static int STATUS_TRANSPORT_ERROR = -90;
    public static int STATUS_VALIDATION_ERROR = -4;
	
    private Map<String, String> errorFieldMsgMap = new HashMap<String, String>();

    private int status;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Map<String, String> getErrorFieldMsgMap() {
		return errorFieldMsgMap;
	}

	public void setErrorFieldMsgMap(Map<String, String> errorFieldMsgMap) {
		this.errorFieldMsgMap = errorFieldMsgMap;
	}
    
	public void addErrorMsag(String fieldname, String errorMsg){
		errorFieldMsgMap.put(fieldname, errorMsg);
	}
}
