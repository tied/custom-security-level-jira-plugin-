package fr.nlebec.jira.plugins.customseclvl.ao.converters;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;

import fr.nlebec.jira.plugins.customseclvl.ao.model.CSLConfigurationAO;
import fr.nlebec.jira.plugins.customseclvl.ao.model.SecurityRuleAO;
import fr.nlebec.jira.plugins.customseclvl.model.AddSecurityRuleRequestBody;
import fr.nlebec.jira.plugins.customseclvl.model.CSLConfiguration;
import fr.nlebec.jira.plugins.customseclvl.model.SecurityRules;

public class ItemConverter {

	
	private final static Logger LOG = Logger.getLogger(ItemConverter.class);
	
    public static CSLConfiguration convertActiveObjectToPOJO(CSLConfigurationAO configurations) {
    	CSLConfiguration csl = new CSLConfiguration();
    	csl.setSecurityRules(convertActiveObjectToPOJO(configurations.getSecurityRules()));
    	csl.setActive(configurations.getActive());
        return csl;
    }

    public static void bindPojoToActiveObject(CSLConfiguration configuration, CSLConfigurationAO configAo) throws SQLException {
    	configAo.setActive(configuration.getActive());
    	if( configuration.getSecurityRules() != null ){
	    	for (int i = 0; i < configuration.getSecurityRules().size(); i++) {
	    		SecurityRuleAO securityRuleAO = configAo.getEntityManager().create(SecurityRuleAO.class);
	    		bindPojoToActiveObject(configAo ,configuration.getSecurityRules().get(i), securityRuleAO);
			}
    	}
    }
    public static void bindPojoToActiveObject(CSLConfigurationAO configAo, SecurityRules sr, SecurityRuleAO securityRuleAo) throws SQLException {
    	securityRuleAo.setActive(sr.getActive());
    	securityRuleAo.setCreationDate(sr.getCreationDate());
    	securityRuleAo.setCreationUser(sr.getCreationUser().getId());
    	securityRuleAo.setJql(sr.getJql());
    	securityRuleAo.setJiraSecurityId(10000L);
    	securityRuleAo.setName(sr.getName());
    	securityRuleAo.setPriority(sr.getPriority());
    	securityRuleAo.setCSLConfigurationAO(configAo);
    }
    
    public static List<SecurityRules> convertActiveObjectToPOJO(SecurityRuleAO[] srao) {
    	UserManager userManager = ComponentAccessor.getUserManager();
    	List<SecurityRules> list = new ArrayList<>();
    	for (int i = 0; i < srao.length; i++) {
    		SecurityRules sr = new SecurityRules();
    		sr.setActive(srao[i].getActive());
    		sr.setCreationDate(srao[i].getCreationDate());
    		if(UserConverter.convertUsert(userManager, srao[i].getCreationUser()).isPresent()){
    			sr.setCreationUser(UserConverter.convertUsert(userManager, srao[i].getCreationUser()).get());
    		}
    		sr.setDisableDate(srao[i].getDisableDate());
    		if(UserConverter.convertUsert(userManager, srao[i].getDisableUser()).isPresent()){
    			sr.setDisableUser(UserConverter.convertUsert(userManager, srao[i].getDisableUser()).get());
    		}
    		sr.setId(srao[i].getID());
    		sr.setJiraSecurityId(srao[i].getJiraSecurityId());
    		sr.setJql(srao[i].getJql());
    		sr.setName(srao[i].getName());
    		sr.setPriority(srao[i].getPriority());
			list.add(sr);
		}
    	return list;
    }

    
    public static SecurityRules bodyToPojo(AddSecurityRuleRequestBody body, ApplicationUser creationUser) {
    	SecurityRules securityRule = new SecurityRules();
    	securityRule.setActive(body.getActive());
    	securityRule.setCreationDate(new Date());
    	securityRule.setCreationUser(creationUser);
    	//securityRule.setEvents(getEventMapping(body.getEvents()));
    	securityRule.setJiraSecurityId(body.getSecurityLvl());
    	securityRule.setJql(body.getJql());
    	securityRule.setName(body.getRuleName());
    	securityRule.setPriority(body.getPriority());
    	return securityRule;
    }
}




