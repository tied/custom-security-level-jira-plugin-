package fr.nlebec.jira.plugins.customseclvl.scheduler;

import javax.inject.Named;

import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.config.JobRunnerKey;

@Named
public interface ApplySecurityLevel extends JobRunner
{
    /** Our job runner key */
    JobRunnerKey APPLY_SL_JOB = JobRunnerKey.of(ApplySecurityLevelTask.class.getName());

    /** Name of the parameter map entry where the ID is stored */
    String APPLY_SL_JOB_ID= "ApplySecurityLevel";
}