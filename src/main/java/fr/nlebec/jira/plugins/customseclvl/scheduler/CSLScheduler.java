package fr.nlebec.jira.plugins.customseclvl.scheduler;

import javax.inject.Named;

import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.config.JobRunnerKey;

@Named
public interface CSLScheduler extends JobRunner
{
    /** Our job runner key */
    JobRunnerKey CSL_JOB = JobRunnerKey.of(CustomSecurityLevelScheduler.class.getName());

    /** Name of the parameter map entry where the ID is stored */
    String CSL_ID = "DefaultCSLJob";
}