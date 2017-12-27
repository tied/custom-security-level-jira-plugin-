package fr.nlebec.jira.plugins.customseclvl.ao.model;

import net.java.ao.Entity;

public interface EventToSecurityRule extends Entity {

	
	public void setEvent(EventAO eventAO);
    public EventAO getEvent();

    public void setSecurityRule(SecurityRuleAO securityRuleAo);
    public SecurityRuleAO getSecurityRule();
}
