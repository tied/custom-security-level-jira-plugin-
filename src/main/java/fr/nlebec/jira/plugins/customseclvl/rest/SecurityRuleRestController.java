package fr.nlebec.jira.plugins.customseclvl.rest;

import java.sql.SQLException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.atlassian.jira.permission.GlobalPermissionKey;
import com.atlassian.jira.security.GlobalPermissionManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

import fr.nlebec.jira.plugins.customseclvl.ao.converters.ItemConverter;
import fr.nlebec.jira.plugins.customseclvl.config.SecurityRuleService;
import fr.nlebec.jira.plugins.customseclvl.model.AddSecurityRuleRequestBody;
import fr.nlebec.jira.plugins.customseclvl.model.AddSecurityRuleResponse;

@Path("/security-rule")
@Scanned
public class SecurityRuleRestController {
	private final Logger log = Logger.getLogger(this.getClass());
	private final UserManager userManager;
	private final GlobalPermissionManager globalPermissionManager;
	private final SecurityRuleService securityRuleService;
	private final I18nHelper i18nHelper;

	@Inject
	public SecurityRuleRestController(@ComponentImport UserManager userManager,
			@ComponentImport GlobalPermissionManager globalPermissionManager, @ComponentImport I18nHelper i18nHelper,
			SecurityRuleService securityRuleService) {
		this.userManager = userManager;
		this.globalPermissionManager = globalPermissionManager;
		this.i18nHelper = i18nHelper;
		this.securityRuleService = securityRuleService;
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/")
	public Response addSecurityLevel(AddSecurityRuleRequestBody body, @Context HttpServletRequest request) {
		String userName = request.getRemoteUser();
		ApplicationUser user = this.userManager.getUserByKey(userName);
		AddSecurityRuleResponse response = new AddSecurityRuleResponse();
		if (!this.globalPermissionManager.hasPermission(GlobalPermissionKey.ADMINISTER, user)) {
			response.setError(this.i18nHelper.getText("fr.csl.admin.error.unauthorized"));
		} else {
			try {
				checkParameters(body);
				this.securityRuleService.addSecurityRule(ItemConverter.bodyToPojo(body, user));
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return Response.ok(response).build();
	}

	private void checkParameters(AddSecurityRuleRequestBody body) {

		if (body.getActive() == null) {
			throw new ValidationException();
		}
		if (StringUtils.isEmpty(body.getRuleName())) {
			throw new ValidationException();
		}
		if (StringUtils.isEmpty(body.getJql())) {
			throw new ValidationException();
		}
		

	}

}
