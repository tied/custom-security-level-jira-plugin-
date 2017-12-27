package fr.nlebec.jira.plugins.customseclvl.ao.model;

import net.java.ao.Entity;
import net.java.ao.OneToMany;
import net.java.ao.Preload;
import net.java.ao.schema.Table;

@Preload
@Table("CSL_CONF")
public interface CSLConfigurationAO extends Entity {

	
	public Boolean getActive();
	
	public void setActive(Boolean active);
	
	@OneToMany
	public SecurityRuleAO[] getSecurityRules();
}
