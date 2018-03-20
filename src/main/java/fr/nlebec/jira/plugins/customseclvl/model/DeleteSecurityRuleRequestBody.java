package fr.nlebec.jira.plugins.customseclvl.model;

import java.time.ZonedDateTime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import fr.nlebec.jira.plugins.customseclvl.CSLInitializer;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class DeleteSecurityRuleRequestBody {

	@XmlElement(name="id")
	private Integer idSecurityRuleToDelete;
	
	@XmlElement(name="applicationDate")
	private String applicationDate;

	public Integer getIdSecurityRuleToDelete() {
		return idSecurityRuleToDelete;
	}

	public void setIdSecurityRuleToDelete(Integer idSecurityRuleToDelete) {
		this.idSecurityRuleToDelete = idSecurityRuleToDelete;
	}

	public String getApplicationDate() {
		return applicationDate;
	}

	public void setApplicationDate(String applicationDate) {
		this.applicationDate = applicationDate;
	}
	public ZonedDateTime getApplicationDateAsZoneDateTime() {
		return ZonedDateTime.parse(applicationDate, CSLInitializer.getDefaultDateTimeFormatter());
	}
	
}
