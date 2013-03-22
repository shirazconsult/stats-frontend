package com.shico.statistics.model;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity(name = "settings")
@NamedQueries({
    @NamedQuery(name = Settings.NQ_FIND_BY_APP, query = "FROM com.shico.statistics.model.Settins s where app = :app"),
})
@XmlRootElement(name = "data")
@XmlAccessorType(XmlAccessType.FIELD)
public class Settings {
	public final static String NQ_FIND_BY_APP = "find.settings.by.app";
	
	private String app;
	private String brokerUrl;
	private String chartUrl;
	private String brokerUser;
	private String brokerPwd;
	private String chartUser;
	private String chartPwd;
	private int chartRefreshInterval;
	private int chartWinSize;
	
	public Settings() {
		super();
	}

	public Settings(String app, String brokerUrl, String chartUrl,
			String brokerUser, String brokerPwd, String chartUser,
			String chartPwd, int chartRefreshInterval, int chartWinSize) {
		super();
		this.app = app;
		this.brokerUrl = brokerUrl;
		this.chartUrl = chartUrl;
		this.brokerUser = brokerUser;
		this.brokerPwd = brokerPwd;
		this.chartUser = chartUser;
		this.chartPwd = chartPwd;
		this.chartRefreshInterval = chartRefreshInterval;
		this.chartWinSize = chartWinSize;
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public String getBrokerUrl() {
		return brokerUrl;
	}

	public void setBrokerUrl(String brokerUrl) {
		this.brokerUrl = brokerUrl;
	}

	public String getChartUrl() {
		return chartUrl;
	}

	public void setChartUrl(String chartUrl) {
		this.chartUrl = chartUrl;
	}

	public String getBrokerUser() {
		return brokerUser;
	}

	public void setBrokerUser(String brokerUser) {
		this.brokerUser = brokerUser;
	}

	public String getBrokerPwd() {
		return brokerPwd;
	}

	public void setBrokerPwd(String brokerPwd) {
		this.brokerPwd = brokerPwd;
	}

	public String getChartUser() {
		return chartUser;
	}

	public void setChartUser(String chartUser) {
		this.chartUser = chartUser;
	}

	public String getChartPwd() {
		return chartPwd;
	}

	public void setChartPwd(String chartPwd) {
		this.chartPwd = chartPwd;
	}

	public int getChartRefreshInterval() {
		return chartRefreshInterval;
	}

	public void setChartRefreshInterval(int chartRefreshInterval) {
		this.chartRefreshInterval = chartRefreshInterval;
	}

	public int getChartWinSize() {
		return chartWinSize;
	}

	public void setChartWinSize(int chartWinSize) {
		this.chartWinSize = chartWinSize;
	}
	
	
}
