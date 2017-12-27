package fr.nlebec.jira.plugins.customseclvl.admin;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;

import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.permission.GlobalPermissionKey;
import com.atlassian.jira.security.GlobalPermissionManager;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserRole;

import fr.nlebec.jira.plugins.customseclvl.config.CSLConfigurationService;

@Scanned
public class ConfigureCSLAction extends JiraWebActionSupport {

   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private CSLConfigurationService configurationService;
    private GlobalPermissionManager globalPermissionManager;
    private CustomFieldManager customFieldManager;
    private GroupManager groupManager;
    private ProjectRoleManager projectRoleManager;
    private final LoginUriProvider loginUriProvider;
    
    private Boolean active = true;

    private List<String> messages;


    @Inject
    public ConfigureCSLAction( CSLConfigurationService configurationService,
                              @ComponentImport GlobalPermissionManager globalPermissionManager,
                              @ComponentImport CustomFieldManager customFieldManager,
                              @ComponentImport GroupManager groupManager,
                              @ComponentImport ProjectRoleManager projectRoleManager,
                              @ComponentImport LoginUriProvider loginUriProvider)
    {
        this.configurationService = configurationService;
        this.globalPermissionManager = globalPermissionManager;
        this.customFieldManager = customFieldManager;
        this.groupManager = groupManager;
        this.projectRoleManager = projectRoleManager;
        this.loginUriProvider = loginUriProvider;
    }

    protected String doExecute() throws Exception {
        ApplicationUser loggedInUser = this.getLoggedInUser();
        if (!this.globalPermissionManager.hasPermission(GlobalPermissionKey.ADMINISTER, loggedInUser)) {
            URI uri = URI.create(this.getHttpRequest().getRequestURI());
            return this.forceRedirect(loginUriProvider.getLoginUriForRole(uri, UserRole.ADMIN).toASCIIString());
        }
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
        return INPUT;
    }

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
    
}
