package fr.nlebec.jira.plugins.customseclvl.model;

public class SecurityLvlJob {



	private String jobId;
	
	private String jobRunnerKey;
	
	private SecurityRules securityRule;

	
	public String getJobRunnerKey() {
		return jobRunnerKey;
	}

	public void setJobRunnerKey(String jobRunnerKey) {
		this.jobRunnerKey = jobRunnerKey;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public SecurityRules getSecurityRule() {
		return securityRule;
	}

	public void setSecurityRule(SecurityRules securityRule) {
		this.securityRule = securityRule;
	}
}
