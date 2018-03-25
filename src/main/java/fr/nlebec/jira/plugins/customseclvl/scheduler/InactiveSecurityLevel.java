package fr.nlebec.jira.plugins.customseclvl.scheduler;

import javax.inject.Named;

import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.config.JobRunnerKey;

@Named
public interface InactiveSecurityLevel extends JobRunner
{
    /** Our job runner key */
    JobRunnerKey INACTIVE_SL_JOB = JobRunnerKey.of(InactiveSecurityLevelTask.class.getName());

    /** Name of the parameter map entry where the ID is stored */
    String INACTIVE_SL_JOB_ID= "InactiveSecurityLevel";
}