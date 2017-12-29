package fr.nlebec.jira.plugins.customseclvl.util;

import java.util.List;

import com.atlassian.event.api.EventPublisher;

import fr.nlebec.jira.plugins.customseclvl.model.Event;

public class EventUtil {

	public static boolean contains(List<Event> events, Long actualEventId){
		for(Event event : events ){
			if(event.getJiraEventId() == actualEventId){
				return true;
			}
		}
		return false;
	}
	
}
