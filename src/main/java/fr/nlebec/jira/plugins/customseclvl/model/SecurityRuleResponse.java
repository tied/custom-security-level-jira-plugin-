package fr.nlebec.jira.plugins.customseclvl.model;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class SecurityRuleResponse {
		@XmlElement
		private Integer id;

		@XmlElement
		private Boolean active;

		@XmlElement
		private List<Long> events;
		
		@XmlElement
		private Date applicationDate;
		
		@XmlElement
		private String ruleName;
		@XmlElement
		private Date creationDate;
		@XmlElement
		private Long creationUser;
		
		@XmlElement
		private String jql;
		
		@XmlElement
		private Long securityLvl;
		
		@XmlElement
		private Integer priority;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public Boolean getActive() {
			return active;
		}

		public void setActive(Boolean active) {
			this.active = active;
		}

		public List<Long> getEvents() {
			return events;
		}

		public void setEvents(List<Long> events) {
			this.events = events;
		}

		public String getRuleName() {
			return ruleName;
		}

		public void setRuleName(String ruleName) {
			this.ruleName = ruleName;
		}

		public String getJql() {
			return jql;
		}

		public void setJql(String jql) {
			this.jql = jql;
		}

		public Long getSecurityLvl() {
			return securityLvl;
		}

		public void setSecurityLvl(Long securityLvl) {
			this.securityLvl = securityLvl;
		}

		public Integer getPriority() {
			return priority;
		}

		public void setPriority(Integer priority) {
			this.priority = priority;
		}

		public Date getCreationDate() {
			return creationDate;
		}

		public void setCreationDate(Date creationDate) {
			this.creationDate = creationDate;
		}

		public Long getCreationUser() {
			return creationUser;
		}

		public void setCreationUser(Long creationUser) {
			this.creationUser = creationUser;
		}

		public Date getApplicationDate() {
			return applicationDate;
		}

		public void setApplicationDate(Date applicationDate) {
			this.applicationDate = applicationDate;
		}
	
}
