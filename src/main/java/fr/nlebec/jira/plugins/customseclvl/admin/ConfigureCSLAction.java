package fr.nlebec.jira.plugins.customseclvl.admin;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.atlassian.jira.permission.GlobalPermissionKey;
import com.atlassian.jira.security.GlobalPermissionManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserRole;

import fr.nlebec.jira.plugins.customseclvl.config.CSLConfigurationService;
import fr.nlebec.jira.plugins.customseclvl.model.CSLConfiguration;
import fr.nlebec.jira.plugins.customseclvl.model.SecurityRules;

@Scanned
public class ConfigureCSLAction extends JiraWebActionSupport {

	
	private final Logger LOG = Logger.getLogger(ConfigureCSLAction.class);
   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private CSLConfigurationService configurationService;
    private GlobalPermissionManager globalPermissionManager;
    private final LoginUriProvider loginUriProvider;
    
    private Boolean active;

    private List<String> messages;
    private CSLConfiguration configuration;

    @Inject
    public ConfigureCSLAction( CSLConfigurationService configurationService,
                              @ComponentImport GlobalPermissionManager globalPermissionManager,
                              @ComponentImport LoginUriProvider loginUriProvider)
    {
        this.configurationService = configurationService;
        this.globalPermissionManager = globalPermissionManager;
        this.loginUriProvider = loginUriProvider;
    }

    protected String doExecute() throws Exception {
        ApplicationUser loggedInUser = this.getLoggedInUser();
        if (!this.globalPermissionManager.hasPermission(GlobalPermissionKey.ADMINISTER, loggedInUser)) {
            URI uri = URI.create(this.getHttpRequest().getRequestURI());
            return this.forceRedirect(loginUriProvider.getLoginUriForRole(uri, UserRole.ADMIN).toASCIIString());
        }
        
        this.configuration = configurationService.getConfiguration();
        return INPUT;
    }

    public String doSave() throws Exception {
        ApplicationUser loggedInUser = this.getLoggedInUser();
        if (!this.globalPermissionManager.hasPermission(GlobalPermissionKey.ADMINISTER, loggedInUser)) {
            URI uri = URI.create(this.getHttpRequest().getRequestURI());
            return this.forceRedirect(loginUriProvider.getLoginUriForRole(uri, UserRole.ADMIN).toASCIIString());
        }
        //Faire controles
        if( LOG.isDebugEnabled()) {
        	LOG.debug("Active : " + active);
        }
        System.out.println("LOG : "+LOG.isDebugEnabled());
        System.out.println("Active : "+active);
        this.configurationService.updateConfiguration(getActive());
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
	
	public List<SecurityRules> getSecurityRules(){
		return getConfiguration().getSecurityRules();
	}
	
}


