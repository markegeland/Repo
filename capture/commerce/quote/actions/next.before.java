/*
===================================================================================
Name:   next.before.java
Author:   
Create date:  
Description:  The before formulas logic for the general Next action.  dialog box.  Sets the status and the submitted date.
        
Input:   	_system_company_name - String - system being used
                _system_current_step_var - String - current step 
                _quote_process_customer_id - customer ID
	        division_quote - String - Lawson Division
		salesActivity_quote - String - New, Existing Customer, Change of Owner
		SiteNumber_quote - customer site number
		
                    
Output:  	String - Output parsed as HTML by the attribute to display table 
                of attributes

Updates:	04/04/15 - Mike (Republic) - added reporting status
===================================================================================
*/
retStr = "";
status = "";
orderMgmtEmailStr = "";
quoteAssignEmailStr = "";
retStr = retStr +  commerce.setSiteCustomerReadOnlyAttributes()+ commerce.copySiteInfoToBillingInfo() + commerce.settingDefaultsForInitialAndRenewalTermsBasedOnLawsonDivision();

//Order management email setup
if(lower(_system_company_name) == "testrepublicservices" OR lower(_system_company_name) == "devrepublicservices"){
	orderMgmtEmailStr = "approvals.testing@gmail.com";
	quoteAssignEmailStr = "approvals.testing@gmail.com";
}elif(lower(_system_company_name) == "republicservices"){
	orderMgmtEmailStr = "CSA" + division_quote + "@republicservices.com"; // + "," +"dl_bmi_ops_" + division_quote + "@republicservices.com";
	quoteAssignEmailStr = "Assign" + division_quote + "@republicservices.com"; 
}

retStr = retStr + "1~orderManagementEmail_quote~" + orderMgmtEmailStr + "|";
retStr = retStr + "1~quoteAssignedTo_Quote~" + quoteAssignEmailStr + "|";

siteContainershtmlStr = "";
siteContainerstextStr = "";
if(salesActivity_quote == "Existing Customer" OR salesActivity_quote == "Change of Owner"){

	containersInputDict = dict("string");
	put(containersInputDict, "_quote_process_customer_id", _quote_process_customer_id);
	put(containersInputDict, "SiteNumber_quote", SiteNumber_quote);
	put(containersInputDict, "containerGroupForTransaction_quote", containerGroupForTransaction_quote);
	containersOutputDict = util.getContainerGroups(containersInputDict);
	
	if(containskey(containersOutputDict, "html")){
		siteContainershtmlStr = get(containersOutputDict, "html");
	}
	if(containskey(containersOutputDict, "text")){
		siteContainerstextStr = get(containersOutputDict, "text");
	}
}
retStr = retStr + "1" + "~" + "siteContainerGroupsHTMLText_quote" + "~" + siteContainershtmlStr + "|"
	            + "1" + "~" + "siteContainersData_quote" + "~" + siteContainerstextStr + "|";

//Set the reportingStatus_quote
if(_system_current_step_var == "newCustomerAndSite"){
	retStr = retStr + "1~reportingStatus_quote~Created|";
}
elif(_system_current_step_var == "newCustomerAndSite_bmClone_3"){
	retStr = retStr + "1~reportingStatus_quote~Created|";
}
elif(_system_current_step_var == "newCustomerAndSite_bmClone_1"){
	retStr = retStr + "1~reportingStatus_quote~Created|";
}
elif(_system_current_step_var == "newCustomerAndSite_bmClone_2"){
	retStr = retStr + "1~reportingStatus_quote~Created|";
}
elif(_system_current_step_var == "newCustomerAndSite_bmClone_4"){
	retStr = retStr + "1~reportingStatus_quote~Created|";
}
elif(_system_current_step_var == "selectServices"){
	retStr = retStr + "1~reportingStatus_quote~Configured|";
}
elif(_system_current_step_var == "selectServices_bmClone_1"){
	retStr = retStr + "1~reportingStatus_quote~Configured|";
}
elif(_system_current_step_var == "selectServices_bmClone_2"){
	retStr = retStr + "1~reportingStatus_quote~Configured|";
}
elif(_system_current_step_var == "selectServices_bmClone_3"){
	retStr = retStr + "1~reportingStatus_quote~Configured|";
}

retStr = retStr + commerce.feeDefaults();

return retStr;
