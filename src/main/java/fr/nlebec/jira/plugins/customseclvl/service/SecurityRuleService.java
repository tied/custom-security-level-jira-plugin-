package fr.nlebec.jira.plugins.customseclvl.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.sql.SQLException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

import fr.nlebec.jira.plugins.customseclvl.ao.converters.ItemConverter;
import fr.nlebec.jira.plugins.customseclvl.ao.model.CSLConfigurationAO;
import fr.nlebec.jira.plugins.customseclvl.ao.model.EventAO;
import fr.nlebec.jira.plugins.customseclvl.ao.model.SecurityRuleAO;
import fr.nlebec.jira.plugins.customseclvl.model.CSLConfiguration;
import fr.nlebec.jira.plugins.customseclvl.model.Event;
import fr.nlebec.jira.plugins.customseclvl.model.SecurityRules;
import net.java.ao.Query;

@Named
public class SecurityRuleService {

    private final static Logger LOG = Logger.getLogger(SecurityRuleService.class);

    private ActiveObjects persistenceManager;
    private CustomFieldManager customFieldManager;
    private CSLConfiguration configuration;
    private EventService eventService;

    @Inject
    public SecurityRuleService(@ComponentImport ActiveObjects persistenceManager,
                                   @ComponentImport CustomFieldManager customFieldManager,
                                   EventService eventService
                                   
    		){
        this.customFieldManager = customFieldManager;
        this.persistenceManager = checkNotNull(persistenceManager);
        this.eventService = eventService;
    }

	public CSLConfiguration getConfiguration() {
		CSLConfigurationAO[] configs = this.persistenceManager.find(CSLConfigurationAO.class);
		if (configs.length > 0) {
			this.configuration = ItemConverter.convertActiveObjectToPOJO(configs[0]);
		} else {
			this.configuration = new CSLConfiguration();
			this.persistenceManager.create(CSLConfigurationAO.class).save();
		}
		return this.configuration;
	}
    
    public CSLConfigurationAO getConfigurationAo() {
        CSLConfigurationAO[] configs = this.persistenceManager.find(CSLConfigurationAO.class);
        CSLConfigurationAO configAo;
        
        if (configs.length == 0) {
            configAo = this.persistenceManager.create(CSLConfigurationAO.class);
        } else {
            configAo = configs[0];
        }
        return configAo;
    }

    public SecurityRules getSecurityRule(int idSecurityRule) throws SQLException {
    	LOG.info("Get new security rule : "+ idSecurityRule);
    	SecurityRuleAO[] securityRuleAO = this.persistenceManager.find(SecurityRuleAO.class,Query.select().where("ID = ?",idSecurityRule)); 
    	List<SecurityRules> securityRules = ItemConverter.convertActiveObjectToPOJO(securityRuleAO);
        return securityRules.get(0);
    }

    public int addSecurityRule(SecurityRules securityRule) throws SQLException {
    	LOG.info("Add new security rule : "+ securityRule.toString());
    	
    	SecurityRuleAO securityRuleAO = this.persistenceManager.create(SecurityRuleAO.class); 
    	ItemConverter.bindPojoToActiveObject(getConfigurationAo(),securityRule, securityRuleAO);

    	//Before saving security Rule : add transitives dependances
    	for(Event e : securityRule.getEvents()){
    		LOG.info("Event : "+ e.toString());
    		eventService.addEvent(e, securityRuleAO);
    	}
    	
        LOG.info("Save security rule : "+ securityRuleAO.toString());
        securityRuleAO.save();
        
        return securityRuleAO.getID();
    }
    public void updateSecurityRule(SecurityRules securityRule) throws SQLException {
    	LOG.info("Update existing security rule : "+ securityRule.toString());
    	
    	SecurityRuleAO[] securityRuleAO = this.persistenceManager.find(SecurityRuleAO.class, Query.select().where("ID = ?",securityRule.getId()));
    	
    	
    	ItemConverter.bindPojoToActiveObject(getConfigurationAo(),securityRule, securityRuleAO[0]);

    	//Bottom up approach : we delete associations first
    	for (EventAO eventAO : securityRuleAO[0].getEvents()) {
    		eventService.deleteEvent(eventAO, securityRuleAO[0]);
		}
    	
    	//Before saving security Rule : add transitives dependances
    	for(Event e : securityRule.getEvents()){
    		eventService.addEvent(e, securityRuleAO[0]);
    	}
        LOG.info("Update security rule : "+ securityRuleAO.toString());
        securityRuleAO[0].save();
    }
	public void deleteSecurityRule(Integer idSecurityRuleToDelete, ZonedDateTime applicationDeleteDate) {
		
		SecurityRuleAO[] securityRules = this.persistenceManager.find(SecurityRuleAO.class,Query.select().where("ID = ?",idSecurityRuleToDelete));
		securityRules[0].setApplicationDate(Date.from(applicationDeleteDate.toInstant()));
		securityRules[0].save();
        
	}
}
