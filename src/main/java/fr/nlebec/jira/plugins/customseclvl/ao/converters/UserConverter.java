package fr.nlebec.jira.plugins.customseclvl.ao.converters;

import java.util.Optional;

import com.atlassian.jira.user.ApplicationUser;

public class UserConverter {

	public static Optional<ApplicationUser> convertUsert(com.atlassian.jira.user.util.UserManager userManager , Long userId){
		return userManager.getUserById(userId);
		
	}
	
}
