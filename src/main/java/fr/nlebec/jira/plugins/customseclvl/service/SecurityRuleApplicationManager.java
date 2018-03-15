package fr.nlebec.jira.plugins.customseclvl.service;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

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

import fr.nlebec.jira.plugins.customseclvl.model.SecurityRules;
import fr.nlebec.jira.plugins.customseclvl.util.EventUtil;

@Named
public class SecurityRuleApplicationManager {

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

	private ApplicationUser adminUser;

	private final Logger LOG = Logger.getLogger(SecurityRuleApplicationManager.class);

	@Inject
	public SecurityRuleApplicationManager(EventPublisher eventPublisher, IssueManager issueManager,
			UserManager userManager, SearchService searchService, SecurityRuleService securityRuleService,
			IssueSecurityLevelManager issueSecurityLevelManager) {
		this.eventPublisher = eventPublisher;
		this.issueManager = issueManager;
		this.searchService = searchService;
		this.userManager = userManager;
		this.issueSecurityLevelManager = issueSecurityLevelManager;
		this.securityRuleService = securityRuleService;
		this.adminUser = userManager.getUser("admin");
	}

	public void removeRuleOnWholeStock(SecurityRules securityRule) {
		this.applyRuleOnJQLExp(securityRule, securityRule.getJql());
		ApplicationUser createdUser = securityRule.getCreationUser();
		final SearchService.ParseResult parseResult = searchService.parseQuery(createdUser, securityRule.getJql());
		if (parseResult.isValid()) {
			try {
				final SearchResults results = searchService.search(createdUser, parseResult.getQuery(),
						com.atlassian.jira.web.bean.PagerFilter.getUnlimitedFilter());
				final List<Issue> issues = results.getIssues();
				try {
					for (Issue issue : issues) {
						MutableIssue mi = issueManager.getIssueByCurrentKey(issue.getKey());
						if (issueSecurityLevelManager.getSecurityLevel(securityRule.getJiraSecurityId()) != null) {
							LOG.info("Removing security level on issue " + mi.getKey());
							mi.setSecurityLevelId(null);
						} else {
							LOG.info("Security level does not exist anymore : " + securityRule.getJiraSecurityId());
							LOG.info("No security level has been applied");
						}

						issueManager.updateIssue(createdUser, mi, EventDispatchOption.DO_NOT_DISPATCH, false);
					}
				} catch (Exception e) {
					LOG.error("Error " + e.getMessage());
				}
			} catch (SearchException e) {
				LOG.error("Error running search", e);
			}
		} else {
			LOG.error("Error parsing jqlQuery: " + parseResult.getErrors());
		}
	}
	
	public void applyRuleOnWholeStock(SecurityRules securityRule) {
		this.applyRuleOnJQLExp(securityRule, securityRule.getJql());
	}

	public void applyRule(SecurityRules securityRule, String issueKey) {
		String jqlQuery = securityRule.getJql() + " AND key = " + issueKey;
		this.applyRuleOnJQLExp(securityRule, jqlQuery);

	}

	private void applyRuleOnJQLExp(SecurityRules securityRule, String jqlQuery) {
		boolean appliedSecurityLevel = false;
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
						if (issueSecurityLevelManager.getSecurityLevel(securityRule.getJiraSecurityId()) != null) {
							LOG.info("Apply security level : " + securityRule.getJiraSecurityId() + " on issue "
									+ mi.getKey());
							mi.setSecurityLevelId(securityRule.getJiraSecurityId());
							appliedSecurityLevel = true;
						} else {
							LOG.info("Security level does not exist anymore : " + securityRule.getJiraSecurityId());
							LOG.info("No security level has been applied");
						}

						issueManager.updateIssue(createdUser, mi, EventDispatchOption.DO_NOT_DISPATCH, false);
					}
				} catch (Exception e) {
					LOG.error("Error " + e.getMessage());
				}
			} catch (SearchException e) {
				LOG.error("Error running search", e);
			}
		} else {
			LOG.error("Error parsing jqlQuery: " + parseResult.getErrors());
		}
	}

}
