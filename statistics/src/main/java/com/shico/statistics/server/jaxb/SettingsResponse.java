package com.shico.statistics.server.jaxb;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import com.shico.statistics.model.Settings;
import com.shico.statistics.model.User;

@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({User.class, Settings.class})
public class SettingsResponse extends DSResponse<Settings> {

	@XmlElementWrapper(name="data")
	@XmlElement(name="record")
	public Collection<Settings> getData() {
		return data;
	}

}
