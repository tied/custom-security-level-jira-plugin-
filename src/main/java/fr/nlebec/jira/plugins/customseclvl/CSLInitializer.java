package fr.nlebec.jira.plugins.customseclvl;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.sal.api.lifecycle.LifecycleAware;

import fr.nlebec.jira.plugins.customseclvl.service.CSLConfigurationService;

@ExportAsService
@Component
public class CSLInitializer implements LifecycleAware {

		static DateTimeFormatter simpleFormatter ;  
	
		static CSLConfigurationService configService;
		
		static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm").withZone(ZoneId.systemDefault());
		
		static DateTimeFormatter technicalDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		
		static DateTimeFormatter technicalTimeFormatter = DateTimeFormatter.ofPattern("hh:mm");
		
		
		@Inject
		public CSLInitializer(CSLConfigurationService configService) {
			this.configService = configService;
		}
		
		public void onStart() {
			simpleFormatter =  DateTimeFormatter.ofPattern( configService.getConfiguration().getDateFormat()).withZone(ZoneId.systemDefault());
		}

		public void onStop() {
			
		}
		
		
		public static DateTimeFormatter getDateTimeFormatter() {
			if( simpleFormatter == null ) {
				simpleFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault());
			}
			else {
				simpleFormatter = DateTimeFormatter.ofPattern( configService.getConfiguration().getDateFormat()).withZone(ZoneId.systemDefault());
			}
			return simpleFormatter;
		}
		
		public static DateTimeFormatter getDefaultDateTimeFormatter() {
			return formatter;
		}
		public static DateTimeFormatter getTechnicalDateFormatter() {
			return technicalDateFormatter;
		}
		public static DateTimeFormatter getTechnicalTimeFormatter() {
			return technicalTimeFormatter;
		}
	}