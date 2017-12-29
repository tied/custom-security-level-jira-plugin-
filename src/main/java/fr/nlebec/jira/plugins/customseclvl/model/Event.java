package fr.nlebec.jira.plugins.customseclvl.model;

public class Event {

	private Long jiraEventId;
	
	private String jiraEventName;

	public Long getJiraEventId() {
		return jiraEventId;
	}

	public void setJiraEventId(Long jiraEventId) {
		this.jiraEventId = jiraEventId;
	}

	public String getJiraEventName() {
		return jiraEventName;
	}

	public void setJiraEventName(String jiraEventName) {
		this.jiraEventName = jiraEventName;
	}

	@Override
	public String toString() {
		return "Event [jiraEventId=" + jiraEventId + ", jiraEventName=" + jiraEventName + "]";
	}
	
	

}
