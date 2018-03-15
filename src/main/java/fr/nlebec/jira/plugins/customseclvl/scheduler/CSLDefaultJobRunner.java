package fr.nlebec.jira.plugins.customseclvl.scheduler;

import java.time.Instant;
import java.time.ZonedDateTime;

import com.atlassian.scheduler.SchedulerServiceException;

public interface CSLDefaultJobRunner {

	public void addSecurityLevelJob(Integer idEntity, ZonedDateTime zonedDateTime) throws SchedulerServiceException;

	public void removeSecurityLevelJob(Integer idSecurityRule, ZonedDateTime applicationDate) throws SchedulerServiceException ;

}
