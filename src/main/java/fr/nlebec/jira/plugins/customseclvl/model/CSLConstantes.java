package fr.nlebec.jira.plugins.customseclvl.model;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public interface CSLConstantes {

	
	static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm").withZone(ZoneId.systemDefault());;
	
	static DateTimeFormatter simpleFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault());;
}
