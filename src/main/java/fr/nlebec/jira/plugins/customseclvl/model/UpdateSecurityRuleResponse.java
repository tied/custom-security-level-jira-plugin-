package fr.nlebec.jira.plugins.customseclvl.model;

public class UpdateSecurityRuleResponse extends CSLResponse {

	public String location;
	
	public UpdateSecurityRuleResponse() {
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}
