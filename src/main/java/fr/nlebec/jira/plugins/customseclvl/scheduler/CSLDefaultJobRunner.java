package fr.nlebec.jira.plugins.customseclvl.scheduler;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;

import fr.nlebec.jira.plugins.customseclvl.service.SecurityRuleApplicationManager;
import fr.nlebec.jira.plugins.customseclvl.service.SecurityRuleService;

@Named
@Scanned
public class CSLDefaultJobRunner implements InitializingBean, DisposableBean,LifecycleAware {

	Logger log = Logger.getLogger(ApplySecurityLevelTask.class);
	
	private SchedulerService schedulerService;

	private SecurityRuleApplicationManager securityRuleApplicationManager;
	
	private SecurityRuleService securityRulesService;
	
	
	@Inject
	public CSLDefaultJobRunner(@ComponentImport final SchedulerService schedulerService, SecurityRuleApplicationManager securityRuleApplicationManager, SecurityRuleService securityRulesService)  {
		this.schedulerService = schedulerService ;
		this.securityRuleApplicationManager = securityRuleApplicationManager;
		this.securityRulesService = securityRulesService;
		
	}

	public void applySecurityLevelJob(int idSecurityRule, Date applicationDate) throws SchedulerServiceException {
		Map<String, Serializable> params = new HashMap<>();
		params.put("idSecurityRule", idSecurityRule);
		final JobConfig jobConfig = JobConfig.forJobRunnerKey(ApplySecurityLevel.APPLY_SL_JOB)
                .withSchedule(Schedule.runOnce(applicationDate))
                .withRunMode(RunMode.RUN_LOCALLY)
                .withParameters(params);
		JobId jobId = schedulerService.scheduleJobWithGeneratedId(jobConfig);
		log.info("JobID : " + jobId.toString());
	}
	
	public void removeSecurityLevelJob(int idSecurityRule, Date applicationDate) throws SchedulerServiceException {
		Map<String, Serializable> params = new HashMap<>();
		params.put("idSecurityRule", idSecurityRule);
		final JobConfig jobConfig = JobConfig.forJobRunnerKey(RemoveSecurityLevel.REMOVE_SL_JOB)
                .withSchedule(Schedule.runOnce(applicationDate))
                .withRunMode(RunMode.RUN_LOCALLY)
                .withParameters(params);
		JobId jobId = schedulerService.scheduleJobWithGeneratedId(jobConfig);
		log.info("JobID : " + jobId.toString());
	}
	
	public void onStart() {
	}

	public void onStop() {
		
	}

	public void destroy() throws Exception {
		
	}

	public void afterPropertiesSet() throws Exception {
		schedulerService.registerJobRunner(ApplySecurityLevel.APPLY_SL_JOB, new ApplySecurityLevelTask(securityRuleApplicationManager,securityRulesService));
		schedulerService.registerJobRunner(RemoveSecurityLevel.REMOVE_SL_JOB, new RemoveSecurityLevelTask(securityRuleApplicationManager,securityRulesService));
		log.info("Apply security level Job defined !");
	}
	
}
