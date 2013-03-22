package com.shico.statistics.server.jaxb;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import org.w3c.dom.Node;

import com.shico.statistics.model.Settings;
import com.shico.statistics.model.User;
import com.smartgwt.client.data.RestDataSource;

/**
 * Utility class for representing a {@link RestDataSource} request as an object. Example:
 * <request>
 *   <data>
 *       <app>mnm-amq</app>
 *       <user>admin</user>
 *       ...
 *   </data>
 *  <dataSource>isc_DefaultRestDS_0</dataSource>
 *   <operationType>fetch</operationType>
 *  <oldValues></oldValues>
 * </request>
 * 
 * @author farhad
 *
 * @param <T>
 */
@XmlRootElement(name = "request")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({User.class, Settings.class})
public class DSRequest<T> {

	@XmlElement(name = "data")
	private T data;

	@XmlElement
	private String dataSource;
	
	@XmlElement
	private OperationType operationType;
	
	@XmlElement
	private int startRow;
	
	@XmlElement
	private int endRow;
	
	@XmlElement
	private String componentId;
	
	@XmlTransient
	private String oldValues;
	
	public OperationType getOperationType() {
		return operationType;
	}

	public void setOperationType(OperationType operationType) {
		this.operationType = operationType;
	}

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
	
	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}

	public String getComponentId() {
		return componentId;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public String getDataSource() {
		return dataSource;
	}
	
	@SuppressWarnings("unchecked")
	public T getData(Class<T> type) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(type);
		Unmarshaller u = jc.createUnmarshaller();  
		return (T)u.unmarshal((Node)data);
	}	
}
