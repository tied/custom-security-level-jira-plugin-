package fr.nlebec.jira.plugins.customseclvl.activity;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventDispatchOption;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

import fr.nlebec.jira.plugins.customseclvl.config.SecurityRuleService;
import fr.nlebec.jira.plugins.customseclvl.model.CSLConfiguration;
import fr.nlebec.jira.plugins.customseclvl.model.SecurityRules;
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

	public SecurityRuleService securityRuleService;

	@Inject
	public CustomSecurityLvlDefaultListener(EventPublisher eventPublisher, IssueManager issueManager,
			UserManager userManager, SearchService searchService, SecurityRuleService securityRuleService) {
		this.eventPublisher = eventPublisher;
		this.issueManager = issueManager;
		this.searchService = searchService;
		this.userManager = userManager;
		this.securityRuleService = securityRuleService;
	}

	@EventListener
	public void onIssueEvent(IssueEvent event) {
		LOG.info("Evenement : " + event.getEventTypeId());
		CSLConfiguration config = securityRuleService.getConfiguration();
		LOG.info("Est-ce que le plugin est activé : "+config.getActive().booleanValue());
		if (Boolean.TRUE.equals(config.getActive())) {
			for (SecurityRules securityRule : config.getSecurityRules()) {
				LOG.info("Do security level : " + securityRule.getName() + " is active ? "+securityRule.getActive());
				if (Boolean.TRUE.equals(securityRule.getActive())) {
					LOG.info("Events : "+EventType.ISSUE_CREATED_ID + " | " + EventType.ISSUE_UPDATED_ID+ " | "+EventType.ISSUE_COMMENTED_ID);
					if ( EventUtil.contains(securityRule.getEvents(), event.getEventTypeId())) {
						LOG.info("Apply security level : " + securityRule.getName());
						String jqlQuery = securityRule.getJql() + " AND key = " + event.getIssue().getKey();
						ApplicationUser createdUser = securityRule.getCreationUser();
						final SearchService.ParseResult parseResult = searchService.parseQuery(createdUser, jqlQuery);
						LOG.info("JQL Request " + jqlQuery);
						if (parseResult.isValid()) {
							try {
								final SearchResults results = searchService.search(createdUser, parseResult.getQuery(),
										com.atlassian.jira.web.bean.PagerFilter.getUnlimitedFilter());
								LOG.info("Created User " + createdUser.getName());
								final List<Issue> issues = results.getIssues();
								LOG.info("Count issue : " + results.getIssues().size());
								try {
									for (Issue issue : issues) {
										MutableIssue mi = issueManager.getIssueByCurrentKey(issue.getKey());
										LOG.info("Apply security level : " + securityRule.getJiraSecurityId()
												+ "on issue " + mi.getKey());
										mi.setSecurityLevelId(securityRule.getJiraSecurityId());
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
	}

	public void destroy() throws Exception {
		this.eventPublisher.unregister(this);
	}

	public void afterPropertiesSet() throws Exception {
		this.eventPublisher.register(this);
	}
}
