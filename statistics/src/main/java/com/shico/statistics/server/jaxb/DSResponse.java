package com.shico.statistics.server.jaxb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import com.shico.statistics.model.Settings;
import com.shico.statistics.model.User;
import com.smartgwt.client.data.RestDataSource;

/**
 * Utility class for representing a {@link RestDataSource} response as an object. 
 * Having the generic type in the "data" field makes jaxb to put the xsi:type definition in the generated response. This is not
 * treated appropriately by smartgwt RestDataSource. In order to avoid having namespace and xmlns type def. in the
 * response we have to have specialized subclasses of this class for each specific bean type of "data".
 * 
 * @author farhad
 *
 * @param <T>
 */
@XmlType
@XmlAccessorType(XmlAccessType.NONE)
public abstract class DSResponse<T> {
    public static int STATUS_FAILURE = -1;
    public static int STATUS_LOGIN_INCORRECT = -5;
    public static int STATUS_LOGIN_REQUIRED = -7;
    public static int STATUS_LOGIN_SUCCESS = -8;
    public static int STATUS_MAX_LOGIN_ATTEMPTS_EXCEEDED = -6;
    public static int STATUS_SERVER_TIMEOUT = -100;
    public static int STATUS_SUCCESS = 0;
    public static int STATUS_TRANSPORT_ERROR = -90;
    public static int STATUS_VALIDATION_ERROR = -4;
	
//	@XmlElementWrapper(name="data")
//	@XmlElement(name="record")
	List<T> data;

	@XmlElement
	private int status;
	
	@XmlElement
	private int startRow;
	
	@XmlElement
	private int endRow;
	
	@XmlElement
	private int totalRows;	
	
	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public int getEndRow() {
		return endRow;
	}

	public void setEndRow(int endRow) {
		this.endRow = endRow;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}

	public int getTotalRows() {
		return totalRows;
	}
		
	public void addRecord(T record) {
		if (data == null) {
			data = new ArrayList<T>();
		}
		this.data.add(record);
	}
}
