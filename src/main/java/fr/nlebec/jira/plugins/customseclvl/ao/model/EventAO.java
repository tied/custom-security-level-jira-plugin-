package fr.nlebec.jira.plugins.customseclvl.ao.model;

import net.java.ao.ManyToMany;

public interface EventAO extends net.java.ao.Entity  {

	
	public Long getJiraId();
	
	public void setJiraId(Long jiraId);
	
	public String getJiraName();

	public void setJiraName(String jiraName);
	
	@ManyToMany(value = EventToSecurityRule.class)
	public SecurityRuleAO getSecurityRules();
}

