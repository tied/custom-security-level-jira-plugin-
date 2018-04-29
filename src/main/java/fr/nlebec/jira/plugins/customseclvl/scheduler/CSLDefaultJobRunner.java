package fr.nlebec.jira.plugins.customseclvl.scheduler;

import java.time.ZonedDateTime;

import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobId;

public interface CSLDefaultJobRunner {

	public void addSecurityLevelJob(Integer idEntity, ZonedDateTime zonedDateTime) throws SchedulerServiceException;

	public void removeSecurityLevelJob(Integer idSecurityRule, ZonedDateTime applicationDate) throws SchedulerServiceException ;
	
	public void disableSecurityLevelJob(Integer idSecurityRule, ZonedDateTime applicationDate) throws SchedulerServiceException ;

	public void activateSecurityLevelJob(Integer idSecurityRule, ZonedDateTime applicationDate) throws SchedulerServiceException ;
	
	public void removeTaskFromScheduler(JobId jobid);
}
