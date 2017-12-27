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
			request.done(function( data ) {
				
			});
		});
    	if(success){
    	 	AJS.dialog2("#add-dialog").hide();
    	}
});
