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

import fr.nlebec.jira.plugins.customseclvl.rest.admin.AssociationResponse;

@Named
public class DefaultActivityService implements InitializingBean, DisposableBean {

    private final Logger LOG = Logger.getLogger(DefaultActivityService.class);
	
    @ComponentImport
    public UserManager userManager;
    @ComponentImport
    public IssueManager issueManager;
    @ComponentImport
    public SearchService searchService;
	@ComponentImport 
	public EventPublisher eventPublisher;

	@Inject
	public DefaultActivityService(
		EventPublisher eventPublisher,IssueManager issueManager,UserManager userManager,SearchService searchService) {
		this.eventPublisher = eventPublisher;
		this.issueManager = issueManager;
		this.searchService = searchService;
		this.userManager = userManager;
	}
	
    @EventListener
    public void onIssueEvent(IssueEvent event) {
    	LOG.info("Plugin Nico : " + event.getEventTypeId());
        if (event.getEventTypeId().equals(EventType.ISSUE_CREATED_ID) || event.getEventTypeId().equals(EventType.ISSUE_UPDATED_ID) ) {
        	LOG.info("Apply security level");
    		String jqlQuery = "project = PRJ";
    		ApplicationUser userAdmin = this.userManager.getUserByKey("admin");
    		final SearchService.ParseResult parseResult = searchService.parseQuery(userAdmin, jqlQuery);
    		LOG.info("parsed result "+ parseResult );
    		if (parseResult.isValid()) {
    			try {
    				final SearchResults results = searchService.search(userAdmin, parseResult.getQuery(),
    						com.atlassian.jira.web.bean.PagerFilter.getUnlimitedFilter());
    				LOG.info("Admin User "+ userAdmin.getName());
    				final List<Issue> issues = results.getIssues();
    				LOG.info("List issue size : "+ results.getIssues().size());
    				try {
    					for (Issue issue : issues) {
    						MutableIssue mi = issueManager.getIssueByCurrentKey(issue.getKey());
    						LOG.info("Apply security level : 10000L sur issue "+mi.getKey());
    						mi.setSecurityLevelId(10000L);
    						issueManager.updateIssue(userAdmin, mi, EventDispatchOption.DO_NOT_DISPATCH, false);
    					}
    				} catch (Exception e) {
    					LOG.error("error " + e.getMessage() );
    					e.printStackTrace();
    				}
    			} catch (SearchException e) {
    				LOG.error("Error running search", e);
    			}
    		} else {
    			LOG.warn("Error parsing jqlQuery: " + parseResult.getErrors());
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
