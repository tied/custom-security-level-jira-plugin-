package fr.nlebec.jira.plugins.customseclvl.ao.converters;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.event.type.EventTypeManager;
import com.atlassian.jira.user.ApplicationUser;

import fr.nlebec.jira.plugins.customseclvl.ao.model.CSLConfigurationAO;
import fr.nlebec.jira.plugins.customseclvl.ao.model.EventAO;
import fr.nlebec.jira.plugins.customseclvl.ao.model.EventToSecurityRule;
import fr.nlebec.jira.plugins.customseclvl.ao.model.SecurityRuleAO;
import fr.nlebec.jira.plugins.customseclvl.model.AddSecurityRuleRequestBody;
import fr.nlebec.jira.plugins.customseclvl.model.CSLConfiguration;
import fr.nlebec.jira.plugins.customseclvl.model.Event;
import fr.nlebec.jira.plugins.customseclvl.model.SecurityRules;

public class ItemConverter {

	private final static Logger LOG = Logger.getLogger(ItemConverter.class);

	public static CSLConfiguration convertActiveObjectToPOJO(CSLConfigurationAO configurations) {
		CSLConfiguration csl = new CSLConfiguration();
		csl.setSecurityRules(convertActiveObjectToPOJO(configurations.getSecurityRules()));
		csl.setActive(configurations.getActive());
		return csl;
	}

	public static void bindPojoToActiveObject(CSLConfiguration configuration, CSLConfigurationAO configAo)
			throws SQLException {
		configAo.setActive(configuration.getActive());
		if (configuration.getSecurityRules() != null) {
			for (int i = 0; i < configuration.getSecurityRules().size(); i++) {
				SecurityRuleAO securityRuleAO = configAo.getEntityManager().create(SecurityRuleAO.class);
				bindPojoToActiveObject(configAo, configuration.getSecurityRules().get(i), securityRuleAO);
			}
		}
	}

	public static void bindPojoToActiveObject(CSLConfigurationAO configAo, SecurityRules sr,
			SecurityRuleAO securityRuleAo) throws SQLException {
		securityRuleAo.setActive(sr.getActive());
		securityRuleAo.setCreationDate(sr.getCreationDate());
		securityRuleAo.setCreationUser(sr.getCreationUser().getId());
		securityRuleAo.setJql(sr.getJql());
		
		EventToSecurityRule associationAo = configAo.getEntityManager().create(EventToSecurityRule.class); 
		EventAO eventAO = configAo.getEntityManager().create(EventAO.class);
		
		for(Event e : sr.getEvents()) {
			bindPojoToActiveObject(eventAO, securityRuleAo, associationAo, e);
		}
		securityRuleAo.setJiraSecurityId(sr.getJiraSecurityId());
		securityRuleAo.setName(sr.getName());
		securityRuleAo.setPriority(sr.getPriority());
		securityRuleAo.setCSLConfigurationAO(configAo);
	}

	public static void bindPojoToActiveObject(EventAO eventAO,
		SecurityRuleAO securityRuleAo, EventToSecurityRule eventToSR, Event event) throws SQLException {
		EventTypeManager eventTypeManager = ComponentAccessor.getEventTypeManager();
		eventToSR.setSecurityRule(securityRuleAo);
		eventToSR.setEvent(eventAO);
		eventAO.setJiraId(event.getJiraEventId());
		eventAO.setJiraName(eventTypeManager.getEventType(event.getJiraEventId()).getName());
	}
	
	public static List<SecurityRules> convertActiveObjectToPOJO(SecurityRuleAO[] srao) {
		List<SecurityRules> list = new ArrayList<>();
		for (int i = 0; i < srao.length; i++) {
			SecurityRules sr = new SecurityRules();
			sr.setActive(srao[i].getActive());
			sr.setCreationDate(srao[i].getCreationDate());
			if (UserConverter.convertUsert(srao[i].getCreationUser()).isPresent()) {
				sr.setCreationUser(UserConverter.convertUsert(srao[i].getCreationUser()).get());
			}
			if (srao[i].getDisableDate() != null) {
				sr.setDisableDate(srao[i].getDisableDate());
			}
			if (srao[i].getDisableUser() != null && UserConverter.convertUsert(srao[i].getDisableUser()).isPresent()) {
				sr.setDisableUser(UserConverter.convertUsert(srao[i].getDisableUser()).get());
			}
			sr.setId(srao[i].getID());
			sr.setJiraSecurityId(srao[i].getJiraSecurityId());
			sr.setJql(srao[i].getJql());
			sr.setName(srao[i].getName());
			sr.setPriority(srao[i].getPriority());
			
			List<Event> events = new ArrayList<>();
			for(EventAO eventAo : srao[i].getEvents()) {
				Event event = new Event();
				event.setJiraEventId(eventAo.getJiraId());
				event.setJiraEventName(eventAo.getJiraName());
				events.add(event);
			}
			sr.setEvents(events);
			list.add(sr);
		}

		return list.stream().sorted(
				(elem1, elem2) -> elem1.getPriority().compareTo(elem2.getPriority()))
				.collect(Collectors.toList());
	}

	public static SecurityRules bodyToPojo(AddSecurityRuleRequestBody body, ApplicationUser creationUser) {
		SecurityRules securityRule = new SecurityRules();
		securityRule.setActive(body.getActive());
		securityRule.setCreationDate(new Date());
		securityRule.setCreationUser(creationUser);
		securityRule.setEvents(getEventMapping(body.getEvents()));
		securityRule.setJiraSecurityId(body.getSecurityLvl());
		securityRule.setJql(body.getJql());
		securityRule.setName(body.getRuleName());
		securityRule.setPriority(body.getPriority());
		return securityRule;
	}


	
	private static List<Event> getEventMapping(List<Long> events) {
		List<Event> eventsCLS = new ArrayList<>();
		for (Long event : events) {
			Event eventCSL = new Event();
			eventCSL.setJiraEventId(event);
			eventsCLS.add(eventCSL);
		}
		return eventsCLS;
	}
}
