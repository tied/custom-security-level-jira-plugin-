AJS.$(document).ready(function () {
	var today = new Date();
	var	oneMonthMaxDate = new Date();
	oneMonthMaxDate.setMonth(today.getMonth()+1);
	
	// Hides the dialog
	AJS.$("#edit-close-button").click(function(e) {
	    e.preventDefault();
	    AJS.$("#edit-form-error").hide();
		AJS.$("#edit-rule-application-date-section").hide();
	    AJS.dialog2("#edit-dialog").hide();
	});
	
	AJS.$("#edit-loading").hide();
	AJS.$("#add-loading").hide();
	AJS.$("#delete-loading").hide();
	
	AJS.$("[id^=unactivate-delete-sr]").tooltip();
	AJS.$('#delete-application-date').datePicker();
    AJS.$('#add-application-date').datePicker();
    AJS.$('#edit-application-date').datePicker();
    
    AJS.$('#delete-application-date').attr("min",formatDate(today));
    AJS.$('#add-application-date').attr("min",formatDate(today));
    AJS.$('#edit-application-date').attr("min",formatDate(today));
    AJS.$('#delete-application-date').attr("max",formatDate(oneMonthMaxDate));
    AJS.$('#add-application-date').attr("max",formatDate(oneMonthMaxDate));
    AJS.$('#edit-application-date').attr("max",formatDate(oneMonthMaxDate));
    
    AJS.$("#btn-add-customsecurity-lvl").click(function(e) {
    	e.preventDefault();
        AJS.dialog2("#add-dialog").show();
    });
    
    AJS.$("#add-rule-active").change(function() {
        if(this.checked) {
        	AJS.$("#add-application-date-section").show();
        }
        else{
        	AJS.$("#add-application-date-section").hide();
        }
    });
    AJS.$("#edit-rule-active").change(function() {
    	var id = $("#edit-rule-id").val()
    	var active = AJS.$("#edit-sr-" + id ).attr("data-active");
    	if((this.checked && !active) || (!this.checked && active)) {
        	AJS.$("#edit-rule-application-date-section").show();
        }
        else{
        	AJS.$("#edit-rule-application-date-section").hide();
        }
    });
	
	
    AJS.$("#add-application-date-section").hide();
    AJS.$("#edit-rule-application-date-section").hide();
    AJS.$("#add-security-lvl").auiSelect2();
    AJS.$("#edit-security-lvl").auiSelect2();
    AJS.$("#add-events").auiSelect2();
    AJS.$("#edit-events").auiSelect2();
    AJS.$("#layout").auiSelect2();
    
    
    AJS.$("#add-form").on("submit", function(e){
    	e.preventDefault();
		var param = {};
		param.jql = $("#add-jql").val();
		param.active= $("#add-rule-active").is(':checked');
		param.securityLvl= $("#add-security-lvl").val();
		param.events = $("#add-events").val();
		param.ruleName = $("#add-rule-name").val();
		param.priority = $("#add-priority").val();
		param.applicationDate = $("#add-application-date").val();
		
		
		AJS.$("#add-form-error").hide();
		AJS.$("#add-loading").show();
		
		var request = AJS.$.ajax({
			  url:  AJS.contextPath() + "/rest/csl/1.0/security-rule",
			  type: "POST",
			  data: JSON.stringify(param),
		      contentType: "application/json",
			  dataType: "json"
			});
			request.success(function( data ) {
				 AJS.dialog2("#add-dialog").hide();
				 AJS.$("#add-loading").hide();
				 //TODO : find another way
				 var  addRedirect = AJS.contextPath() + "/secure/ConfigureSecurityRules.jspa?message=csl.admin.securityrule.add.success"
				 window.location.href = addRedirect ;
			});
			request.fail(function( data ) {
				AJS.$("#add-form-error").show();
				AJS.$("#add-loading").hide();
				var jsonResponse = JSON.parse(data.responseText);
				var errorMessage = jsonResponse.error;
				AJS.$("#add-form-error > p:nth-child(2)").text(errorMessage);
			});
			
		});
    
    
    AJS.$("#edit-form").on("submit", function(e){
    	e.preventDefault();
		var param = {};
		param.id = $("#edit-rule-id").val();
		param.events = $("#edit-events").val();
		param.ruleName = $("#edit-rule-name").val();
		param.priority = $("#edit-priority").val();
		
		AJS.$("#edit-form-error").hide();
		AJS.$("#edit-loading").show();
		
		var request = AJS.$.ajax({
			  url:  AJS.contextPath() + "/rest/csl/1.0/security-rule",
			  type: "PUT",
			  data: JSON.stringify(param),
		      contentType: "application/json",
			  dataType: "json"
			});
			request.success(function( data ) {
				AJS.dialog2("#edit-dialog").hide();
				AJS.$("#edit-loading").hide();
			
				 var  editRedirect = AJS.contextPath() + "/secure/ConfigureSecurityRules.jspa?message=csl.admin.securityrule.update.success"
				 window.location.href = editRedirect ;
			});
			request.fail(function( data ) {
				AJS.$("#edit-form-error").show();
				AJS.$("#edit-loading").hide();
				
				var jsonResponse = JSON.parse(data.responseText);
				var errorMessage = jsonResponse.error;
				AJS.$("#edit-form-error > p:nth-child(2)").text(errorMessage);
				
			
			});
			
		});
    
  
    AJS.$("#delete-form").on("submit", function(e){
    	e.preventDefault();
    	var id =  $("#delete-rule-id").val();
		var param = {};
		param.id = id;
		param.applicationDate =  $("#delete-application-date").val();
		
		AJS.$("#delete-form-error").addClass('hide');
		AJS.$("#delete-loading").show();
		
		var request = AJS.$.ajax({
			  url:  AJS.contextPath() + "/rest/csl/1.0/security-rule",
			  type: "DELETE",
			  data: JSON.stringify(param),
		      contentType: "application/json",
			  dataType: "json"
			});
			request.success(function( data ) {
				AJS.$("div[id=rule-" + id + "]" ).remove();
				alertUser("sucess-messages", AJS.I18n.getText("csl.admin.securityrule.delete.success.msg"));
				var countRule = AJS.$("div[id^=rule-]" ).size();
				if(countRule == 0){
					AJS.$("#no-rule-message").show();
				}
				AJS.dialog2("#delete-dialog").hide();
				var editRedirect = AJS.contextPath() + "/secure/ConfigureSecurityRules.jspa?message=csl.admin.securityrule.update.success"
				window.location.href = editRedirect ;
			});
			
    });
    
    
    AJS.$("[id^=delete-sr]").on("click", function(e){
    	var id = AJS.$(this).attr('data-id');
    	AJS.$("#delete-rule-id").attr("value",id);
    	AJS.dialog2("#delete-dialog").show();
	});
    
    
    AJS.$("[id^=unactivate-sr]").on("click", function(e){
    	AJS.dialog2("#unactivate-dialog").show();
    });
    
    AJS.$("[id^=edit-sr]").on("click", function(e){
    	var id = AJS.$(this).attr('data-id');
    	var name = AJS.$(this).attr('data-name');
    	var priority = AJS.$(this).attr('data-priority');
    	var jql = AJS.$(this).attr('data-jql');
    	var securitylvl =AJS.$(this).attr('data-security-lvl');
    	var events = AJS.$(this).attr('data-events');
    	var eventsAsArray = JSON.parse(events);
    	var active =AJS.$(this).attr('data-active');
    	var activeAsBoolean = (active == 'true');
    	
    	 AJS.$("#edit-rule-id").attr("value",id);
    	 AJS.$("#edit-priority").attr("value",priority);
    	 AJS.$("#edit-rule-name").attr("value",name);
    	 AJS.$("#edit-jql").attr("value",jql);
    	 AJS.$("#edit-security-lvl").attr("value",securitylvl);
    	 AJS.$("#edit-rule-active").prop("checked",activeAsBoolean);
    	 
    	 AJS.$('#edit-security-lvl').val(securitylvl);
    	 AJS.$('#edit-security-lvl').trigger('change.select2');
    	 
    	 AJS.$('#edit-events').val(eventsAsArray);
    	 AJS.$('#edit-events').trigger('change.select2');
    	 
    	AJS.dialog2("#edit-dialog").show();
    });
    function alertUser(idElement, msg ){
    	AJS.$("#" + idElement).show();
		 AJS.$("#" + idElement + " > p:nth-child(2)").text(msg);
		 window.setTimeout(function(){
			 AJS.$("#"+ idElement).hide();
		 }, 5000);
    }
    function addNewrule(location){
    	console.log("GET " + location);
    	var request = AJS.$.ajax({
			  url:  AJS.contextPath() + location,
			  type: "GET",
		      contentType: "application/json",
			  dataType: "json"
			});
			request.success(function( data ) {
				console.data;
			});
    }
    function formatDate(date) {
    	  var separator = '-';
    	  var day = date.getDate();
    	  var month = date.getMonth() + 1;
    	  var year = date.getFullYear();
    	  var hour = date.getHours();
    	  var minute = date.getMinutes();
    	  if( month <= 9){
    		  month = "0" + month; 
    	  }
    	  return year + separator + month + separator + day + 'T' + hour + ":" + minute;
    	}
});

    