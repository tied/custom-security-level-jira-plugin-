package fr.nlebec.jira.plugins.customseclvl.rest;

import java.sql.SQLException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.permission.GlobalPermissionKey;
import com.atlassian.jira.security.GlobalPermissionManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.util.MessageSet;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

import fr.nlebec.jira.plugins.customseclvl.ao.converters.ItemConverter;
import fr.nlebec.jira.plugins.customseclvl.config.SecurityRuleService;
import fr.nlebec.jira.plugins.customseclvl.model.AddSecurityRuleRequestBody;
import fr.nlebec.jira.plugins.customseclvl.model.AddSecurityRuleResponse;
import fr.nlebec.jira.plugins.customseclvl.model.DeleteSecurityRuleRequestBody;
import fr.nlebec.jira.plugins.customseclvl.model.DeleteSecurityRuleResponse;

@Path("/security-rule")
@Scanned
public class SecurityRuleRestController {
	private final Logger log = Logger.getLogger(this.getClass());
	private final UserManager userManager;
	private final GlobalPermissionManager globalPermissionManager;
	private final SecurityRuleService securityRuleService;
	private final I18nHelper i18nHelper;
	private SearchService searchService; 

	@Inject
	public SecurityRuleRestController(@ComponentImport UserManager userManager,
			@ComponentImport GlobalPermissionManager globalPermissionManager, @ComponentImport I18nHelper i18nHelper,
			SecurityRuleService securityRuleService,
			  @ComponentImport SearchService searchService
			) {
		this.userManager = userManager;
		this.globalPermissionManager = globalPermissionManager;
		this.i18nHelper = i18nHelper;
		this.securityRuleService = securityRuleService;
		this.searchService = searchService;
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/")
	public Response addSecurityLevel(AddSecurityRuleRequestBody body, @Context HttpServletRequest request) {
		String userName = request.getRemoteUser();
		ApplicationUser user = this.userManager.getUserByKey(userName);
		AddSecurityRuleResponse response = new AddSecurityRuleResponse();
		int errorCode = 200;
		if (!this.globalPermissionManager.hasPermission(GlobalPermissionKey.ADMINISTER, user)) {
			response.setError(this.i18nHelper.getText("fr.csl.admin.error.unauthorized"));
		} else {
			try {
				checkParameters(body,userName);
				this.securityRuleService.addSecurityRule(ItemConverter.bodyToPojo(body, user));
			} catch (SQLException e) {
				errorCode = 500;
				response.setError(e.getMessage());
			}
			catch (ValidationException e) {
				errorCode = 400;
				response.setError(e.getMessage());
			}
		}
		return Response.status(errorCode).entity(response).build();
	}
	@DELETE
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/")
	public Response deleteSecurityLevel(DeleteSecurityRuleRequestBody body, @Context HttpServletRequest request) {
		String userName = request.getRemoteUser();
		ApplicationUser user = this.userManager.getUserByKey(userName);
		DeleteSecurityRuleResponse response = new DeleteSecurityRuleResponse();
		if (!this.globalPermissionManager.hasPermission(GlobalPermissionKey.ADMINISTER, user)) {
			response.setError(this.i18nHelper.getText("fr.csl.admin.error.unauthorized"));
		} else {
			//checkParameters(body);
			this.securityRuleService.deleteSecurityRule(body.getIdSecurityRuleToDelete());
		}
		return Response.ok(response).build();
	}

	private void checkParameters(AddSecurityRuleRequestBody body, String userName) {
		ApplicationUser user = this.userManager.getUserByName(userName);
		final SearchService.ParseResult parseResult = searchService.parseQuery(user, body.getJql());
		MessageSet msgSet = searchService.validateQuery(this.userManager.getUserByName(userName), parseResult.getQuery());
		
		System.out.println(body.getRuleName() + " "+body.getJql());
		if (body.getActive() == null) {
			throw new ValidationException("Parametre active n'est pas valide");
		}
		if (body.getRuleName() == null || StringUtils.isEmpty(body.getRuleName())) {
			throw new ValidationException("Le nom de la règle est vide");
		}
		if (body.getJql() == null || StringUtils.isEmpty(body.getJql())) {
			throw new ValidationException("Le JQL est vide ou invalide");
		}
		if(msgSet.hasAnyErrors()) {
			StringBuilder sb= new StringBuilder();
			for(String msg : msgSet.getErrorMessages()) {
				sb.append(msg);
				sb.append("\t\n");
			}
			throw new ValidationException(sb.toString());
		}

	}

}
