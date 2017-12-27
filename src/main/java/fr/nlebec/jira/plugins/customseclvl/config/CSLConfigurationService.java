package fr.nlebec.jira.plugins.customseclvl.config;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

import fr.nlebec.jira.plugins.customseclvl.ao.converters.ItemConverter;
import fr.nlebec.jira.plugins.customseclvl.ao.model.CSLConfigurationAO;
import fr.nlebec.jira.plugins.customseclvl.model.CSLConfiguration;
import fr.nlebec.jira.plugins.customseclvl.model.SecurityRules;

@Named
public class CSLConfigurationService {

    private final static Logger LOG = Logger.getLogger(CSLConfigurationService.class);

    private ActiveObjects persistenceManager;
    private I18nHelper i18n;
    private CustomFieldManager customFieldManager;
    private CSLConfiguration configuration;

    @Inject
    public CSLConfigurationService(@ComponentImport ActiveObjects persistenceManager,
                                   @ComponentImport I18nHelper i18n,
                                   @ComponentImport CustomFieldManager customFieldManager)
    {
        this.customFieldManager = customFieldManager;
        this.persistenceManager = checkNotNull(persistenceManager);
        this.i18n = i18n;
    }

    public CSLConfiguration getConfiguration() {
        if (this.configuration == null) {
        	CSLConfigurationAO[] configs = this.persistenceManager.find(CSLConfigurationAO.class);
            if (configs.length > 0) {
                this.configuration = ItemConverter.convertActiveObjectToPOJO(configs[0]);
            } else {
                this.configuration = new CSLConfiguration();
                this.persistenceManager.create(CSLConfigurationAO.class).save();
            }
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

    public void updateConfiguration(boolean isActive){
        if (LOG.isDebugEnabled()) {
            LOG.debug("Saving configuration preferences");
        }

        this.getConfiguration().setActive(isActive);

        CSLConfigurationAO configAo = getConfigurationAo();
        ItemConverter.bindPojoToActiveObject(configuration, configAo);
        configAo.save();
    }
    public void addSecurityRule(SecurityRules securityRule) {
    	if (LOG.isDebugEnabled()) {
            LOG.debug("Add new security rule");
        }
    	
    	CSLConfigurationAO configAo = getConfigurationAo();
    	
    	//Check Security rule
    	this.getConfiguration().getSecurityRules().add(securityRule);
    	
        ItemConverter.bindPojoToActiveObject( this.getConfiguration(), configAo);
        configAo.save();
    }
}