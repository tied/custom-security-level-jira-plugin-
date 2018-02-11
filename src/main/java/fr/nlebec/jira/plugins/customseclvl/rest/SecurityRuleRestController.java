package fr.nlebec.jira.plugins.customseclvl.rest;

import java.sql.SQLException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
import com.atlassian.scheduler.SchedulerServiceException;

import fr.nlebec.jira.plugins.customseclvl.ao.converters.ItemConverter;
import fr.nlebec.jira.plugins.customseclvl.model.AddSecurityRuleRequestBody;
import fr.nlebec.jira.plugins.customseclvl.model.AddSecurityRuleResponse;
import fr.nlebec.jira.plugins.customseclvl.model.DeleteSecurityRuleRequestBody;
import fr.nlebec.jira.plugins.customseclvl.model.DeleteSecurityRuleResponse;
import fr.nlebec.jira.plugins.customseclvl.model.RetrieveSecurityRuleResponse;
import fr.nlebec.jira.plugins.customseclvl.model.SecurityRuleResponse;
import fr.nlebec.jira.plugins.customseclvl.model.UpdateSecurityRuleRequestBody;
import fr.nlebec.jira.plugins.customseclvl.model.UpdateSecurityRuleResponse;
import fr.nlebec.jira.plugins.customseclvl.scheduler.CSLDefaultJobRunner;
import fr.nlebec.jira.plugins.customseclvl.service.SecurityRuleService;

@Path("/security-rule")
@Scanned
public class SecurityRuleRestController {
	private final Logger log = Logger.getLogger(this.getClass());
	private final UserManager userManager;
	private final GlobalPermissionManager globalPermissionManager;
	private final SecurityRuleService securityRuleService;
	private final I18nHelper i18nHelper;
	private SearchService searchService;
	private final CSLDefaultJobRunner jobRunner;
	
	@Inject
	public SecurityRuleRestController(@ComponentImport UserManager userManager,
			@ComponentImport GlobalPermissionManager globalPermissionManager, @ComponentImport I18nHelper i18nHelper,
			SecurityRuleService securityRuleService, @ComponentImport SearchService searchService, CSLDefaultJobRunner jobRunner) {
		this.userManager = userManager;
		this.globalPermissionManager = globalPermissionManager;
		this.i18nHelper = i18nHelper;
		this.securityRuleService = securityRuleService;
		this.searchService = searchService;
		this.jobRunner = jobRunner;
	}

	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/{idSecurityRule}")
	public Response getSecurityLevel(@PathParam(value = "idSecurityRule") int idSecurityRule, @Context HttpServletRequest request) {
		String userName = request.getRemoteUser();
		ApplicationUser user = this.userManager.getUserByKey(userName);
		RetrieveSecurityRuleResponse response = new RetrieveSecurityRuleResponse();
		int errorCode = 200;
		if (!this.globalPermissionManager.hasPermission(GlobalPermissionKey.ADMINISTER, user)) {
			response.setError(this.i18nHelper.getText("fr.csl.admin.error.unauthorized"));
		} else {
			try {
				SecurityRuleResponse resp = ItemConverter.pojoToResponse(this.securityRuleService.getSecurityRule(idSecurityRule));
				response.setSecurityRule(resp);
			} catch (SQLException e) {
				errorCode = 500;
				response.setError(e.getMessage());
			} catch (ValidationException e) {
				errorCode = 400;
				response.setError(e.getMessage());
			}
		}
		return Response.status(errorCode).entity(response).build();
	}
	
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/")
	public Response addSecurityLevel(AddSecurityRuleRequestBody ruleBody, @Context HttpServletRequest request) {
		String userName = request.getRemoteUser();
		ApplicationUser user = this.userManager.getUserByKey(userName);
		AddSecurityRuleResponse response = new AddSecurityRuleResponse();
		int errorCode = 201;
		System.out.println(ruleBody.getApplicationDate());
		if (!this.globalPermissionManager.hasPermission(GlobalPermissionKey.ADMINISTER, user)) {
			response.setError(this.i18nHelper.getText("fr.csl.admin.error.unauthorized"));
		} else {
			try {
				checkParameters(ruleBody, user);
				int idEntity = this.securityRuleService.addSecurityRule(ItemConverter.bodyToPojo(ruleBody, user));
				if( Boolean.TRUE.equals(ruleBody.getActive() && ruleBody.getApplicationDate() != null)) {
					jobRunner.applySecurityLevelJob(idEntity,ruleBody.getApplicationDate());
				}
				response.setLocation(request.getRequestURI() + "/" + idEntity );
			} catch (SQLException e) {
				errorCode = 500;
				response.setError(e.getMessage());
			} catch (ValidationException e) {
				errorCode = 400;
				response.setError(e.getMessage());
			} catch (SchedulerServiceException e) {
				errorCode = 500;
				response.setError(e.getMessage());
				e.printStackTrace();
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
		System.out.println(body.getApplicationDate());
		int errorCode = 201;
		if (!this.globalPermissionManager.hasPermission(GlobalPermissionKey.ADMINISTER, user)) {
			response.setError(this.i18nHelper.getText("fr.csl.admin.error.unauthorized"));
		} else {
			try {
				// checkParameters(body);
				this.securityRuleService.deleteSecurityRule(body.getIdSecurityRuleToDelete(),body.getApplicationDate());
				if( body.getApplicationDate() != null) {
					jobRunner.removeSecurityLevelJob(body.getIdSecurityRuleToDelete(),body.getApplicationDate());
				}
			} catch (ValidationException e) {
				errorCode = 400;
				response.setError(e.getMessage());
			} catch (SchedulerServiceException e) {
				errorCode = 500;
				response.setError(e.getMessage());
			}
		}
		return Response.status(errorCode).entity(response).build();
	}

	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/")
	public Response updateSecurityLevel(UpdateSecurityRuleRequestBody body, @Context HttpServletRequest request)
			throws SQLException {
		String userName = request.getRemoteUser();
		ApplicationUser user = this.userManager.getUserByKey(userName);
		UpdateSecurityRuleResponse response = new UpdateSecurityRuleResponse();
		int errorCode = 201;
		if (!this.globalPermissionManager.hasPermission(GlobalPermissionKey.ADMINISTER, user)) {
			response.setError(this.i18nHelper.getText("fr.csl.admin.error.unauthorized"));
		} else {
			try {
				checkParameters(body, user);
				this.securityRuleService.updateSecurityRule(ItemConverter.bodyToPojo(body, user));
				response.setLocation(request.getRequestURI() + "/" + body.getId() );
			} catch (SQLException e) {
				errorCode = 500;
				response.setError(e.getMessage());
			} catch (ValidationException e) {
				errorCode = 400;
				response.setError(e.getMessage());
			}
		}
		return Response.status(errorCode).entity(response).build();
	}

	private void checkParameters(AddSecurityRuleRequestBody body, ApplicationUser user) {
		final SearchService.ParseResult parseResult = searchService.parseQuery(user, body.getJql());

		if (body.getActive() == null) {
			throw new ValidationException("Parametre active n'est pas valide");
		}
		if (body.getRuleName() == null || StringUtils.isEmpty(body.getRuleName())) {
			throw new ValidationException("Le nom de la règle est vide");
		}
		if (body.getJql() == null || StringUtils.isEmpty(body.getJql())) {
			throw new ValidationException("Le JQL est vide ou invalide");
		}
		if (body.getEvents() == null || body.getEvents().size() == 0) {
			throw new ValidationException("Au moins un evenement doit être définis");
		}
		if (body.getPriority() == null) {
			throw new ValidationException("Parametre prorité n'est pas valide");
		}
		try {
			MessageSet msgSet = searchService.validateQuery(user, parseResult.getQuery());
			if (msgSet.hasAnyErrors()) {
				StringBuilder sb = new StringBuilder();
				for (String msg : msgSet.getErrorMessages()) {
					sb.append(msg);
					sb.append("\t\n");
				}
				throw new ValidationException(sb.toString());
			}
		} catch (Exception e) {
			throw new ValidationException(e.getMessage());
		}

	}

	private void checkParameters(UpdateSecurityRuleRequestBody body, ApplicationUser user) {
		final SearchService.ParseResult parseResult = searchService.parseQuery(user, body.getJql());

		if (body.getId() == null) {
			throw new ValidationException("Paramètre ID n'est pas valide");
		}
		if (body.getActive() == null) {
			throw new ValidationException("Parametre active n'est pas valide");
		}
		if (body.getRuleName() == null || StringUtils.isEmpty(body.getRuleName())) {
			throw new ValidationException("Le nom de la règle est vide");
		}
		if (body.getJql() == null || StringUtils.isEmpty(body.getJql())) {
			throw new ValidationException("Le JQL est vide ou invalide");
		}
		if (body.getEvents() == null || body.getEvents().size() == 0) {
			throw new ValidationException("Au moins un evenement doit être définis");
		}
		try {
			MessageSet msgSet = searchService.validateQuery(user, parseResult.getQuery());
			if (msgSet.hasAnyErrors()) {
				StringBuilder sb = new StringBuilder();
				for (String msg : msgSet.getErrorMessages()) {
					sb.append(msg);
					sb.append("\t\n");
				}
				throw new ValidationException(sb.toString());
			}
		} catch (Exception e) {
			throw new ValidationException(e.getMessage());
		}

	}

}
