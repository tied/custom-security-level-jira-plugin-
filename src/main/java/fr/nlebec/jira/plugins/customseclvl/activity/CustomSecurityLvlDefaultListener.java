package fr.nlebec.jira.plugins.customseclvl.activity;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.ofbiz.core.entity.GenericEntityException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventDispatchOption;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.issue.security.IssueSecurityLevelManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.Schedule;
import com.google.common.collect.ImmutableMap;

import fr.nlebec.jira.plugins.customseclvl.model.CSLConfiguration;
import fr.nlebec.jira.plugins.customseclvl.model.SecurityRules;
import fr.nlebec.jira.plugins.customseclvl.scheduler.CustomSecurityLevelScheduler;
import fr.nlebec.jira.plugins.customseclvl.service.SecurityRuleService;
import fr.nlebec.jira.plugins.customseclvl.util.EventUtil;

@Named
public class CustomSecurityLvlDefaultListener implements InitializingBean, DisposableBean {

	private final Logger LOG = Logger.getLogger(CustomSecurityLvlDefaultListener.class);

	@ComponentImport
	public UserManager userManager;
	@ComponentImport
	public IssueManager issueManager;
	@ComponentImport
	public SearchService searchService;
	@ComponentImport
	public EventPublisher eventPublisher;
	@ComponentImport
	public IssueSecurityLevelManager issueSecurityLevelManager;
	
	public SecurityRuleService securityRuleService;

	@Inject
	public CustomSecurityLvlDefaultListener(EventPublisher eventPublisher, IssueManager issueManager,
			UserManager userManager, SearchService searchService, SecurityRuleService securityRuleService, IssueSecurityLevelManager issueSecurityLevelManager) {
		this.eventPublisher = eventPublisher;
		this.issueManager = issueManager;
		this.searchService = searchService;
		this.userManager = userManager;
		this.issueSecurityLevelManager = issueSecurityLevelManager;
		this.securityRuleService = securityRuleService;
	}

	@EventListener
	public void onIssueEvent(IssueEvent event) throws GenericEntityException {
		boolean appliedSecurityLevel = false;
		ApplicationUser adminUser = userManager.getUser("admin");
		CSLConfiguration config = securityRuleService.getConfiguration();
		LOG.info("Is plugin active : "+config.getActive().booleanValue());
		if (Boolean.TRUE.equals(config.getActive())) {
			for (SecurityRules securityRule : config.getActivesSecurityRules()) {
				LOG.info("Do security lvl : " + securityRule.getName() + " is active ? "+securityRule.getActive());
				if (Boolean.TRUE.equals(securityRule.getActive())) {
					if ( EventUtil.contains(securityRule.getEvents(), event.getEventTypeId())) {
						String jqlQuery = securityRule.getJql() + " AND key = " + event.getIssue().getKey();
						ApplicationUser createdUser = securityRule.getCreationUser();
						final SearchService.ParseResult parseResult = searchService.parseQuery(createdUser, jqlQuery);
						if (parseResult.isValid()) {
							try {
								final SearchResults results = searchService.search(createdUser, parseResult.getQuery(),
										com.atlassian.jira.web.bean.PagerFilter.getUnlimitedFilter());
								final List<Issue> issues = results.getIssues();
								try {
									for (Issue issue : issues) {
										MutableIssue mi = issueManager.getIssueByCurrentKey(issue.getKey());
										
										if(issueSecurityLevelManager.getSecurityLevel(securityRule.getJiraSecurityId()) != null) {
											LOG.info("Apply security level : " + securityRule.getJiraSecurityId()+ " on issue " + mi.getKey());
											mi.setSecurityLevelId(securityRule.getJiraSecurityId());
											appliedSecurityLevel = true;
										}
										else {
											LOG.info("Security level does not exist anymore : " + securityRule.getJiraSecurityId());
											LOG.info("No security level has been applied");
										}
										
										issueManager.updateIssue(createdUser, mi, EventDispatchOption.DO_NOT_DISPATCH,
												false);
									}
								} catch (Exception e) {
									LOG.error("Error " + e.getMessage());
								}
							} catch (SearchException e) {
								LOG.error("Error running search", e);
							}
						} else {
							LOG.warn("Error parsing jqlQuery: " + parseResult.getErrors());
						}
					}
				}
			}
		}
		
		
		if (appliedSecurityLevel == false) {
			LOG.info("No security level has been applied : removing existing security level");
			MutableIssue mi = issueManager.getIssueByCurrentKey(event.getIssue().getKey());
			mi.setSecurityLevelId(null);
			issueManager.updateIssue(adminUser, mi, EventDispatchOption.DO_NOT_DISPATCH,
					false);
		}
	}

	public void destroy() throws Exception {
		this.eventPublisher.unregister(this);
	}

	public void afterPropertiesSet() throws Exception {
		this.eventPublisher.register(this);
	}
}
