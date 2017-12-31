package fr.nlebec.jira.plugins.customseclvl.util;

import java.util.List;

import org.apache.log4j.Logger;

import fr.nlebec.jira.plugins.customseclvl.model.Event;

public class EventUtil {

	private final static Logger log = Logger.getLogger(EventUtil.class);
	
	public static boolean contains(List<Event> events, Long actualEventId){
		for(Event event : events ){
			if(event.getJiraEventId().equals(actualEventId)){
				log.debug("Event "+actualEventId+" match with security rule ");
				return true;
			}
		}
		return false;
	}
	
}
