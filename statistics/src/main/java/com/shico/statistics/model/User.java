package com.shico.statistics.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class User {
	private String user;
	private String passwd;

	public User() {
		super();
	}

	public User(String user, String passwd) {
		super();
		this.user = user;
		this.passwd = passwd;
	}
	
}
