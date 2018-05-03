package fr.nlebec.jira.plugins.customseclvl.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class CancelPendingActionRequestBody {

	@XmlElement(name="id")
	private Integer idSecurityRule;
	
	public Integer getIdSecurityRule() {
		return idSecurityRule;
	}

	public void setIdSecurityRule(Integer idSecurityRule) {
		this.idSecurityRule = idSecurityRule;
	}
	
}
