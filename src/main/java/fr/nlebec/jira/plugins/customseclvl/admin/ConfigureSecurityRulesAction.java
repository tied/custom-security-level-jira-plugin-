package fr.nlebec.jira.plugins.customseclvl.admin;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.atlassian.core.util.ObjectUtils;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.event.type.EventTypeManager;
import com.atlassian.jira.issue.security.IssueSecurityLevel;
import com.atlassian.jira.issue.security.IssueSecurityLevelManager;
import com.atlassian.jira.permission.GlobalPermissionKey;
import com.atlassian.jira.security.GlobalPermissionManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserRole;

import fr.nlebec.jira.plugins.customseclvl.model.CSLConfiguration;
import fr.nlebec.jira.plugins.customseclvl.model.CSLConstantes;
import fr.nlebec.jira.plugins.customseclvl.model.SecurityRules;
import fr.nlebec.jira.plugins.customseclvl.service.CSLConfigurationService;

@Scanned
public class ConfigureSecurityRulesAction extends JiraWebActionSupport {

	
	private final Logger LOG = Logger.getLogger(ConfigureSecurityRulesAction.class);
   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private CSLConfigurationService configurationService;
    private GlobalPermissionManager globalPermissionManager;
    private final LoginUriProvider loginUriProvider;
    private IssueSecurityLevelManager issueSecurityLevelManager;
    private EventTypeManager eventManager;
    private Boolean active;

    private Collection<IssueSecurityLevel> securityLevels;
    private Collection<EventType> eventTypes;
    
    private List<String> messages;
    private CSLConfiguration configuration;
    private String message; 
    private I18nHelper i18n;

    @Inject
    public ConfigureSecurityRulesAction( CSLConfigurationService configurationService,
                              @ComponentImport GlobalPermissionManager globalPermissionManager,
                              @ComponentImport LoginUriProvider loginUriProvider,
                              @ComponentImport IssueSecurityLevelManager issueSecurityLevelManager,
                              @ComponentImport EventTypeManager eventTypeManager,
                              @ComponentImport I18nHelper i18nHelper
    		)
    {
    	this.i18n = i18nHelper;
    	this.issueSecurityLevelManager = issueSecurityLevelManager;
        this.configurationService = configurationService;
        this.globalPermissionManager = globalPermissionManager;
        this.loginUriProvider = loginUriProvider;
        this.eventManager = eventTypeManager ; 
    }

    protected String doExecute() throws Exception {
        ApplicationUser loggedInUser = this.getLoggedInUser();
        if (!this.globalPermissionManager.hasPermission(GlobalPermissionKey.ADMINISTER, loggedInUser)) {
            URI uri = URI.create(this.getHttpRequest().getRequestURI());
            return this.forceRedirect(loginUriProvider.getLoginUriForRole(uri, UserRole.ADMIN).toASCIIString());
        }
        
        this.configuration = configurationService.getConfiguration();
        this.securityLevels = issueSecurityLevelManager.getAllIssueSecurityLevels();
        this.eventTypes = this.eventManager.getEventTypes();
        
        return INPUT;
    }

    public String doSave() throws Exception {
        ApplicationUser loggedInUser = this.getLoggedInUser();
        if (!this.globalPermissionManager.hasPermission(GlobalPermissionKey.ADMINISTER, loggedInUser)) {
            URI uri = URI.create(this.getHttpRequest().getRequestURI());
            return this.forceRedirect(loginUriProvider.getLoginUriForRole(uri, UserRole.ADMIN).toASCIIString());
        }
        //Faire controles
        this.configurationService.updateConfiguration(getActive());
        this.securityLevels = issueSecurityLevelManager.getAllIssueSecurityLevels();
        
        return INPUT;
    }

	public Boolean getActive() {
		if(active == null) {
			active = Boolean.FALSE;
		}
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public CSLConfiguration getConfiguration() {
		return configurationService.getConfiguration();
	}
	
	public List<SecurityRules> getActivesSecurityRules(){
		return getConfiguration().getActivesSecurityRules();
	}
	public List<SecurityRules> getInactivesSecurityRules(){
		return getConfiguration().getInactivesSecurityRules();
	}
	public List<SecurityRules> getDeletedSecurityRules(){
		return getConfiguration().getDeletedSecurityRules();
	}
	public Collection<IssueSecurityLevel> getLevelList() {
		return securityLevels;
	}

	public Collection<EventType> getEventTypes() {
		return eventTypes;
	}

	public void setEventTypes(Collection<EventType> eventTypes) {
		this.eventTypes = eventTypes;
	}
	public String getMessage() {
		String ret = "";
		if(this.message != null){
			ret = i18n.getText(this.message);
		}
		return ret;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	public String formatDate(ZonedDateTime zdt) {
		String ret = "-";
		if( ObjectUtils.isNotEmpty(zdt) ) {
			ret = zdt.format(CSLConstantes.simpleFormatter);
		}
		return ret;
	}
}


