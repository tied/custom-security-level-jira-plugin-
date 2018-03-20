package fr.nlebec.jira.plugins.customseclvl.scheduler;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;

import fr.nlebec.jira.plugins.customseclvl.model.SecurityLvlJob;
import fr.nlebec.jira.plugins.customseclvl.service.DefaultSecurityLevelJobManager;
import fr.nlebec.jira.plugins.customseclvl.service.SecurityRuleApplicationManager;
import fr.nlebec.jira.plugins.customseclvl.service.SecurityRuleService;


@ExportAsService
@Component
public class DefaultCSLDefaultJobRunner implements CSLDefaultJobRunner,LifecycleAware  {

	Logger log = Logger.getLogger(DefaultCSLDefaultJobRunner.class);
	
	private SchedulerService schedulerService;

	private SecurityRuleApplicationManager securityRuleApplicationManager;
	
	private SecurityRuleService securityRulesService;
	
	private DefaultSecurityLevelJobManager jobService;
	
	@Inject
	public DefaultCSLDefaultJobRunner(@ComponentImport SchedulerService schedulerService, SecurityRuleApplicationManager securityRuleApplicationManager, SecurityRuleService securityRulesService, DefaultSecurityLevelJobManager jobService)  {
		this.schedulerService = schedulerService ;
		this.securityRuleApplicationManager = securityRuleApplicationManager;
		this.securityRulesService = securityRulesService;
		this.jobService = jobService;
	}

	public void addSecurityLevelJob(Integer idSecurityRule, ZonedDateTime applicationDate) throws SchedulerServiceException {
		JobConfig jobConfig = this.createJobConfig(idSecurityRule, applicationDate, ApplySecurityLevel.APPLY_SL_JOB);
		JobId jobId = schedulerService.scheduleJobWithGeneratedId(jobConfig);
		jobService.persistJobEntry(jobId.toString(), ApplySecurityLevel.APPLY_SL_JOB.toString(), idSecurityRule );
		log.info("Persist Job Entry  " + ApplySecurityLevel.APPLY_SL_JOB.toString() + " : " + jobId.toString());
	}
	
	public void removeSecurityLevelJob(Integer idSecurityRule, ZonedDateTime applicationDate) throws SchedulerServiceException {
		JobConfig jobConfig = this.createJobConfig(idSecurityRule, applicationDate, RemoveSecurityLevel.REMOVE_SL_JOB);
		JobId jobId = schedulerService.scheduleJobWithGeneratedId(jobConfig);
		jobService.persistJobEntry(jobId.toString(),RemoveSecurityLevel.REMOVE_SL_JOB.toString() ,  idSecurityRule);
		log.info("Persist Job Entry  " + RemoveSecurityLevel.REMOVE_SL_JOB.toString() + " : " + jobId.toString());
	}
	


	public void onStop() {
		log.info("*********************************** onStop");
	     schedulerService.unregisterJobRunner(ApplySecurityLevel.APPLY_SL_JOB);
	     schedulerService.unregisterJobRunner(RemoveSecurityLevel.REMOVE_SL_JOB);
	     
	     List<SecurityLvlJob> pendingJobs = jobService.getPendingJob();
			for (SecurityLvlJob pendingJob : pendingJobs) {
				schedulerService.unscheduleJob(JobId.of(pendingJob.getJobId()));
			}
	}

	public JobConfig createJobConfig(int idSecurityRule, ZonedDateTime instant,JobRunnerKey jobRunnerKey){
		Map<String, Serializable> params = new HashMap<>();
		params.put("idSecurityRule", idSecurityRule);
		final JobConfig jobConfig = JobConfig.forJobRunnerKey(jobRunnerKey)
                .withSchedule(Schedule.runOnce(Date.from(instant.toInstant())))
                .withRunMode(RunMode.RUN_LOCALLY)
                .withParameters(params);
		return jobConfig;
	}

	public void onStart() {
		log.info("*********************************** onStart");
		schedulerService.registerJobRunner(ApplySecurityLevel.APPLY_SL_JOB, new ApplySecurityLevelTask(securityRuleApplicationManager,securityRulesService,jobService));
		schedulerService.registerJobRunner(RemoveSecurityLevel.REMOVE_SL_JOB, new RemoveSecurityLevelTask(securityRuleApplicationManager,securityRulesService,jobService));
		
		List<SecurityLvlJob> pendingJobs = jobService.getPendingJob();
		
		for (SecurityLvlJob pendingJob : pendingJobs) {
			JobConfig jobConfig = createJobConfig(pendingJob.getSecurityRule().getId(),pendingJob.getSecurityRule().getApplicationDate(), JobRunnerKey.of(pendingJob.getJobRunnerKey()));
			try {
				schedulerService.scheduleJob(JobId.of(pendingJob.getJobId()), jobConfig);
			} catch (SchedulerServiceException e) {
				//TODO:Add Exception handling
				e.printStackTrace();
			}
		}
		
	}


	
	
}
