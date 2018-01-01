AJS.$(document).ready(function () {
	
    AJS.$("#btn-add-customsecurity-lvl").click(function(e) {
    	e.preventDefault();
        AJS.dialog2("#add-dialog").show();
    });
    
    AJS.$("#add-security-lvl").auiSelect2();
    AJS.$("#edit-security-lvl").auiSelect2();
    AJS.$("#add-events").auiSelect2();
    AJS.$("#edit-events").auiSelect2();
    
    
    AJS.$("#add-form").on("submit", function(e){
    	e.preventDefault();
		var param = {};
		param.jql = $("#add-jql").val();
		param.active= $("#add-rule-active").is(':checked');
		param.securityLvl= $("#add-security-lvl").val();
		param.events = $("#add-events").val();
		param.ruleName = $("#add-rule-name").val();
		param.priority = $("#add-priority").val();
		
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
		param.jql = $("#edit-jql").val();
		param.active= $("#edit-rule-active").is(':checked');
		param.securityLvl= $("#edit-security-lvl").val();
		param.events = $("#edit-events").val();
		param.ruleName = $("#edit-rule-name").val();
		param.priority = $("#edit-priority").val();
		
		AJS.$("#edit-form-error").addClass('hide');
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
				AJS.$("#edit-form-error").removeClass('hide');
				AJS.$("#edit-loading").hide();
				
				var jsonResponse = JSON.parse(data.responseText);
				var errorMessage = jsonResponse.error;
				AJS.$("#edit-form-error > p:nth-child(2)").text(errorMessage);
				
			
			});
			
		});
    
    AJS.$("[id^=delete-sr]").on("click", function(e){
    	var id = AJS.$(this).attr('data-id');
    	var name = AJS.$(this).attr('data-name');
    	var param = {};
    	param.id = id;
    	if(confirm(AJS.I18n.getText("csl.admin.securityrule.delete.confirm.msg",name))){
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
				});
	    	}
		});
    AJS.$("[id^=edit-sr]").on("click", function(e){
    	var id = AJS.$(this).attr('data-id');
    	var name = AJS.$(this).attr('data-name');
    	var priority = AJS.$(this).attr('data-priority');
    	var jql = AJS.$(this).attr('data-jql');
    	var securitylvl =AJS.$(this).attr('data-security-lvl');
    	var active =AJS.$(this).attr('data-active');
    	var activeAsBoolean = (active == 'true');
    	var events = AJS.$(this).attr('data-events');
    	var eventsAsArray = JSON.parse(events);
    	
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
});

    