AJS.$(document).ready(function () {
	
    AJS.$("#btn-add-customsecurity-lvl").click(function(e) {
    	e.preventDefault();
        AJS.dialog2("#add-dialog").show();
    });
    AJS.$("#security-lvl").auiSelect2();
    AJS.$("#events").auiSelect2();
    
    AJS.$("#add-form").on("submit", function(e){
    	e.preventDefault();
    	var success = true;
		var param = {};
		param.jql = $("#jql").val();
		param.active= $("#active").val();
		param.securityLvl= $("#security-lvl").val();
		param.events = $("#events").val();
		param.ruleName = $("#rule-name").val();
		param.priority = $("#priority").val();
		
		var request = $.ajax({
			  url:  AJS.contextPath() + "/rest/csl/1.0/security-rule",
			  type: "POST",
			  data: JSON.stringify(param),
		      contentType: "application/json",
			  dataType: "json"
			});
			request.success(function( data ) {
				 AJS.dialog2("#add-dialog").hide();
				 location.reload();
			});
			
		});
    AJS.$("[id^=delete-sr]").on("click", function(e){
    	console.log(AJS.$(this).attr('data-id'));
    	var id = AJS.$(this).attr('data-id');
    	var name = AJS.$(this).attr('data-name');
    	var param = {};
    	param.id = id;
    	if(confirm("Est-vous sur de supprimer la règle : " + name)){
	    	var request = $.ajax({
				  url:  AJS.contextPath() + "/rest/csl/1.0/security-rule",
				  type: "DELETE",
				  data: JSON.stringify(param),
			      contentType: "application/json",
				  dataType: "json"
				});
				request.success(function( data ) {
					 location.reload();
				});
	    	}
		});
});
