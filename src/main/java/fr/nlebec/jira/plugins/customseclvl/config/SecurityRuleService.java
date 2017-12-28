package fr.nlebec.jira.plugins.customseclvl.config;

import static com.google.common.base.Preconditions.checkNotNull;

import java.sql.SQLException;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

import fr.nlebec.jira.plugins.customseclvl.ao.converters.ItemConverter;
import fr.nlebec.jira.plugins.customseclvl.ao.model.CSLConfigurationAO;
import fr.nlebec.jira.plugins.customseclvl.ao.model.SecurityRuleAO;
import fr.nlebec.jira.plugins.customseclvl.model.CSLConfiguration;
import fr.nlebec.jira.plugins.customseclvl.model.SecurityRules;

@Named
public class SecurityRuleService {

    private final static Logger LOG = Logger.getLogger(SecurityRuleService.class);

    private ActiveObjects persistenceManager;
    private I18nHelper i18n;
    private CustomFieldManager customFieldManager;
    private CSLConfiguration configuration;
    private UserManager userManager;

    @Inject
    public SecurityRuleService(@ComponentImport ActiveObjects persistenceManager,
                                   @ComponentImport I18nHelper i18n,
                                   @ComponentImport CustomFieldManager customFieldManager,
                                   @ComponentImport UserManager userManager
    		)
    {
        this.customFieldManager = customFieldManager;
        this.persistenceManager = checkNotNull(persistenceManager);
        this.i18n = i18n;
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


    public void addSecurityRule(SecurityRules securityRule) throws SQLException {
    	LOG.info("Add new security rule : "+ securityRule.toString());
    	this.getConfiguration().getSecurityRules().add(securityRule);
    	
    	SecurityRuleAO securityRuleAO = this.persistenceManager.create(SecurityRuleAO.class); 
    	
        ItemConverter.bindPojoToActiveObject(getConfigurationAo(),securityRule, securityRuleAO);
        
        LOG.info("Save security rule : "+ securityRuleAO.toString());
        securityRuleAO.save();
    }
}