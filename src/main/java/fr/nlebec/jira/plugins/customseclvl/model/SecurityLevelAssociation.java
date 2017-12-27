package fr.nlebec.jira.plugins.customseclvl.model;

import java.util.List;

import com.atlassian.event.api.EventPublisher;

public class SecurityLevelAssociation {
    
	private int id;
	
	private String name;
	
	private String securityLvlId;
	
	private String jql;
	
	private int orderId;
	
	private List<EventPublisher> triggers;
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSecurityLvlId() {
		return securityLvlId;
	}

	public void setSecurityLvlId(String securityLvlId) {
		this.securityLvlId = securityLvlId;
	}

	public String getJql() {
		return jql;
	}

	public void setJql(String jql) {
		this.jql = jql;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public List<EventPublisher> getTriggers() {
		return triggers;
	}

	public void setTriggers(List<EventPublisher> triggers) {
		this.triggers = triggers;
	}
	
}
