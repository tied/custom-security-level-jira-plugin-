package fr.nlebec.jira.plugins.customseclvl.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.security.IssueSecurityLevel;
import com.atlassian.jira.user.ApplicationUser;

public class SecurityRules implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String name;
	private ApplicationUser creationUser;
	private Date creationDate;
	private ApplicationUser disableUser;
	private Date applicationDate;
	private Date disableDate;
	private Boolean active;
	private Integer priority;
	private Long jiraSecurityId;
	private List<Event> events;
	private String jql;
	private Boolean deleted;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ApplicationUser getCreationUser() {
		return creationUser;
	}
	public void setCreationUser(ApplicationUser creationUser) {
		this.creationUser = creationUser;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public ApplicationUser getDisableUser() {
		return disableUser;
	}
	public void setDisableUser(ApplicationUser disableUser) {
		this.disableUser = disableUser;
	}
	public Date getDisableDate() {
		return disableDate;
	}
	public void setDisableDate(Date disableDate) {
		this.disableDate = disableDate;
	}
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	public Long getJiraSecurityId() {
		return jiraSecurityId;
	}
	public void setJiraSecurityId(Long jiraSecurityId) {
		this.jiraSecurityId = jiraSecurityId;
	}
	public List<Event> getEvents() {
		return events;
	}
	public void setEvents(List<Event> events) {
		this.events = events;
	}
	public String getJql() {
		return jql;
	}
	public void setJql(String jql) {
		this.jql = jql;
	}
	
	public String getJiraSecurityName() {
		IssueSecurityLevel level = ComponentAccessor.getIssueSecurityLevelManager().getSecurityLevel(getJiraSecurityId());
		String ret = "Unknown";
		if(level != null) {
			ret = level.getName();
		}
		return ret;
		
	}
	public String getSimpleEventsList() {
		StringBuilder ret = new StringBuilder("[");
		for(Event event : this.events) {
			ret.append( event.getJiraEventId() );
			ret.append( "," );
		}
		ret.replace(ret.lastIndexOf(","),ret.lastIndexOf(",")+1,"]");
		return ret.toString();
		
	}
	public Boolean getDeleted() {
		return deleted;
	}
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
	public Date getApplicationDate() {
		return applicationDate;
	}
	public void setApplicationDate(Date applicationDate) {
		this.applicationDate = applicationDate;
	}
	
	
	
}
