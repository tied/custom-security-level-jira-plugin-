package fr.nlebec.jira.plugins.customseclvl.model;

import java.util.List;

public class CSLConfiguration {

	private Integer id;
	
	private List<SecurityRules> securityRules;
	
	private Boolean active;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<SecurityRules> getSecurityRules() {
		return securityRules;
	}

	public void setSecurityRules(List<SecurityRules> securityRules) {
		this.securityRules = securityRules;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
	
}
