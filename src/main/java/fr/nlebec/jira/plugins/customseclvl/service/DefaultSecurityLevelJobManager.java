package fr.nlebec.jira.plugins.customseclvl.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

import fr.nlebec.jira.plugins.customseclvl.ao.model.JobEntryAO;
import fr.nlebec.jira.plugins.customseclvl.model.SecurityLvlJob;
import fr.nlebec.jira.plugins.customseclvl.model.SecurityRules;
import net.java.ao.Query;

@Named
public class DefaultSecurityLevelJobManager {

	private final static Logger LOG = Logger.getLogger(DefaultSecurityLevelJobManager.class);

	@ComponentImport
	public ActiveObjects persistenceManager;
	
	public SecurityRuleService securityRuleService;

	@Inject
	public DefaultSecurityLevelJobManager( ActiveObjects persistenceManager, SecurityRuleService securityRuleService) {
		this.persistenceManager = checkNotNull(persistenceManager);
		this.securityRuleService = securityRuleService;
	}

	public void deleteJobEntry(String jobName) {
		JobEntryAO[] jobEntries = this.persistenceManager.find(JobEntryAO.class,Query.select().where("JOB_NAME = ?",jobName));
		this.persistenceManager.delete(jobEntries[0]);
	}

	public void persistJobEntry(String jobId, String jobName, int securityLvlId) {
		JobEntryAO jobEntry = this.persistenceManager.create(JobEntryAO.class);
		jobEntry.setSecurityRuleId(securityLvlId);
		jobEntry.setJobName(jobName);
		jobEntry.setJobId(jobId);
		jobEntry.save();
	}

	public List<SecurityLvlJob> getPendingJob() {
		JobEntryAO[] jobEntries = this.persistenceManager.find(JobEntryAO.class,Query.select());
		List<SecurityLvlJob> jobs = new ArrayList<>(); 
		for (JobEntryAO jobEntryAO : jobEntries) {
			SecurityRules sr = null;
			try {
				sr = securityRuleService.getSecurityRule(jobEntryAO.getSecurityRuleId());
			} catch (SQLException e) {
				e.printStackTrace();
			}
			SecurityLvlJob job = new SecurityLvlJob();
			job.setSecurityRule(sr);
			job.setJobId(jobEntryAO.getJobId());
			job.setJobRunnerKey(jobEntryAO.getJobName());
			jobs.add(job);
		}
		return jobs;
	}



}