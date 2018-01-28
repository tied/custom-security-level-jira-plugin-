package fr.nlebec.jira.plugins.customseclvl.scheduler;

import javax.inject.Named;

import org.apache.log4j.Logger;

import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;

@Named
public class CustomSecurityLevelScheduler implements CSLScheduler{

	Logger log = Logger.getLogger(CustomSecurityLevelScheduler.class);
	
	@Override
	public JobRunnerResponse runJob(JobRunnerRequest arg0) {
		log.info("Test !");
		return null;
	}

}
