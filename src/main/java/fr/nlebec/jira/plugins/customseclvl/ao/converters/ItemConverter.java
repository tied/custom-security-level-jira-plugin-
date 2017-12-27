package fr.nlebec.jira.plugins.customseclvl.ao.converters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.nlebec.jira.plugins.customseclvl.ao.model.CSLConfigurationAO;
import fr.nlebec.jira.plugins.customseclvl.ao.model.SecurityRuleAO;
import fr.nlebec.jira.plugins.customseclvl.model.AddSecurityRuleRequestBody;
import fr.nlebec.jira.plugins.customseclvl.model.CSLConfiguration;
import fr.nlebec.jira.plugins.customseclvl.model.SecurityRules;

public class ItemConverter {

    public static CSLConfiguration convertActiveObjectToPOJO(CSLConfigurationAO configurations) {
    	CSLConfiguration csl = new CSLConfiguration();
    	csl.setSecurityRules(convertActiveObjectToPOJO(configurations.getSecurityRules()));
    	csl.setActive(configurations.getActive());
        return csl;
    }

    public static void bindPojoToActiveObject(CSLConfiguration configuration,CSLConfigurationAO configAo) {
    	configAo.setActive(configuration.getActive());
    	SecurityRuleAO[] srao = new SecurityRuleAO[configuration.getSecurityRules().size()];
    	for (int i = 0; i < configuration.getSecurityRules().size(); i++) {
			bindPojoToActiveObject(configAo ,configuration.getSecurityRules().get(i), srao[i]);
		}
    }
    public static List<SecurityRules> convertActiveObjectToPOJO(SecurityRuleAO[] srao) {
    	List<SecurityRules> list = new ArrayList<>();
    	for (int i = 0; i < srao.length; i++) {
    		SecurityRules sr = new SecurityRules();
			list.add(sr);
		}
    	return list;
    }

    public static void bindPojoToActiveObject(CSLConfigurationAO config,SecurityRules sr,SecurityRuleAO srao) {
    	srao.setActive(sr.getActive());
    	srao.setCreationDate(sr.getCreationDate());
    	srao.setCSLConfigurationAO(config);
    }
    
    public static SecurityRules bodyToPojo(AddSecurityRuleRequestBody body) {
    	SecurityRules securityRule = new SecurityRules();
    	securityRule.setActive(true);
    	securityRule.setCreationDate(new Date());
    	//securityRule.setEvents(getEventMapping(body.getEvents()));
    	securityRule.setJiraSecurityId(body.getSecurityLvl());
    	securityRule.setJql(body.getJql());
    	securityRule.setName(body.getRuleName());
    	securityRule.setPriority(body.getPriority());
    	return securityRule;
    }
}


