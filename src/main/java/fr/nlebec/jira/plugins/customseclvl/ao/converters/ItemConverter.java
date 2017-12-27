package fr.nlebec.jira.plugins.customseclvl.ao.converters;

import java.util.ArrayList;
import java.util.List;

import fr.nlebec.jira.plugins.customseclvl.ao.model.CSLConfigurationAO;
import fr.nlebec.jira.plugins.customseclvl.ao.model.SecurityRuleAO;
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
			bindPojoToActiveObject(configuration.getSecurityRules().get(i),srao[i]);
		}
    	//configAo.setSecurityRules(srao);
    }
    public static List<SecurityRules> convertActiveObjectToPOJO(SecurityRuleAO[] srao) {
    	List<SecurityRules> list = new ArrayList<>();
    	for (int i = 0; i < srao.length; i++) {
    		SecurityRules sr = new SecurityRules();
			list.add(sr);
		}
    	return list;
    }

    public static void bindPojoToActiveObject(SecurityRules sr,SecurityRuleAO srao) {
    	srao.setActive(sr.getActive());
    	srao.setCreationDate(sr.getCreationDate());
    }
}
