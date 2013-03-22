package com.shico.statistics.server.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

import com.smartgwt.client.data.RestDataSource;

/**
 * Simple enumeration representing the different {@link RestDataSource} operations.
 */
@XmlType
@XmlEnum(String.class)
@XmlAccessorType(XmlAccessType.FIELD)
public enum OperationType {
	@XmlEnumValue("add") ADD, 
	@XmlEnumValue("fetch") FETCH, 
	@XmlEnumValue("update") UPDATE, 
	@XmlEnumValue("remove") REMOVE
}