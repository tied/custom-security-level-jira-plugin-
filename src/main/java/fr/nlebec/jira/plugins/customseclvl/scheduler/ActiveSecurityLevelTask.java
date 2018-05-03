package fr.nlebec.jira.plugins.customseclvl.scheduler;

import java.io.Serializable;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.Map;

import org.apache.log4j.Logger;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;

import fr.nlebec.jira.plugins.customseclvl.model.SecurityRules;
import fr.nlebec.jira.plugins.customseclvl.service.DefaultSecurityLevelJobManager;
import fr.nlebec.jira.plugins.customseclvl.service.SecurityRuleApplicationManager;
import fr.nlebec.jira.plugins.customseclvl.service.SecurityRuleService;

@Scanned
public class ActiveSecurityLevelTask implements ActiveSecurityLevel {

	private SecurityRuleApplicationManager applicationManager;
	
	private Logger log = Logger.getLogger(ActiveSecurityLevelTask.class);

	private SecurityRuleService securityRuleService;
	
	private DefaultSecurityLevelJobManager jobServices;
	
	public ActiveSecurityLevelTask(SecurityRuleApplicationManager applicationManager, SecurityRuleService service, DefaultSecurityLevelJobManager jobServices) {
		this.applicationManager = applicationManager; 
		this.securityRuleService = service;
		this.jobServices = jobServices;
	}
	
	public JobRunnerResponse runJob(JobRunnerRequest req) {
		JobRunnerResponse resp = JobRunnerResponse.success("Job scheduled");
		Map<String, Serializable> params = req.getJobConfig().getParameters();
		int idSr = (int) params.get("idSecurityRule");
		SecurityRules sr = null;
		try {
			sr = securityRuleService.getSecurityRule(idSr);
			applicationManager.applyRuleOnWholeStock(sr);
			if(sr != null) {
				sr.setActive(Boolean.TRUE);
				securityRuleService.updateSecurityRule(sr);
			}
			jobServices.deleteJobEntry(req.getJobId().toString(),idSr);
		} catch (SQLException e1) {
			resp.failed(e1.getMessage());
		}
		
		return resp;
	}
	
}
