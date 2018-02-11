package fr.nlebec.jira.plugins.customseclvl.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class DeleteSecurityRuleRequestBody {

	@XmlElement(name="id")
	private Integer idSecurityRuleToDelete;
	
	@XmlElement(name="applicationDate")
	private Date applicationDate;

	public Integer getIdSecurityRuleToDelete() {
		return idSecurityRuleToDelete;
	}

	public void setIdSecurityRuleToDelete(Integer idSecurityRuleToDelete) {
		this.idSecurityRuleToDelete = idSecurityRuleToDelete;
	}

	public Date getApplicationDate() {
		return applicationDate;
	}

	public void setApplicationDate(Date applicationDate) {
		this.applicationDate = applicationDate;
	}
	
}
