package fr.nlebec.jira.plugins.customseclvl.model;

public abstract class CSLResponse {
	
	public String error;

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
